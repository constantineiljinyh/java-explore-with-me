package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.UtilConstants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.SortMode;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventControllerPublic {
    private final EventService eventService;

    @GetMapping()
    public List<EventShortDto> getAllEventPublic(@RequestParam(defaultValue = "") String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = UtilConstants.DATETIME_FORMAT) LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = UtilConstants.DATETIME_FORMAT) LocalDateTime rangeEnd,
                                                 @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                 @RequestParam(defaultValue = "VIEWS") SortMode sort,
                                                 @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                                 @Valid @RequestParam(defaultValue = "10") @Min(1) int size,
                                                 HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }
        log.info("Поступил запрос на поиск событий");
        return eventService.getAllEventPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("{eventId}")
    public EventFullDto getEventByIdPublic(@PathVariable long eventId,
                                           HttpServletRequest request) {
        log.info("Поступил запрос на получение информации о событии с eventId{}", eventId);
        return eventService.getEventByIdPublic(eventId, request);
    }

}
