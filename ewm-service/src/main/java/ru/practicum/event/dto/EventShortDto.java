package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.event.UtilConstants.DATETIME_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {

    @NotBlank
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    private LocalDateTime eventDate;

    private Long id;

    @NotNull
    private UserShortDto initiator;

    private Boolean paid;

    @NotBlank
    private String title;

    private Long views;
}
