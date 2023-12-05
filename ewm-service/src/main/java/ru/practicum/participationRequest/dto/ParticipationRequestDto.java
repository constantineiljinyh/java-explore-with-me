package ru.practicum.participationRequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.participationRequest.model.ParticipationRequestState;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.event.UtilConstants.DATETIME_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {

    private Long id;

    @NotNull
    private Long requester;

    @NotNull
    private Long event;

    @NotNull
    private ParticipationRequestState status;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    private LocalDateTime created;
}
