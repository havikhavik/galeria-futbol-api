package com.galeriafutbol.api.dto;

import lombok.Data;

@Data
public class AdminStatsOverviewResponse {

    private long totalAlbums;
    private long totalCollections;
    private long totalClubs;
    private long totalSelections;
    private long totalImages;
}