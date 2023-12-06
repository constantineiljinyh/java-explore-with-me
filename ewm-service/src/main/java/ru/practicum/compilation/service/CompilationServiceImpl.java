package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getAllCompilation(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);

        Page<Compilation> compilations = compilationRepository.findAllByPublic(pinned, pageable);

        return compilations.isEmpty() ? Collections.emptyList() :
                compilations.getContent().stream()
                        .map(compilationMapper::toCompilationDto)
                        .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getByIdCompilation(long id) {
        return compilationMapper.toCompilationDto(findCompilationById(id));
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(CompilationNewDto compilationNewDto) {
        List<Event> events = getEventsFromDto(compilationNewDto);

        if (compilationNewDto.getPinned() == null) {
            compilationNewDto.setPinned(false);
        }

        Compilation savedCompilation = compilationRepository.save(
                compilationMapper.toCompilation(compilationNewDto, events));

        return compilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long id, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = findCompilationById(id);

        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(updateCompilationRequest.getEvents()));
        }

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(long id) {
        Compilation compilation = findCompilationById(id);

        compilationRepository.delete(compilation);
    }

    List<Event> getEventsFromDto(CompilationNewDto compilationNewDto) {
        return Optional.ofNullable(compilationNewDto.getEvents())
                .map(eventRepository::findAllById)
                .orElse(new ArrayList<>());
    }

    private Compilation findCompilationById(long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборки с таким id= " + id + " не найдено."));
    }
}
