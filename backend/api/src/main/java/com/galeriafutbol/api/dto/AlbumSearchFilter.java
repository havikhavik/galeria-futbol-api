package com.galeriafutbol.api.dto;

import com.galeriafutbol.api.model.TeamType;

import lombok.Data;

@Data
public class AlbumSearchFilter {

    private String q;

    private TeamType teamType;

    private String categoryCode;

    private Integer seasonStart;

    private Boolean kids;
    private Boolean women;
    private Boolean goalkeeper;
    private Boolean training;
    private Boolean classic;
    private Boolean retro;
}
