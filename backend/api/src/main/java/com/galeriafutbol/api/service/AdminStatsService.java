package com.galeriafutbol.api.service;

import com.galeriafutbol.api.dto.AdminStatsOverviewResponse;

public interface AdminStatsService {

    AdminStatsOverviewResponse getOverview();

    long countTotalImages();
}