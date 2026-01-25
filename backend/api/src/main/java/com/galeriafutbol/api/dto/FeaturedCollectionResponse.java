package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class FeaturedCollectionResponse {

    private String slug;
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private int priority;
    private String bannerImage;
}
