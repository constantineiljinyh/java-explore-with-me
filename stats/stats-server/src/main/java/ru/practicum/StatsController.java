package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<?> postHit(@RequestBody @Valid EndpointHitDto hitDto, UriComponentsBuilder uriComponentsBuilder) {
        log.info("Поступил запрос на создание Hit {}", hitDto);
        return statsService.saveEndpointHit(hitDto, uriComponentsBuilder);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                      @RequestParam(required = false) List<String> uris,
                                      @RequestParam(defaultValue = "false", required = false) boolean unique) {
        log.info("Поступил запрос на просмотр со start {} и end {}", start, end);
        return statsService.getStatistics(start, end, uris, unique);
    }
}
