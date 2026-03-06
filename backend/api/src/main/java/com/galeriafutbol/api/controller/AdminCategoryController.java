package com.galeriafutbol.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.dto.CategoryAdminRequest;
import com.galeriafutbol.api.dto.CategoryAdminResponse;
import com.galeriafutbol.api.model.Category;
import com.galeriafutbol.api.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/categories")
@Tag(name = "Categorías (Admin)", description = "Gestión de categorías para administradores")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/draft")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear borrador", description = "Crea una categoría en estado DRAFT para subir thumbnail")
    public CategoryAdminResponse createDraft() {
        return categoryService.createDraft();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría con código, nombre y thumbnail")
    public Category createCategory(@Validated @RequestBody CategoryAdminRequest request) {
        return categoryService.createCategory(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Reemplaza completamente los datos de una categoría")
    public Category updateCategory(@PathVariable Long id, @Validated @RequestBody CategoryAdminRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría y su thumbnail asociado")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
