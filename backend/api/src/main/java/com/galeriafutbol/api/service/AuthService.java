package com.galeriafutbol.api.service;

import com.galeriafutbol.api.dto.LoginRequest;
import com.galeriafutbol.api.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
