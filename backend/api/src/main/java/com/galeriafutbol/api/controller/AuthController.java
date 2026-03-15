package com.galeriafutbol.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.galeriafutbol.api.dto.LoginRequest;
import com.galeriafutbol.api.dto.LoginResponse;
import com.galeriafutbol.api.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints de login y autenticación")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña, devuelve un token JWT")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Sesión actual", description = "Valida el token JWT y devuelve información básica de la sesión")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "authenticated", true,
                "email", authentication.getName());
    }
}
