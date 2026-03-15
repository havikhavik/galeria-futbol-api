package com.galeriafutbol.api.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.dto.AdminStatsOverviewResponse;
import com.galeriafutbol.api.service.AdminStatsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/stats")
@Tag(name = "Estadísticas (Admin)", description = "Métricas administrativas rápidas")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Resumen de estadísticas", description = "Devuelve métricas agregadas para dashboard admin")
    public AdminStatsOverviewResponse getOverview() {
        return adminStatsService.getOverview();
    }

    @GetMapping("/images/count")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Contar imágenes totales", description = "Devuelve el total real de registros en tabla images")
    public Map<String, Long> countImages() {
        return Map.of("totalImages", adminStatsService.countTotalImages());
    }
}
