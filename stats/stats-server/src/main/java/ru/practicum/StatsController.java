package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.practicum.DateTimeFormatConstants.DATE_TIME_FORMAT;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveEndpointHit(@RequestBody @Valid EndpointHitDto hitDto, UriComponentsBuilder uriComponentsBuilder) {
        log.info("Поступил запрос на создание Hit {}", hitDto);
        EndpointHitDto savedHit = statsService.saveEndpointHit(hitDto, uriComponentsBuilder);
        return ResponseEntity.created(uriComponentsBuilder.path("/hit/{hitId}").build(Map.of("hitId", savedHit.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedHit);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStatistics(@RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                                            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                                            @RequestParam(required = false) List<String> uris,
                                                            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Поступил запрос на просмотр со start {} и end {}", start, end);
        List<ViewStatsDto> result = statsService.getStatistics(start, end, uris, unique);
        return ResponseEntity.ok(result);
    }
}
