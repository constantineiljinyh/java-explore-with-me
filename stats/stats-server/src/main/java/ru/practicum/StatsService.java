package ru.practicum;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto saveEndpointHit(EndpointHitDto endpointHit, UriComponentsBuilder uriComponentsBuilder);

    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
