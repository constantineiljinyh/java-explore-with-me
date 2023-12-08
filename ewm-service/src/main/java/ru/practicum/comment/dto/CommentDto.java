package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.event.UtilConstants.DATETIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank
    private String userName;

    @NotBlank
    private String text;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    private LocalDateTime createdOn;
}