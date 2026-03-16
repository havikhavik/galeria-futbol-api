package com.galeriafutbol.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.galeriafutbol.api.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByAlbumIdOrderByPositionAsc(Long albumId);

    boolean existsByAlbumIdAndUrl(Long albumId, String url);

    @Query("""
            SELECT i.album.id AS albumId, COUNT(i.id) AS totalImages
            FROM Image i
            WHERE i.album.id IN :albumIds
            GROUP BY i.album.id
            """)
    List<AlbumImageCountProjection> countByAlbumIds(@Param("albumIds") List<Long> albumIds);
}
