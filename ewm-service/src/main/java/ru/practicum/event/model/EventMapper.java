package ru.practicum.event.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.location.Location;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    Event toEvent(EventNewDto eventNewDto, Category category, Location location);

    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
