package com.galeriafutbol.api.dto;

import com.galeriafutbol.api.model.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAdminRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String password;

    @NotNull
    private UserRole role;

    private Boolean active;
}
