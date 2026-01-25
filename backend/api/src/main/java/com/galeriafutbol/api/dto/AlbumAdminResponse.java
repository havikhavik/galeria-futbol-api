package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;

import com.galeriafutbol.api.model.AlbumStatus;
import com.galeriafutbol.api.model.TeamType;

import lombok.Data;

@Data
public class AlbumAdminResponse {

    private Long id;
    private String title;
    private String seasonLabel;
    private Integer seasonStart;
    private TeamType teamType;
    private AlbumStatus status;

    private String categoryCode;
    private String categoryName;
    private String categoryThumbnail;

    private String thumbnail;
    private String description;

    private boolean kids;
    private boolean women;
    private boolean goalkeeper;
    private boolean training;
    private boolean classic;
    private boolean retro;
    private Long sourceAlbumId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private UserAdminResponse createdBy;
    private UserAdminResponse updatedBy;
}
