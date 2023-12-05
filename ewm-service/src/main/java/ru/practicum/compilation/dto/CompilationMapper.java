package ru.practicum.compilation.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation toCompilation(CompilationNewDto dto, List<Event> events);

    CompilationDto toCompilationDto(Compilation compilation);
}
