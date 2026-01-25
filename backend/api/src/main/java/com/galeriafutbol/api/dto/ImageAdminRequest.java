package com.galeriafutbol.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageAdminRequest {

    private Long id;

    @NotBlank
    private String url;

    private Integer position;

    private Boolean primary;
}
