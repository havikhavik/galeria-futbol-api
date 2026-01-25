package com.galeriafutbol.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.galeriafutbol.api.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByAlbumIdOrderByPositionAsc(Long albumId);
}
