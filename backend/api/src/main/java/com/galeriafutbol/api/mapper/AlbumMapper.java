package com.galeriafutbol.api.mapper;

import org.springframework.stereotype.Component;

import com.galeriafutbol.api.dto.AlbumAdminResponse;
import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.dto.UserAdminResponse;
import com.galeriafutbol.api.model.Album;
import com.galeriafutbol.api.model.User;

@Component
public class AlbumMapper {

    public AlbumPublicResponse toPublicResponse(Album album) {
        if (album == null) {
            return null;
        }

        AlbumPublicResponse dto = new AlbumPublicResponse();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setSeasonLabel(album.getSeasonLabel());
        dto.setSeasonStart(album.getSeasonStart());
        dto.setTeamType(album.getTeamType());
        if (album.getCategory() != null) {
            dto.setCategoryCode(album.getCategory().getCode());
            dto.setCategoryName(album.getCategory().getName());
        }
        dto.setThumbnail(album.getThumbnail());
        dto.setDescription(album.getDescription());
        dto.setKids(album.isKids());
        dto.setWomen(album.isWomen());
        dto.setGoalkeeper(album.isGoalkeeper());
        dto.setTraining(album.isTraining());
        dto.setClassic(album.isClassic());
        dto.setRetro(album.isRetro());
        return dto;
    }

    public AlbumAdminResponse toAdminResponse(Album album) {
        if (album == null) {
            return null;
        }

        AlbumAdminResponse dto = new AlbumAdminResponse();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setSeasonLabel(album.getSeasonLabel());
        dto.setSeasonStart(album.getSeasonStart());
        dto.setTeamType(album.getTeamType());
        dto.setStatus(album.getStatus());

        if (album.getCategory() != null) {
            dto.setCategoryCode(album.getCategory().getCode());
            dto.setCategoryName(album.getCategory().getName());
            dto.setCategoryThumbnail(album.getCategory().getThumbnail());
        }

        dto.setThumbnail(album.getThumbnail());
        dto.setDescription(album.getDescription());
        dto.setKids(album.isKids());
        dto.setWomen(album.isWomen());
        dto.setGoalkeeper(album.isGoalkeeper());
        dto.setTraining(album.isTraining());
        dto.setClassic(album.isClassic());
        dto.setRetro(album.isRetro());
        dto.setSourceAlbumId(album.getSourceAlbumId());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());

        dto.setCreatedBy(toUserAdminResponse(album.getCreatedBy()));
        dto.setUpdatedBy(toUserAdminResponse(album.getUpdatedBy()));

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
