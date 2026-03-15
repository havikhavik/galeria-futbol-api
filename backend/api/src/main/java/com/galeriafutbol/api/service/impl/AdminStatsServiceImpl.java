package com.galeriafutbol.api.service.impl;

import org.springframework.stereotype.Service;

import com.galeriafutbol.api.repository.ImageRepository;
import com.galeriafutbol.api.service.AdminStatsService;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    private final ImageRepository imageRepository;

    public AdminStatsServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public long countTotalImages() {
        return imageRepository.count();
    }
}