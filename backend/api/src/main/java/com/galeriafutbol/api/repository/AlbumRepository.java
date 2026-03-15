package com.galeriafutbol.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.galeriafutbol.api.model.Album;
import com.galeriafutbol.api.model.AlbumStatus;
import com.galeriafutbol.api.model.TeamType;

public interface AlbumRepository extends JpaRepository<Album, Long> {

  @Query("""
      SELECT COUNT(a)
      FROM Album a
      JOIN a.category c
      WHERE c.teamType = :teamType
      """)
  long countByTeamType(@Param("teamType") TeamType teamType);

  @Query("""
      SELECT a
      FROM Album a
      LEFT JOIN FETCH a.category c
      WHERE a.status = :status
        AND (:query IS NULL OR LOWER(a.title) LIKE CONCAT('%', CAST(:query AS String), '%'))
        AND (:teamType IS NULL OR c.teamType = :teamType)
        AND (:categoryCode IS NULL OR c.code = :categoryCode)
        AND (:seasonStart IS NULL OR a.seasonStart = :seasonStart)
        AND (:kids IS NULL OR a.kids = :kids)
        AND (:women IS NULL OR a.women = :women)
        AND (:goalkeeper IS NULL OR a.goalkeeper = :goalkeeper)
        AND (:training IS NULL OR a.training = :training)
        AND (:classic IS NULL OR a.classic = :classic)
        AND (:retro IS NULL OR a.retro = :retro)
      """)
  Page<Album> searchAlbums(
      @Param("status") AlbumStatus status,
      @Param("query") String query,
      @Param("teamType") TeamType teamType,
      @Param("categoryCode") String categoryCode,
      @Param("seasonStart") Integer seasonStart,
      @Param("kids") Boolean kids,
      @Param("women") Boolean women,
      @Param("goalkeeper") Boolean goalkeeper,
      @Param("training") Boolean training,
      @Param("classic") Boolean classic,
      @Param("retro") Boolean retro,
      Pageable pageable);

  @Query("""
      SELECT a
      FROM Album a
      LEFT JOIN FETCH a.category c
      WHERE (:query IS NULL OR LOWER(a.title) LIKE CONCAT('%', CAST(:query AS String), '%'))
        AND (:teamType IS NULL OR c.teamType = :teamType)
        AND (:categoryCode IS NULL OR c.code = :categoryCode)
        AND (:seasonStart IS NULL OR a.seasonStart = :seasonStart)
        AND (:kids IS NULL OR a.kids = :kids)
        AND (:women IS NULL OR a.women = :women)
        AND (:goalkeeper IS NULL OR a.goalkeeper = :goalkeeper)
        AND (:training IS NULL OR a.training = :training)
        AND (:classic IS NULL OR a.classic = :classic)
        AND (:retro IS NULL OR a.retro = :retro)
      """)
  Page<Album> searchAlbumsWithoutStatus(
      @Param("query") String query,
      @Param("teamType") TeamType teamType,
      @Param("categoryCode") String categoryCode,
      @Param("seasonStart") Integer seasonStart,
      @Param("kids") Boolean kids,
      @Param("women") Boolean women,
      @Param("goalkeeper") Boolean goalkeeper,
      @Param("training") Boolean training,
      @Param("classic") Boolean classic,
      @Param("retro") Boolean retro,
      Pageable pageable);
}
