package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitMapper;
import ru.practicum.model.ViewStats;
import ru.practicum.model.ViewStatsMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository endpointHitRepository;

    private final EndpointHitMapper endpointHitMapper;

    private final ViewStatsMapper viewStatsMapper;

    @Transactional
    public ResponseEntity<?> saveEndpointHit(EndpointHitDto endpointHit, UriComponentsBuilder uriComponentsBuilder) {
        EndpointHit endpointHitsSave = endpointHitRepository.save(endpointHitMapper.toEndpointHit(endpointHit));
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/hit/{hitId}")
                        .build(Map.of("hitId", endpointHitsSave.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitsSave);
    }

    public ResponseEntity<?> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }
        List<ViewStats> result;
        if (unique) {
            result = endpointHitRepository.findUniqueStatistics(start, end, uris);
        } else {
            result = endpointHitRepository.findStatistics(start, end, uris);
        }

        List<ViewStatsDto> dtoList = viewStatsMapper.toViewStatsDtoList(result);
        log.info("Выводим результат {}", result);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

}
