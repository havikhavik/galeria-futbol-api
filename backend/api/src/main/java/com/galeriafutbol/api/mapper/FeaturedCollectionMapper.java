package com.galeriafutbol.api.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.galeriafutbol.api.dto.FeaturedCollectionAdminResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionWithAlbumsResponse;
import com.galeriafutbol.api.dto.UserAdminResponse;
import com.galeriafutbol.api.model.FeaturedCollection;
import com.galeriafutbol.api.model.User;

@Component
public class FeaturedCollectionMapper {

    private final AlbumMapper albumMapper;

    public FeaturedCollectionMapper(AlbumMapper albumMapper) {
        this.albumMapper = albumMapper;
    }

    public FeaturedCollectionResponse toResponse(FeaturedCollection collection) {
        if (collection == null) {
            return null;
        }

        FeaturedCollectionResponse dto = new FeaturedCollectionResponse();
        dto.setSlug(collection.getSlug());
        dto.setTitle(collection.getTitle());
        dto.setDescription(collection.getDescription());
        dto.setStartDate(collection.getStartDate());
        dto.setEndDate(collection.getEndDate());
        dto.setPriority(collection.getPriority());
        dto.setBannerImage(collection.getBannerImage());

        return dto;
    }

    public FeaturedCollectionWithAlbumsResponse toResponseWithAlbums(FeaturedCollection collection) {
        if (collection == null) {
            return null;
        }

        FeaturedCollectionWithAlbumsResponse dto = new FeaturedCollectionWithAlbumsResponse();
        dto.setSlug(collection.getSlug());
        dto.setTitle(collection.getTitle());
        dto.setDescription(collection.getDescription());
        dto.setStartDate(collection.getStartDate());
        dto.setEndDate(collection.getEndDate());
        dto.setBannerImage(collection.getBannerImage());

        if (collection.getAlbums() != null) {
            dto.setAlbums(collection.getAlbums()
                    .stream()
                    .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                    .map(fca -> albumMapper.toPublicResponse(fca.getAlbum()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public FeaturedCollectionAdminResponse toAdminResponse(FeaturedCollection collection) {
        if (collection == null) {
            return null;
        }

        FeaturedCollectionAdminResponse dto = new FeaturedCollectionAdminResponse();
        dto.setId(collection.getId());
        dto.setSlug(collection.getSlug());
        dto.setTitle(collection.getTitle());
        dto.setDescription(collection.getDescription());
        dto.setStartDate(collection.getStartDate());
        dto.setEndDate(collection.getEndDate());
        dto.setActive(collection.isActive());
        dto.setPriority(collection.getPriority());
        dto.setBannerImage(collection.getBannerImage());
        dto.setCreatedBy(toUserAdminResponse(collection.getCreatedBy()));
        dto.setUpdatedBy(toUserAdminResponse(collection.getUpdatedBy()));
        dto.setCreatedAt(collection.getCreatedAt());
        dto.setUpdatedAt(collection.getUpdatedAt());

        return dto;
    }

    private UserAdminResponse toUserAdminResponse(User user) {
        if (user == null) {
            return null;
        }

        UserAdminResponse dto = new UserAdminResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
}
