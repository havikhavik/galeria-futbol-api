package com.galeriafutbol.api.dto;

import com.galeriafutbol.api.model.TeamType;

import lombok.Data;

@Data
public class AlbumPublicResponse {

    private Long id;
    private String title;
    private String seasonLabel;
    private Integer seasonStart;
    private TeamType teamType;

    private String categoryCode;
    private String categoryName;

    private String thumbnail;
    private String description;

    private boolean kids;
    private boolean women;
    private boolean goalkeeper;
    private boolean training;
    private boolean classic;
    private boolean retro;
}
