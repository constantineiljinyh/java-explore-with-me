package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
@Slf4j
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories(@Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил запрос на получение списка категорий ");
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable long catId) {
        log.info("Поступил запрос на получение категории с id{}", catId);
        return categoryService.getCategoryById(catId);
    }
}
