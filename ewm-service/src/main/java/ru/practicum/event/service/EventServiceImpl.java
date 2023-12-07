package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.UtilConstants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.location.Location;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.location.LocationMapper;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.EventRequestStatusUpdateRequest;
import ru.practicum.event.model.EventRequestStatusUpdateResult;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.EventUpdateAdminRequest;
import ru.practicum.event.model.EventUpdateUserRequest;
import ru.practicum.event.model.SortMode;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.participationRequest.dto.ParticipationRequestMapper;
import ru.practicum.participationRequest.model.ParticipationRequest;
import ru.practicum.participationRequest.model.ParticipationRequestState;
import ru.practicum.participationRequest.repository.ParticipationRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final ParticipationRequestRepository participationRequestRepository;

    private StatsClient statsClient;

    private final EventMapper eventMapper;

    private final ParticipationRequestMapper participationRequestMapper;

    private final LocationMapper locationMapper;

    @Value("${STAT_SERVER_URL:http://stats-server:9090}")
    private String statClientUrl;

    @PostConstruct
    private void init() {
        statsClient = new StatsClient(new RestTemplate(), statClientUrl);
    }

    @Override
    public List<EventFullDto> getAllEventByAdmin(List<Long> users,
                                                 List<EventState> states,
                                                 List<Long> categories,
                                                 LocalDateTime start,
                                                 LocalDateTime end,
                                                 int from,
                                                 int size) {
        Pageable pageable = PageRequest.of(from, size);

        if (users != null && users.size() == 1 && users.get(0).equals(0L)) {
            users = null;
        }

        if (categories != null && categories.size() == 1 && categories.get(0).equals(0L)) {
            categories = null;
        }

        if (start == null) {
            start = LocalDateTime.now();
        }

        if (end == null) {
            end = UtilConstants.getMaxDateTime();
        }

        Page<Event> page = eventRepository.findAllByAdmin(users, states, categories, start, end, pageable);

        List<String> eventUrls = page.getContent().stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStatsDto> viewStatsDto = statsClient.getStats(
                start.format(UtilConstants.getDefaultDateTimeFormatter()),
                end.format(UtilConstants.getDefaultDateTimeFormatter()), eventUrls, true);

        return page.getContent().stream()
                .map(eventMapper::toEventFullDto)
                .peek(dto -> {
                    Optional<ViewStatsDto> matchingStats = viewStatsDto.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStatsDto::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(
                        participationRequestRepository.countByEventIdAndStatus(dto.getId(),
                                ParticipationRequestState.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllByInitiator(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByIdByInitiator(long userId, long eventId) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByInitiator(long userId, long eventId) {
        findUserById(userId);
        findEventById(eventId);

        return participationRequestRepository.findAllByEventId(eventId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllEventPublic(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 LocalDateTime start,
                                                 LocalDateTime end,
                                                 boolean onlyAvailable,
                                                 SortMode sort,
                                                 int from,
                                                 int size,
                                                 HttpServletRequest request) {

        statsClient.sendHit(EndpointHitDto.builder()
                .app("ewm")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        if (categories != null && categories.size() == 1 && categories.get(0).equals(0L)) {
            categories = null;
        }

        if (start == null) {
            start = LocalDateTime.now();
        }

        if (end == null) {
            end = UtilConstants.getMaxDateTime();
        }

        List<Event> eventList = eventRepository.getAllPublic(text, categories, paid, start, end);

        if (onlyAvailable) {
            eventList = eventList.stream()
                    .filter(event -> event.getParticipantLimit().equals(0L)
                            || event.getParticipantLimit() <
                            participationRequestRepository.countByEventIdAndStatus(event.getId(),
                                    ParticipationRequestState.CONFIRMED))
                    .collect(Collectors.toList());
        }

        List<String> eventUrls = eventList.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStatsDto> viewStatsDto = statsClient.getStats(
                start.format(UtilConstants.getDefaultDateTimeFormatter()),
                end.format(UtilConstants.getDefaultDateTimeFormatter()), eventUrls, true);

        List<EventShortDto> eventShortDtoList = eventList.stream()
                .map(eventMapper::toEventShortDto)
                .peek(dto -> {
                    Optional<ViewStatsDto> matchingStats = viewStatsDto.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStatsDto::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(
                        participationRequestRepository.countByEventIdAndStatus(dto.getId(),
                                ParticipationRequestState.CONFIRMED)))
                .collect(Collectors.toList());

        switch (sort) {
            case EVENT_DATE:
                Collections.sort(eventShortDtoList, Comparator.comparing(EventShortDto::getEventDate));
                break;
            case VIEWS:
                Collections.sort(eventShortDtoList, Comparator.comparing(EventShortDto::getViews).reversed());
                break;
        }

        if (from >= eventShortDtoList.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(from + size, eventShortDtoList.size());
        return eventShortDtoList.subList(from, toIndex);
    }

    @Override
    public EventFullDto getEventByIdPublic(long eventId, HttpServletRequest request) {
        Event event = findEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.debug("Событие не найдено: Не удалось найти событие с id={}", eventId);
            throw new NotFoundException("События с таким id=" + eventId + " не найдено");
        }

        statsClient.sendHit(EndpointHitDto.builder()
                .app("ewm")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<String> eventUrls = Collections.singletonList("/events/" + event.getId());

        List<ViewStatsDto> viewStatsDto = statsClient.getStats(
                UtilConstants.getMinDateTime().format(UtilConstants.getDefaultDateTimeFormatter()),
                UtilConstants.getMaxDateTime().plusYears(1).format(
                        UtilConstants.getDefaultDateTimeFormatter()), eventUrls, true);

        EventFullDto dto = eventMapper.toEventFullDto(event);
        dto.setViews(viewStatsDto.isEmpty() ? 1 : viewStatsDto.get(0).getHits());
        dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(),
                ParticipationRequestState.CONFIRMED));

        return dto;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, EventNewDto eventNewDto) {
        if (LocalDateTime.now().plusHours(2).isAfter(eventNewDto.getEventDate())) {
            log.debug("Конфликт: Дата события должна быть через 2 часа после текущего времени или позже." +
                    " Текущее время: {}, Время события: {}", LocalDateTime.now(), eventNewDto.getEventDate());
            throw new ConflictException("Дата события должна быть через 2 часа после текущего времени или позже.");
        }

        User user = findUserById(userId);
        Category category = findCategoryById(eventNewDto.getCategory());
        Location location = handleLocationDto(eventNewDto.getLocation());

        Event event = eventMapper.toEvent(eventNewDto, category, location);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (eventNewDto.getPaid() == null) {
            event.setPaid(false);
        }

        if (eventNewDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }

        if (eventNewDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(long eventId, EventUpdateAdminRequest updateEventAdminRequest) {
        Event event = findEventById(eventId);

        if (updateEventAdminRequest.getEventDate() != null
                && LocalDateTime.now().plusHours(1).isAfter(updateEventAdminRequest.getEventDate())) {
            log.debug("Конфликт: Дата события должна быть через 1 час после текущего времени или позже." +
                            " Текущее время: {}, Время события: {}",
                    LocalDateTime.now(), updateEventAdminRequest.getEventDate());
            throw new ConflictException("Дата события должна быть через 1 час после текущего времени или позже.");

        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(EventUpdateAdminRequest.StateAction.PUBLISH_EVENT) &&
                    !event.getState().equals(EventState.PENDING)) {
                log.debug("Конфликт: Cобытие можно публиковать, только если оно в состоянии ожидания публикации." +
                        " Текущее состояние: {}", event.getState());
                throw new ConflictException("Cобытие можно публиковать, только если оно в состоянии ожидания публикации: "
                        + event.getState());
            }

            if (updateEventAdminRequest.getStateAction().equals(EventUpdateAdminRequest.StateAction.REJECT_EVENT) &&
                    event.getState().equals(EventState.PUBLISHED)) {
                log.debug("Конфликт: Cобытие можно отклонить, только если оно еще не опубликовано." +
                        " Текущее состояние: {}", event.getState());
                throw new ConflictException("Cобытие можно отклонить, только если оно еще не опубликовано: " +
                        event.getState());
            }
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(handleLocationDto(updateEventAdminRequest.getLocation()));
        }

        Optional.ofNullable(updateEventAdminRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventAdminRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventAdminRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventAdminRequest.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventAdminRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventAdminRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventAdminRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    log.debug("Неизвестный статус: {}", updateEventAdminRequest.getStateAction());
                    throw new IllegalArgumentException("Неизвестный статус: " + updateEventAdminRequest.getStateAction());
            }
        }

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByInitiator(long userId, long eventId, EventUpdateUserRequest updateEventUserRequest) {
        Event event = findEventById(eventId);
        findUserById(userId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        if (updateEventUserRequest.getEventDate() != null &&
                LocalDateTime.now().plusHours(2).isAfter(updateEventUserRequest.getEventDate())) {
            log.debug("Конфликт: Дата события должна быть через 2 часа после текущего времени или позже." +
                            " Текущее время: {}, Время события: {}",
                    LocalDateTime.now(), updateEventUserRequest.getEventDate());
            throw new ConflictException("Дата события должна быть через 2 часа после текущего времени или позже.");
        }

        if (!(event.getState().equals(EventState.CANCELED) ||
                event.getState().equals(EventState.PENDING))) {
            log.debug("Конфликт: Можно изменять только отложенные или отмененные события." +
                    " Текущее состояние: {}", event.getState());
            throw new ConflictException("Можно изменить только отложенные или отмененные события.");
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(handleLocationDto(updateEventUserRequest.getLocation()));
        }

        Optional.ofNullable(updateEventUserRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventUserRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventUserRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventUserRequest.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventUserRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventUserRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    log.debug("Неизвестный статус: {}", updateEventUserRequest.getStateAction());
                    throw new IllegalArgumentException("Неизвестный статус: " + updateEventUserRequest.getStateAction());
            }
        }

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequestsByInitiator(@PathVariable long userId,
                                                                                 @PathVariable long eventId,
                                                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        findUserById(userId);
        Event event = findEventById(eventId);

        long confirmLimit = event.getParticipantLimit() - participationRequestRepository.countByEventIdAndStatus(
                eventId, ParticipationRequestState.CONFIRMED);

        if (confirmLimit <= 0) {
            log.debug("Конфликт: Лимит участников достигнут. Текущий лимит: {}", confirmLimit);
            throw new ConflictException("Лимит участников достигнут");
        }

        List<ParticipationRequest> requestList = participationRequestRepository.findAllByIdIn(
                eventRequestStatusUpdateRequest.getRequestIds());

        List<Long> notFoundIds = eventRequestStatusUpdateRequest.getRequestIds().stream()
                .filter(requestId -> requestList.stream().noneMatch(
                        request -> request.getId().equals(requestId)))
                .collect(Collectors.toList());

        if (!notFoundIds.isEmpty()) {
            log.debug("Заявка на участие не найдена: {}", notFoundIds);
            throw new NotFoundException("Заявка на участие с id= " + notFoundIds + " не найдена");
        }

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        for (ParticipationRequest req : requestList) {
            if (!req.getEvent().getId().equals(eventId)) {
                log.debug("Заявка на участие не найдена: {}", req.getId());
                throw new NotFoundException("Заявка на участие с id= " + req.getId() + " не найдена");
            }

            if (confirmLimit <= 0) {
                req.setStatus(ParticipationRequestState.REJECTED);
                result.getRejectedRequests().add(participationRequestMapper.toParticipationRequestDto(req));
                continue;
            }

            switch (eventRequestStatusUpdateRequest.getStatus()) {
                case CONFIRMED:
                    req.setStatus(ParticipationRequestState.CONFIRMED);
                    result.getConfirmedRequests().add(participationRequestMapper.toParticipationRequestDto(req));
                    confirmLimit--;
                    break;
                case REJECTED:
                    req.setStatus(ParticipationRequestState.REJECTED);
                    result.getRejectedRequests().add(participationRequestMapper.toParticipationRequestDto(req));
                    break;
                default:
                    log.debug("Неизвестный статус: {}", eventRequestStatusUpdateRequest.getStatus());
                    throw new IllegalArgumentException("Неизвестный статус: " + eventRequestStatusUpdateRequest.getStatus());
            }
        }
        return result;
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Событие не найдено: Не удалось найти событие с id={}", id);
                    return new NotFoundException("События с таким id=" + id + " не найдено");
                });
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: Не удалось найти пользователя с id={}", id);
                    return new NotFoundException("Пользователя с таким id=" + id + " не найдено");
                });
    }

    private Category findCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Категория не найдена: Не удалось найти категорию с id={}", id);
                    return new NotFoundException("Категории с таким id=" + id + " не найдено.");
                });
    }

    private void checkInitiator(long userId, long eventId, long initiatorId) {
        if (userId != initiatorId) {
            log.error("Недостаточно прав: Пользователь с id={} не является инициатором события с id={}", userId, eventId);
            throw new NotFoundException("Вы не являетесь инициатором события с id=" + eventId);
        }
    }

    private Location handleLocationDto(LocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return location != null ? location : locationRepository.save(locationMapper.toLocation(locationDto));
    }
}


