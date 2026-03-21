package com.galeriafutbol.api.service;

import java.util.List;

import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionAdminRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionAdminResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionPartialRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionWithAlbumsResponse;

public interface FeaturedCollectionService {

    List<FeaturedCollectionResponse> getAllActive();

    List<FeaturedCollectionAdminResponse> getAllForAdmin();

    FeaturedCollectionAdminResponse getByIdForAdmin(Long id);

    List<AlbumPublicResponse> getAlbumsForAdmin(Long id);

    FeaturedCollectionWithAlbumsResponse getBySlug(String slug);

    FeaturedCollectionAdminResponse createDraft();

    FeaturedCollectionAdminResponse createFeaturedCollection(FeaturedCollectionAdminRequest request);

    FeaturedCollectionAdminResponse partialUpdate(Long id, FeaturedCollectionPartialRequest request);

    void addAlbumToCollection(Long collectionId, Long albumId);

    void removeAlbumFromCollection(Long collectionId, Long albumId);

    void deleteFeaturedCollection(Long id);
}
