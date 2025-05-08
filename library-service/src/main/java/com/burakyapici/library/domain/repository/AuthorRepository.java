package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    boolean existsByFirstName(String firstName);
    Page<Author> findAll(Specification<Author> specification, Pageable pageable);
}
