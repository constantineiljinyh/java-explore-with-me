package ru.practicum.participationRequest.service;

import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    List<ParticipationRequestDto> getAllParticipationRequest(long id);

    ParticipationRequestDto createParticipationRequest(long userId, long eventId);

    ParticipationRequestDto updateParticipationRequest(long userId, long requestId);
}
