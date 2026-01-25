package com.galeriafutbol.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.galeriafutbol.api.model.FeaturedCollection;

@Repository
public interface FeaturedCollectionRepository extends JpaRepository<FeaturedCollection, Long> {

    @Query("SELECT fc FROM FeaturedCollection fc WHERE fc.active = true ORDER BY fc.priority DESC")
    List<FeaturedCollection> findAllActive();

    FeaturedCollection findBySlug(String slug);
}
