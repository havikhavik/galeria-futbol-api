package com.galeriafutbol.api.service;

import java.util.List;

import com.galeriafutbol.api.dto.FeaturedCollectionAdminRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionAdminResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionPartialRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionWithAlbumsResponse;

public interface FeaturedCollectionService {

    List<FeaturedCollectionResponse> getAllActive();

    FeaturedCollectionWithAlbumsResponse getBySlug(String slug);

    FeaturedCollectionAdminResponse createFeaturedCollection(FeaturedCollectionAdminRequest request);

    FeaturedCollectionAdminResponse partialUpdate(Long id, FeaturedCollectionPartialRequest request);

    void deleteFeaturedCollection(Long id);
}
