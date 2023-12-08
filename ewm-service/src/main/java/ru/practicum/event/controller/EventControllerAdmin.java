package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.UtilConstants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.EventUpdateAdminRequest;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping()
    public List<EventFullDto> getAllEventByAdmin(@RequestParam(required = false) List<Long> users,
                                                 @RequestParam(required = false) List<EventState> states,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = UtilConstants.DATETIME_FORMAT) LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = UtilConstants.DATETIME_FORMAT) LocalDateTime rangeEnd,
                                                 @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил запрос на поиск событий ");
        return eventService.getAllEventByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable long eventId,
                                           @Valid @RequestBody EventUpdateAdminRequest updateEventAdminRequest) {
        log.info("Поступил запрос на редактирование события id{}", eventId);
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}