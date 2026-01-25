package com.galeriafutbol.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.model.Category;
import com.galeriafutbol.api.model.TeamType;
import com.galeriafutbol.api.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categorías", description = "Endpoints públicos para categorías")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Obtiene todas las categorías, opcionalmente filtradas por tipo de equipo")
    public List<Category> getCategories(
            @RequestParam(required = false) TeamType teamType) {
        return categoryService.getCategories(teamType);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Obtener categoría por código", description = "Obtiene una categoría específica por su código único")
    public Category getCategoryByCode(@PathVariable String code) {
        return categoryService.getCategoryByCode(code);
    }
}
