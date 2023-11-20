package ru.practicum.model;

import org.mapstruct.Mapper;
import ru.practicum.ViewStatsDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStats toViewStats(ViewStatsDto viewStatsDto);

    ViewStatsDto toViewStatsDto(ViewStats viewStats);

    List<ViewStatsDto> toViewStatsDtoList(List<ViewStats> viewStatsList);

}