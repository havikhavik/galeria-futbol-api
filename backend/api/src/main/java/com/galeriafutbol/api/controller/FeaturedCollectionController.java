package com.galeriafutbol.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.dto.FeaturedCollectionResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionWithAlbumsResponse;
import com.galeriafutbol.api.service.FeaturedCollectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/featured")
@Tag(name = "Promociones", description = "Endpoints públicos para promociones/colecciones destacadas")
public class FeaturedCollectionController {

    private final FeaturedCollectionService featuredCollectionService;

    public FeaturedCollectionController(FeaturedCollectionService featuredCollectionService) {
        this.featuredCollectionService = featuredCollectionService;
    }

    @GetMapping
    @Operation(summary = "Listar promociones/colecciones activas", description = "Obtiene todas las promociones/colecciones destacadas activas ordenadas por prioridad")
    public List<FeaturedCollectionResponse> getAllActive() {
        return featuredCollectionService.getAllActive();
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Obtener promocion/colección por slug", description = "Obtiene una promocion/colección destacada con todos sus álbumes asociados")
    public FeaturedCollectionWithAlbumsResponse getBySlug(@PathVariable String slug) {
        return featuredCollectionService.getBySlug(slug);
    }
}
