package com.galeriafutbol.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.dto.AlbumAdminRequest;
import com.galeriafutbol.api.dto.AlbumAdminResponse;
import com.galeriafutbol.api.dto.AlbumSearchFilter;
import com.galeriafutbol.api.dto.ImageAdminRequest;
import com.galeriafutbol.api.dto.ImageResponse;
import com.galeriafutbol.api.model.AlbumStatus;
import com.galeriafutbol.api.service.AlbumService;
import com.galeriafutbol.api.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/albums")
@Tag(name = "Álbumes (Admin)", description = "Gestión de álbumes para administradores y editores")
public class AdminAlbumController {

    private final AlbumService albumService;
    private final ImageService imageService;

    public AdminAlbumController(AlbumService albumService, ImageService imageService) {
        this.albumService = albumService;
        this.imageService = imageService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Listar álbumes (Admin)", description = "Obtiene una lista paginada de álbumes DRAFT y PUBLISHED. Se puede filtrar por status opcionalmente")
    public Page<AlbumAdminResponse> searchAlbumsForAdmin(AlbumSearchFilter filter, Pageable pageable,
            AlbumStatus status) {
        return albumService.searchAlbumsForAdmin(filter, pageable, status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Obtener álbum (Admin)", description = "Obtiene un álbum para edición con datos administrativos")
    public AlbumAdminResponse getAlbumForAdminById(@PathVariable Long id) {
        return albumService.getAlbumForAdminById(id);
    }

    @PostMapping("/draft")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear borrador", description = "Crea un álbum en estado DRAFT con valores por defecto")
    public AlbumAdminResponse createDraft() {
        return albumService.createDraft();
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Publicar álbum", description = "Actualiza un álbum y cambia su estado a PUBLISHED")
    public AlbumAdminResponse publishAlbum(@PathVariable Long id, @Validated @RequestBody AlbumAdminRequest request) {
        return albumService.publishAlbum(id, request);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear álbum", description = "Crea un nuevo álbum con todos sus datos")
    public AlbumAdminResponse createAlbum(@Validated @RequestBody AlbumAdminRequest request) {
        return albumService.createAlbum(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Actualizar álbum", description = "Reemplaza completamente los datos de un álbum (excepto status)")
    public AlbumAdminResponse updateAlbum(@PathVariable Long id, @Validated @RequestBody AlbumAdminRequest request) {
        return albumService.updateAlbum(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar álbum", description = "Elimina un álbum, su thumbnail y todas sus imágenes")
    public void deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
    }

    @PutMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Reemplazar imágenes", description = "Reemplaza completamente todas las imágenes de un álbum")
    public List<ImageResponse> replaceImages(@PathVariable Long id,
            @Validated @RequestBody List<ImageAdminRequest> requests) {
        return imageService.replaceImagesForAlbum(id, requests);
    }

    @DeleteMapping("/{albumId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen específica de un álbum")
    public void deleteImage(@PathVariable Long albumId, @PathVariable Long imageId) {
        imageService.deleteImage(imageId);
    }

}
