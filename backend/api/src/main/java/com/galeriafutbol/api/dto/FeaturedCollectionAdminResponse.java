package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class FeaturedCollectionAdminResponse {

    private Long id;
    private String slug;
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private boolean active;
    private int priority;
    private String bannerImage;
    private UserAdminResponse createdBy;
    private UserAdminResponse updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
