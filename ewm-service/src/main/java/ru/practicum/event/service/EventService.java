package ru.practicum.event.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventRequestStatusUpdateRequest;
import ru.practicum.event.model.EventRequestStatusUpdateResult;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.EventUpdateAdminRequest;
import ru.practicum.event.model.EventUpdateUserRequest;
import ru.practicum.event.model.SortMode;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventByAdmin(List<Long> users,
                                          List<EventState> states,
                                          List<Long> categories,
                                          LocalDateTime start,
                                          LocalDateTime end,
                                          int from,
                                          int size);

    List<EventShortDto> getAllByInitiator(long userId, int from, int size);

    EventFullDto getByIdByInitiator(long userId, long eventId);

    List<ParticipationRequestDto> getParticipationRequestsByInitiator(long userId, long eventId);

    List<EventShortDto> getAllEventPublic(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          LocalDateTime start,
                                          LocalDateTime end,
                                          boolean onlyAvailable,
                                          SortMode sort,
                                          int from,
                                          int size,
                                          HttpServletRequest request);

    EventFullDto getEventByIdPublic(long eventId, HttpServletRequest request);

    EventFullDto createEvent(long userId, EventNewDto eventNewDto);

    EventFullDto updateEventByAdmin(long eventId, EventUpdateAdminRequest updateEventAdminRequest);

    EventFullDto updateEventByInitiator(long userId, long eventId, EventUpdateUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult updateParticipationRequestsByInitiator(@PathVariable long userId,
                                                                          @PathVariable long eventId,
                                                                          EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
