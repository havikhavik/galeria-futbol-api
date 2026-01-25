package com.galeriafutbol.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.dto.AlbumSearchFilter;
import com.galeriafutbol.api.dto.ImageResponse;
import com.galeriafutbol.api.service.AlbumService;
import com.galeriafutbol.api.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/albums")
@Tag(name = "Álbumes", description = "Endpoints públicos para álbumes")
public class AlbumController {

    private final AlbumService albumService;
    private final ImageService imageService;

    public AlbumController(AlbumService albumService, ImageService imageService) {
        this.albumService = albumService;
        this.imageService = imageService;
    }

    @GetMapping
    @Operation(summary = "Listar álbumes", description = "Obtiene una lista paginada de álbumes publicados")
    public Page<AlbumPublicResponse> searchAlbums(AlbumSearchFilter filter, Pageable pageable) {
        return albumService.searchAlbums(filter, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener álbum por ID", description = "Obtiene los detalles de un álbum específico")
    public AlbumPublicResponse getAlbumById(@PathVariable Long id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("/{id}/images")
    @Operation(summary = "Obtener imágenes del álbum", description = "Obtiene todas las imágenes de un álbum")
    public List<ImageResponse> getImagesForAlbum(@PathVariable Long id) {
        return imageService.getImagesForAlbum(id);
    }
}
