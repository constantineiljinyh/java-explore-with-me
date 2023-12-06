package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category categorySave = categoryRepository.save(categoryMapper.toCategory(categoryDto));
        return categoryMapper.toCategoryDto(categorySave);
    }

    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.isEmpty() ? Collections.emptyList() :
                categories.stream()
                        .map(categoryMapper::toCategoryDto)
                        .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto updateCategory(long id, CategoryDto categoryDto) {
        Category category = findById(id);
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        return categoryMapper.toCategoryDto(category);
    }

    public CategoryDto getCategoryById(long id) {
        return categoryMapper.toCategoryDto(findById(id));
    }

    @Transactional
    public CategoryDto deleteCategory(long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
        return categoryMapper.toCategoryDto(category);
    }

    private Category findById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категории с таким id= " + id + " не найдено."));
    }

}
