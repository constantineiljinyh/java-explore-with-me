package ru.practicum.participationRequest.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequest(long id) {
        findUserById(id);
        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllByRequesterId(id);
        return participationRequests.stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(long userId, long eventId) {
        User requestor = findUserById(userId);
        Event event = findEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может подать запрос на участие в своем собственном событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getParticipantLimit() > 0) {
            if (event.getParticipantLimit() <= participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED)) {
                throw new ConflictException("Количество запросов на участие превысило лимит для данного события.");
            }
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(requestor);
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(event.getRequestModeration() && !event.getParticipantLimit().equals(0L) ? ParticipationRequestState.PENDING : ParticipationRequestState.CONFIRMED);

        return participationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateParticipationRequest(long userId, long requestId) {
        findUserById(userId);
        ParticipationRequest participationRequest = findParticipationRequestById(requestId);

        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Запрос на участие не найден для редактирования");
        }

        participationRequest.setStatus(ParticipationRequestState.CANCELED);

        return participationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    private ParticipationRequest findParticipationRequestById(long id) {
        return participationRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос на участие с таким id=" + id + " не найден."));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Юзер с таким id=" + id + " не найден."));
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ивента с таким id=" + id + " не найдено."));
    }
}
