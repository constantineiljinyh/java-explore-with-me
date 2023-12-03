package ru.practicum.participationRequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.participationRequest.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestControllerPrivate {
    private final ParticipationRequestService participationRequestService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllParticipationRequest(@PathVariable long userId) {
        log.info("Поступил запрос на получение информации о заявках текущего пользователя на участие в событиях userId{}", userId);
        return participationRequestService.getAllParticipationRequest(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable long userId,
                                                              @RequestParam long eventId) {
        log.info("Поступил запрос на добавление заявки пользователя в событии userId{}", userId);
        return participationRequestService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto updateParticipationRequest(@PathVariable long userId,
                                                              @PathVariable long requestId) {
        log.info("Поступил запрос на отмену заявки пользователя в событии userId{}", userId);
        return participationRequestService.updateParticipationRequest(userId, requestId);
    }
}