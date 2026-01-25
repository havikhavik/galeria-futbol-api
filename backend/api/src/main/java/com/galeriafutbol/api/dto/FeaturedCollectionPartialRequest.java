package com.galeriafutbol.api.dto;

import lombok.Data;

@Data
public class FeaturedCollectionPartialRequest {
    private String slug;
    private String title;
    private String description;
    private Integer priority;
    private String bannerImage;
    private Boolean active;
}
