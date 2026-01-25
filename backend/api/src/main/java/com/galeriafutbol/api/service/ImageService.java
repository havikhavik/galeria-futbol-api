package com.galeriafutbol.api.service;

import java.util.List;

import com.galeriafutbol.api.dto.ImageAdminRequest;
import com.galeriafutbol.api.dto.ImageResponse;

public interface ImageService {

    List<ImageResponse> getImagesForAlbum(Long albumId);

    List<ImageResponse> replaceImagesForAlbum(Long albumId, List<ImageAdminRequest> requests);

    void deleteImage(Long imageId);
}
