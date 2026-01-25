package com.galeriafutbol.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.galeriafutbol.api.dto.ImageAdminRequest;
import com.galeriafutbol.api.dto.ImageResponse;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.mapper.ImageMapper;
import com.galeriafutbol.api.model.Album;
import com.galeriafutbol.api.model.Image;
import com.galeriafutbol.api.repository.AlbumRepository;
import com.galeriafutbol.api.repository.ImageRepository;
import com.galeriafutbol.api.service.ImageService;
import com.galeriafutbol.api.service.ImageStorageService;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final AlbumRepository albumRepository;
    private final ImageStorageService imageStorageService;
    private final ImageMapper imageMapper;

    public ImageServiceImpl(ImageRepository imageRepository,
            AlbumRepository albumRepository,
            ImageStorageService imageStorageService,
            ImageMapper imageMapper) {
        this.imageRepository = imageRepository;
        this.albumRepository = albumRepository;
        this.imageStorageService = imageStorageService;
        this.imageMapper = imageMapper;
    }

    @Override
    public List<ImageResponse> getImagesForAlbum(Long albumId) {
        List<Image> images = imageRepository.findByAlbumIdOrderByPositionAsc(albumId);
        return images.stream().map(imageMapper::toResponse).toList();
    }

    @Override
    public List<ImageResponse> replaceImagesForAlbum(Long albumId, List<ImageAdminRequest> requests) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado: " + albumId));

        List<Image> existing = imageRepository.findByAlbumIdOrderByPositionAsc(albumId);
        Map<Long, Image> existingById = new HashMap<>();

        for (Image image : existing) {
            existingById.put(image.getId(), image);
        }

        List<Image> result = new ArrayList<>();

        if (requests != null) {
            for (ImageAdminRequest req : requests) {
                Image image;

                if (req.getId() != null && existingById.containsKey(req.getId())) {
                    image = existingById.get(req.getId());
                    existingById.remove(req.getId());
                } else {
                    // Nueva imagen
                    image = new Image();
                    image.setAlbum(album);
                    image.setUrl(req.getUrl());
                }

                image.setPosition(req.getPosition());
                image.setPrimary(Boolean.TRUE.equals(req.getPrimary()));
                result.add(imageRepository.save(image));
            }
        }

        if (!existingById.isEmpty()) {
            for (Image toDelete : existingById.values()) {
                if (toDelete.getUrl() != null) {
                    imageStorageService.delete(toDelete.getUrl());
                }
            }
            imageRepository.deleteAll(existingById.values());
        }

        return result.stream().map(imageMapper::toResponse).toList();
    }

    @Override
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada: " + imageId));

        if (image.getUrl() != null && !image.getUrl().isBlank()) {
            imageStorageService.delete(image.getUrl());
        }

        imageRepository.delete(image);
    }

}
