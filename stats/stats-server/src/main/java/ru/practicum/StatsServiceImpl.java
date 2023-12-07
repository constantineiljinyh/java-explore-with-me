package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository endpointHitRepository;

    private final EndpointHitMapper endpointHitMapper;

    private final ViewStatsMapper viewStatsMapper;

    @Override
    @Transactional
    public EndpointHitDto saveEndpointHit(EndpointHitDto endpointHit, UriComponentsBuilder uriComponentsBuilder) {
        EndpointHit endpointHitsSave = endpointHitRepository.save(endpointHitMapper.toEndpointHit(endpointHit));
        return endpointHitMapper.toEndpointHitDto(endpointHitsSave);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null) {
            log.debug("Ошибка валидации: Время старта или окончания не заданы. Время старта: {}, Время окончания: {}", start, end);
            throw new ValidationException("Время старта и окончания должны быть заполнены.");
        }

        if (start.isAfter(end)) {
            log.debug("Ошибка валидации: Дата начала ({}), позже даты окончания ({}).", start, end);
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        List<ViewStats> result;
        if (unique) {
            result = endpointHitRepository.findUniqueStatistics(start, end, uris);
        } else {
            result = endpointHitRepository.findStatistics(start, end, uris);
        }

        return viewStatsMapper.toViewStatsDtoList(result);
    }
}
