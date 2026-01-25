package com.galeriafutbol.api.mapper;

import org.springframework.stereotype.Component;

import com.galeriafutbol.api.dto.ImageResponse;
import com.galeriafutbol.api.model.Image;

@Component
public class ImageMapper {

    public ImageResponse toResponse(Image image) {
        if (image == null) {
            return null;
        }

        ImageResponse dto = new ImageResponse();
        dto.setId(image.getId());
        dto.setUrl(image.getUrl());
        dto.setPosition(image.getPosition());
        dto.setPrimary(image.isPrimary());
        return dto;
    }
}
