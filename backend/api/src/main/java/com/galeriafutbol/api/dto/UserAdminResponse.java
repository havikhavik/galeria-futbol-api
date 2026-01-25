package com.galeriafutbol.api.dto;

import java.time.OffsetDateTime;

import com.galeriafutbol.api.model.UserRole;

import lombok.Data;

@Data
public class UserAdminResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastLoginAt;
}
