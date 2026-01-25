package com.galeriafutbol.api.service;

import java.util.List;

import com.galeriafutbol.api.dto.UserAdminRequest;
import com.galeriafutbol.api.dto.UserAdminResponse;

public interface UserService {

    UserAdminResponse createUser(UserAdminRequest request);

    UserAdminResponse updateUser(Long id, UserAdminRequest request);

    void deleteUser(Long id);

    UserAdminResponse getUser(Long id);

    List<UserAdminResponse> getAllUsers();
}
