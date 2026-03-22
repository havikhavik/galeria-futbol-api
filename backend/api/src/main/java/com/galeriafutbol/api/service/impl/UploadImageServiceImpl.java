package com.galeriafutbol.api.service.impl;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.galeriafutbol.api.dto.UploadImageResponse;
import com.galeriafutbol.api.exception.BadRequestException;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.model.FeaturedCollection;
import com.galeriafutbol.api.repository.AlbumRepository;
import com.galeriafutbol.api.repository.CategoryRepository;
import com.galeriafutbol.api.repository.FeaturedCollectionRepository;
import com.galeriafutbol.api.service.ImageStorageService;
import com.galeriafutbol.api.service.UploadImageService;

@Service
public class UploadImageServiceImpl implements UploadImageService {

    private final ImageStorageService imageStorageService;
    private final AlbumRepository albumRepository;
    private final CategoryRepository categoryRepository;
    private final FeaturedCollectionRepository featuredCollectionRepository;
    private final String albumFolderPrefix;

    public UploadImageServiceImpl(ImageStorageService imageStorageService,
            AlbumRepository albumRepository,
            CategoryRepository categoryRepository,
            FeaturedCollectionRepository featuredCollectionRepository,
            @Value("${app.storage.album-folder-prefix:galeria-futbol}") String albumFolderPrefix) {
        this.imageStorageService = imageStorageService;
        this.albumRepository = albumRepository;
        this.categoryRepository = categoryRepository;
        this.featuredCollectionRepository = featuredCollectionRepository;
        this.albumFolderPrefix = normalizePrefix(albumFolderPrefix);
    }

    @Override
    public UploadImageResponse uploadForAdmin(MultipartFile file, Long albumId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo de imagen es obligatorio");
        }
        if (albumId == null) {
            throw new BadRequestException("El id del álbum es obligatorio");
        }

        albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado: " + albumId));

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "image");

        String extension = extractExtension(originalFilename);
        String prefix = (albumId != null) ? (albumFolderPrefix + "/album-" + albumId + "/") : "misc/";

        try {
            byte[] bytes = file.getBytes();
            String checksum = sha256Hex(bytes);
            String keyHint = prefix + checksum + extension;

            String url = imageStorageService.upload(
                    new ByteArrayInputStream(bytes),
                    bytes.length,
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

        FeaturedCollection collection = featuredCollectionRepository.findById(featuredCollectionId)
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

            String previousBanner = collection.getBannerImage();
            collection.setBannerImage(url);
            featuredCollectionRepository.save(collection);

            if (isStorageManagedBanner(previousBanner) && !previousBanner.equals(url)) {
                imageStorageService.delete(previousBanner);
            }

            return new UploadImageResponse(url);
        } catch (Exception e) {
            throw new BadRequestException("No se pudo subir la imagen a R2: " + e.getMessage());
        }
    }

    private boolean isStorageManagedBanner(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalized = value.trim().toLowerCase();
        return !"placeholder".equals(normalized) && normalized.startsWith("http");
    }

    private String normalizePrefix(String value) {
        String trimmed = value == null ? "galeria-futbol" : value.trim();
        if (trimmed.isEmpty()) {
            return "galeria-futbol";
        }
        return trimmed.replaceAll("^/+|/+$", "");
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "";
        }
        String ext = filename.substring(idx + 1).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return ext.isEmpty() ? "" : "." + ext;
    }

    private String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }
}
