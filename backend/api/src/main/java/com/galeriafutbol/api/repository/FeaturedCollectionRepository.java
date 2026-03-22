package com.galeriafutbol.api.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.galeriafutbol.api.model.FeaturedCollection;

@Repository
public interface FeaturedCollectionRepository extends JpaRepository<FeaturedCollection, Long> {

    @Query("SELECT fc FROM FeaturedCollection fc WHERE fc.active = true ORDER BY fc.priority DESC")
    List<FeaturedCollection> findAllActive();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE FeaturedCollection fc
               SET fc.active = false,
                   fc.updatedAt = :now
             WHERE fc.active = true
               AND fc.endDate < :now
            """)
    int deactivateExpired(@Param("now") OffsetDateTime now);

    FeaturedCollection findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
