package com.galeriafutbol.api.service;

import org.springframework.web.multipart.MultipartFile;

import com.galeriafutbol.api.dto.UploadImageResponse;

public interface UploadImageService {

    UploadImageResponse uploadForAdmin(MultipartFile file, Long albumId);

    UploadImageResponse uploadForCategory(MultipartFile file, Long categoryId);

    UploadImageResponse uploadForFeaturedBanner(MultipartFile file, Long featuredCollectionId);
}
