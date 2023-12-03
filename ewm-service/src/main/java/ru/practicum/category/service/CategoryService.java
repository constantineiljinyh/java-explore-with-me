package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto updateCategory(long id, CategoryDto categoryDto);

    CategoryDto getCategoryById(long id);

    CategoryDto deleteCategory(long id);

}
