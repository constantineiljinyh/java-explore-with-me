package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@AllArgsConstructor
@Slf4j
public class CompilationControllerPublic {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAllCompilation(@RequestParam(required = false) Boolean pinned,
                                                  @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил запрос на получение подборок событий");
        return compilationService.getAllCompilation(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getByIdCompilation(@PathVariable long compId) {
        log.info("Поступил запрос на получение подборок событий по id{} ", compId);
        return compilationService.getByIdCompilation(compId);
    }
}
