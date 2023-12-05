package ru.practicum.participationRequest.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.participationRequest.model.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "requester", source = "participationRequest.requester.id")
    @Mapping(target = "event", source = "participationRequest.event.id")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

}
