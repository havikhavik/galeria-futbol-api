package com.galeriafutbol.api.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.galeriafutbol.api.dto.UploadImageResponse;
import com.galeriafutbol.api.exception.BadRequestException;
import com.galeriafutbol.api.service.ImageStorageService;
import com.galeriafutbol.api.service.UploadImageService;

@Service
public class UploadImageServiceImpl implements UploadImageService {

    private final ImageStorageService imageStorageService;

    public UploadImageServiceImpl(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
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
    public UploadImageResponse uploadForCategory(MultipartFile file, String categoryCode) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo de imagen es obligatorio");
        }
        if (categoryCode == null || categoryCode.isBlank()) {
            throw new BadRequestException("El código de categoría es obligatorio");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "image");

        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        String keyHint = "icons/categories/" + categoryCode + "/" + sanitized;

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
