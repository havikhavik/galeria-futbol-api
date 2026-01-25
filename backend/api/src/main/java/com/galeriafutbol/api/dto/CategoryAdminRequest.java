package com.galeriafutbol.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryAdminRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String thumbnail;
}
