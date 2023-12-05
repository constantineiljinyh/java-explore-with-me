package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private int id;

    private Boolean pinned;

    @NotBlank
    private String title;

    private List<EventShortDto> events;
}
