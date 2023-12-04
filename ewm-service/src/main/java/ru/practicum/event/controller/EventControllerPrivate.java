package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventRequestStatusUpdateRequest;
import ru.practicum.event.model.EventRequestStatusUpdateResult;
import ru.practicum.event.model.EventUpdateUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventControllerPrivate {
    private final EventService eventService;

    @GetMapping()
    public List<EventShortDto> getAllByInitiator(@PathVariable long userId,
                                                 @Valid @PositiveOrZero @RequestParam(defaultValue = "0") @Min(0) int from,
                                                 @Valid @Positive @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Поступил запрос на получение событий текущего пользователя с id{}", userId);
        return eventService.getAllByInitiator(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByIdByInitiator(@PathVariable long userId,
                                           @PathVariable long eventId) {
        log.info("Поступил запрос на получение полной информации о событии текущего пользователя с id{}", userId);
        return eventService.getByIdByInitiator(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsByInitiator(@PathVariable long userId,
                                                                             @PathVariable long eventId) {
        log.info("Поступил запрос на получение информации о запросах на участие в событии текущего пользователя с id{}", userId);
        return eventService.getParticipationRequestsByInitiator(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId,
                                    @Valid @RequestBody EventNewDto eventNewDto) {
        log.info("Поступил запрос на добавление события пользователем с id{}", userId);
        return eventService.createEvent(userId, eventNewDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByInitiator(@PathVariable long userId,
                                               @PathVariable long eventId,
                                               @Valid @RequestBody EventUpdateUserRequest updateEventUserRequest) {
        log.info("Поступил запрос на обновление события текущего пользователя с id{}", userId);
        return eventService.updateEventByInitiator(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequestsByInitiator(@PathVariable long userId,
                                                                                 @PathVariable long eventId,
                                                                                 @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Поступил запрос на обновление статуса события текущего пользователя с id{}", userId);
        return eventService.updateParticipationRequestsByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }
}