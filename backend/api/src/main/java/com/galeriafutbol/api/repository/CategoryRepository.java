package com.galeriafutbol.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.galeriafutbol.api.model.Category;
import com.galeriafutbol.api.model.TeamType;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCode(String code);

    List<Category> findByTeamType(TeamType teamType);
}
