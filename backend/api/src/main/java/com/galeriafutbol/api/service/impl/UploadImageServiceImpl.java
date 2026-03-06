package com.galeriafutbol.api.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.galeriafutbol.api.dto.UploadImageResponse;
import com.galeriafutbol.api.exception.BadRequestException;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.repository.CategoryRepository;
import com.galeriafutbol.api.repository.FeaturedCollectionRepository;
import com.galeriafutbol.api.service.ImageStorageService;
import com.galeriafutbol.api.service.UploadImageService;

@Service
public class UploadImageServiceImpl implements UploadImageService {

    private final ImageStorageService imageStorageService;
    private final CategoryRepository categoryRepository;
    private final FeaturedCollectionRepository featuredCollectionRepository;

    public UploadImageServiceImpl(ImageStorageService imageStorageService,
            CategoryRepository categoryRepository,
            FeaturedCollectionRepository featuredCollectionRepository) {
        this.imageStorageService = imageStorageService;
        this.categoryRepository = categoryRepository;
        this.featuredCollectionRepository = featuredCollectionRepository;
    }

    @Override
    public UploadImageResponse uploadForAdmin(MultipartFile file, Long albumId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo de imagen es obligatorio");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "image");

        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9.\\-]", "_");

        String prefix = (albumId != null) ? ("albums/" + albumId + "/") : "misc/";
        String keyHint = prefix + System.currentTimeMillis() + "_" + sanitized;

        try {
            String url = imageStorageService.upload(
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType(),
                    keyHint);
            return new UploadImageResponse(url);
        } catch (Exception e) {
            throw new BadRequestException("No se pudo subir la imagen a R2: " + e.getMessage());
        }
    }

    @Override
    public UploadImageResponse uploadForCategory(MultipartFile file, Long categoryId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo de imagen es obligatorio");
        }
        if (categoryId == null) {
            throw new BadRequestException("El id de la categoría es obligatorio");
        }

        // Validar que la categoría existe
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + categoryId));

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "image");

        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        String keyHint = "icons/categories/" + categoryId + "/" + System.currentTimeMillis() + "_" + sanitized;

        try {
            String url = imageStorageService.upload(
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType(),
                    keyHint);
            return new UploadImageResponse(url);
        } catch (Exception e) {
            throw new BadRequestException("No se pudo subir la imagen a R2: " + e.getMessage());
        }
    }

    @Override
    public UploadImageResponse uploadForFeaturedBanner(MultipartFile file, Long featuredCollectionId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo de imagen es obligatorio");
        }
        if (featuredCollectionId == null) {
            throw new BadRequestException("El id de la colección destacada es obligatorio");
        }

        // Validar que la featured collection existe
        featuredCollectionRepository.findById(featuredCollectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Colección destacada no encontrada: " + featuredCollectionId));

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "banner");

        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        String keyHint = "featured-collections/" + featuredCollectionId + "/" + System.currentTimeMillis() + "_"
                + sanitized;

        try {
            String url = imageStorageService.upload(
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType(),
                    keyHint);
            return new UploadImageResponse(url);
        } catch (Exception e) {
            throw new BadRequestException("No se pudo subir la imagen a R2: " + e.getMessage());
        }
    }
}
