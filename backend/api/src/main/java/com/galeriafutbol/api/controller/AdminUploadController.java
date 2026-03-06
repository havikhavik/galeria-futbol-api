package com.galeriafutbol.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.galeriafutbol.api.dto.UploadImageResponse;
import com.galeriafutbol.api.service.UploadImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/upload")
@Tag(name = "Upload (Admin)", description = "Subida de imágenes y thumbnails para administradores")
@Validated
public class AdminUploadController {

    private final UploadImageService uploadImageService;

    public AdminUploadController(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    @PostMapping("/images/{albumId}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subir imagen a un álbum", description = "Sube una imagen y la asocia al álbum especificado")
    public UploadImageResponse uploadImage(
            @PathVariable Long albumId,
            @RequestParam("file") MultipartFile file) {
        return uploadImageService.uploadForAdmin(file, albumId);
    }

    @PostMapping("/category-thumbnail/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subir thumbnail de categoría", description = "Sube el thumbnail para una categoría específica")
    public UploadImageResponse uploadCategoryThumbnail(
            @PathVariable Long categoryId,
            @RequestParam("file") MultipartFile file) {
        return uploadImageService.uploadForCategory(file, categoryId);
    }

    @PostMapping("/featured-banner/{featuredCollectionId}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subir banner de colección destacada", description = "Sube el banner para una colección destacada específica")
    public UploadImageResponse uploadFeaturedBanner(
            @PathVariable Long featuredCollectionId,
            @RequestParam("file") MultipartFile file) {
        return uploadImageService.uploadForFeaturedBanner(file, featuredCollectionId);
    }
}
