package com.galeriafutbol.api.service.impl;

import org.springframework.stereotype.Service;

import com.galeriafutbol.api.dto.AdminStatsOverviewResponse;
import com.galeriafutbol.api.model.TeamType;
import com.galeriafutbol.api.repository.AlbumRepository;
import com.galeriafutbol.api.repository.ImageRepository;
import com.galeriafutbol.api.service.AdminStatsService;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    private final AlbumRepository albumRepository;
    private final ImageRepository imageRepository;

    public AdminStatsServiceImpl(AlbumRepository albumRepository, ImageRepository imageRepository) {
        this.albumRepository = albumRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public AdminStatsOverviewResponse getOverview() {
        AdminStatsOverviewResponse response = new AdminStatsOverviewResponse();
        response.setTotalAlbums(albumRepository.count());
        response.setTotalClubs(albumRepository.countByTeamType(TeamType.CLUB));
        response.setTotalSelections(albumRepository.countByTeamType(TeamType.NATIONAL));
        response.setTotalImages(imageRepository.count());
        return response;
    }

    @Override
    public long countTotalImages() {
        return imageRepository.count();
    }
}