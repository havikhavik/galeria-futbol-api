package com.galeriafutbol.api.dto;

import lombok.Data;

@Data
public class ImageResponse {

    private Long id;
    private String url;
    private Integer position;
    private boolean primary;
}
