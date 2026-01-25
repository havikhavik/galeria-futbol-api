package com.galeriafutbol.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import com.galeriafutbol.api.model.AlbumStatus;

@Data
public class AlbumAdminRequest {

    @NotBlank
    private String title;

    private String seasonLabel;

    @NotNull
    private Integer seasonStart;

    private String categoryCode;

    private String thumbnail;

    private String description;

    private Boolean kids;
    private Boolean women;
    private Boolean goalkeeper;
    private Boolean training;
    private Boolean classic;
    private Boolean retro;

    private AlbumStatus status;
}
