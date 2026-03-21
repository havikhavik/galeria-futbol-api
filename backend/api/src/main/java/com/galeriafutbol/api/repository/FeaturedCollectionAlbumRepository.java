package com.galeriafutbol.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.galeriafutbol.api.model.FeaturedCollectionAlbum;

@Repository
public interface FeaturedCollectionAlbumRepository extends JpaRepository<FeaturedCollectionAlbum, Long> {

    List<FeaturedCollectionAlbum> findByFeaturedCollectionIdOrderByDisplayOrderAsc(Long featuredCollectionId);

    boolean existsByFeaturedCollectionIdAndAlbumId(Long featuredCollectionId, Long albumId);

    long countByFeaturedCollectionId(Long featuredCollectionId);

    void deleteByFeaturedCollectionIdAndAlbumId(Long featuredCollectionId, Long albumId);
}
