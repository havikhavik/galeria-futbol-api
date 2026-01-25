package com.galeriafutbol.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.galeriafutbol.api.model.FeaturedCollectionAlbum;

@Repository
public interface FeaturedCollectionAlbumRepository extends JpaRepository<FeaturedCollectionAlbum, Long> {
}
