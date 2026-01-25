package com.galeriafutbol.api.service;

import java.util.List;

import com.galeriafutbol.api.dto.CategoryAdminRequest;
import com.galeriafutbol.api.model.Category;
import com.galeriafutbol.api.model.TeamType;

public interface CategoryService {

    Category createCategory(CategoryAdminRequest request);

    Category updateCategory(Long id, CategoryAdminRequest request);

    List<Category> getCategories(TeamType teamType);

    Category getCategoryByCode(String code);

    void deleteCategory(Long id);
}
