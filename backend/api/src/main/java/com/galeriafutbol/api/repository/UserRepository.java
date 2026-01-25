package com.galeriafutbol.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.galeriafutbol.api.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
