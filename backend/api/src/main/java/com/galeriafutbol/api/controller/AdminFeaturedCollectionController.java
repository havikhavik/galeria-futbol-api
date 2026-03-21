package com.galeriafutbol.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.dto.FeaturedCollectionAdminRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionAdminResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionPartialRequest;
import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.service.FeaturedCollectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/featured-collections")
@Tag(name = "Promociones (Admin)", description = "Gestión de promociones para administradores")
public class AdminFeaturedCollectionController {

    private final FeaturedCollectionService featuredCollectionService;

    public AdminFeaturedCollectionController(FeaturedCollectionService featuredCollectionService) {
        this.featuredCollectionService = featuredCollectionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Listar promociones/colecciones (Admin)", description = "Obtiene promociones/colecciones activas e inactivas para administración")
    public List<FeaturedCollectionAdminResponse> getAllForAdmin() {
        return featuredCollectionService.getAllForAdmin();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Obtener promoción/colección (Admin)", description = "Obtiene una promoción/colección por ID para edición")
    public FeaturedCollectionAdminResponse getByIdForAdmin(@PathVariable Long id) {
        return featuredCollectionService.getByIdForAdmin(id);
    }

    @GetMapping("/{id}/albums")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Listar álbumes asociados (Admin)", description = "Obtiene los álbumes asociados a una colección, ordenados por displayOrder")
    public List<AlbumPublicResponse> getAlbumsForAdmin(@PathVariable Long id) {
        return featuredCollectionService.getAlbumsForAdmin(id);
    }

    @PostMapping("/draft")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear borrador", description = "Crea una promoción en estado DRAFT para subir banner")
    public FeaturedCollectionAdminResponse createDraft() {
        return featuredCollectionService.createDraft();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear promocion/colección destacada", description = "Crea una nueva promocion/colección destacada con todos los campos requeridos")
    public FeaturedCollectionAdminResponse createFeaturedCollection(
            @Validated @RequestBody FeaturedCollectionAdminRequest request) {
        return featuredCollectionService.createFeaturedCollection(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Actualizar promocion/colección destacada (parcial)", description = "Actualiza solo los campos enviados, sin afectar los demás")
    public FeaturedCollectionAdminResponse updateFeaturedCollection(@PathVariable Long id,
            @RequestBody FeaturedCollectionPartialRequest request) {
        return featuredCollectionService.partialUpdate(id, request);
    }

    @PostMapping("/{id}/albums/{albumId}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Asociar álbum a colección", description = "Asocia un álbum existente a la colección destacada")
    public void addAlbumToCollection(@PathVariable Long id, @PathVariable Long albumId) {
        featuredCollectionService.addAlbumToCollection(id, albumId);
    }

    @DeleteMapping("/{id}/albums/{albumId}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar álbum de colección", description = "Quita la asociación de un álbum de la colección destacada")
    public void removeAlbumFromCollection(@PathVariable Long id, @PathVariable Long albumId) {
        featuredCollectionService.removeAlbumFromCollection(id, albumId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar promocion/colección destacada", description = "Elimina una promocion/colección destacada y sus recursos asociados")
    public void deleteFeaturedCollection(@PathVariable Long id) {
        featuredCollectionService.deleteFeaturedCollection(id);
    }
}
