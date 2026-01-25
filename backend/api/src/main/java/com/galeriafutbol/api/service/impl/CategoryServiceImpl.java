package com.galeriafutbol.api.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.galeriafutbol.api.dto.CategoryAdminRequest;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.model.Category;
import com.galeriafutbol.api.model.TeamType;
import com.galeriafutbol.api.repository.CategoryRepository;
import com.galeriafutbol.api.service.CategoryService;
import com.galeriafutbol.api.service.ImageStorageService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ImageStorageService imageStorageService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ImageStorageService imageStorageService) {
        this.categoryRepository = categoryRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public Category createCategory(CategoryAdminRequest request) {
        Category category = new Category();
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setThumbnail(request.getThumbnail());
        category.setCreatedAt(OffsetDateTime.now());

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryAdminRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));

        String oldThumbnail = category.getThumbnail();
        String newThumbnail = request.getThumbnail();
        if (oldThumbnail != null && !oldThumbnail.isBlank() && !oldThumbnail.equals(newThumbnail)) {
            imageStorageService.delete(oldThumbnail);
        }

        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setThumbnail(newThumbnail);

        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getCategories(TeamType teamType) {
        if (teamType != null) {
            return categoryRepository.findByTeamType(teamType);
        }
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryByCode(String code) {
        return categoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + code));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        if (category.getThumbnail() != null && !category.getThumbnail().isBlank()) {
            imageStorageService.delete(category.getThumbnail());
        }

        categoryRepository.delete(category);
    }
}
