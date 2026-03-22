package com.galeriafutbol.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.galeriafutbol.api.model.FeaturedCollectionAlbum;

@Repository
public interface FeaturedCollectionAlbumRepository extends JpaRepository<FeaturedCollectionAlbum, Long> {

    interface CollectionAlbumCountProjection {
        Long getFeaturedCollectionId();

        Long getTotalAlbums();
    }

    List<FeaturedCollectionAlbum> findByFeaturedCollectionIdOrderByDisplayOrderAsc(Long featuredCollectionId);

    @Query("""
            SELECT fca.featuredCollection.id AS featuredCollectionId,
                   COUNT(fca) AS totalAlbums
              FROM FeaturedCollectionAlbum fca
             WHERE fca.featuredCollection.id IN :collectionIds
             GROUP BY fca.featuredCollection.id
            """)
    List<CollectionAlbumCountProjection> countAlbumsByCollectionIds(@Param("collectionIds") List<Long> collectionIds);

    boolean existsByFeaturedCollectionIdAndAlbumId(Long featuredCollectionId, Long albumId);

    long countByFeaturedCollectionId(Long featuredCollectionId);

    void deleteByFeaturedCollectionIdAndAlbumId(Long featuredCollectionId, Long albumId);
}
