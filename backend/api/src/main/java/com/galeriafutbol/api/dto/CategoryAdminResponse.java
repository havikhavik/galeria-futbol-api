package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;

import com.galeriafutbol.api.model.TeamType;

import lombok.Data;

@Data
public class CategoryAdminResponse {

    private Long id;
    private String code;
    private String name;
    private TeamType teamType;
    private String thumbnail;
    private OffsetDateTime createdAt;
}
