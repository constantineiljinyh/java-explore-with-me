package ru.practicum.model;

import org.mapstruct.Mapper;
import ru.practicum.EndpointHitDto;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);
}