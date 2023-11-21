package ru.practicum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    ResponseEntity<?> saveEndpointHit(EndpointHitDto endpointHit, UriComponentsBuilder uriComponentsBuilder);

    ResponseEntity<?> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
