package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class FeaturedCollectionPartialRequest {
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Integer priority;
    private String bannerImage;
    private Boolean active;
}
