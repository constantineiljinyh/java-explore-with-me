package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilation(Boolean pinned, int from, int size);

    CompilationDto getByIdCompilation(long id);

    CompilationDto createCompilation(CompilationNewDto compilationNewDto);

    CompilationDto updateCompilation(long id, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilation(long id);

}
