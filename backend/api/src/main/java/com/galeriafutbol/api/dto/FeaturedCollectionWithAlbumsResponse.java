package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class FeaturedCollectionWithAlbumsResponse {

    private String slug;
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String bannerImage;

    private List<AlbumPublicResponse> albums;
}
