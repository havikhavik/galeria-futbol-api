package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeaturedCollectionAdminRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private OffsetDateTime startDate;

    @NotNull
    private OffsetDateTime endDate;

    private Boolean active;

    private Integer priority;

    @NotBlank
    private String bannerImage;
}
