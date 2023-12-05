package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Slf4j
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody CompilationNewDto compilationNewDto) {
        log.info("Поступил запрос на создание новой подборки");
        return compilationService.createCompilation(compilationNewDto);
    }

    @PatchMapping("/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable long comId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Поступил запрос на обновления информации подборки id{}", comId);
        return compilationService.updateCompilation(comId, updateCompilationRequest);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long comId) {
        log.info("Поступил запрос на удаление подборки id{}", comId);
        compilationService.deleteCompilation(comId);
    }
}
