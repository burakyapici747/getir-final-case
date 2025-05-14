package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    boolean existsByName(String name);
}
