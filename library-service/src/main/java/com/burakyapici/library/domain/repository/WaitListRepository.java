package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.WaitList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WaitListRepository extends JpaRepository<WaitList, UUID> {
    boolean existsByBookId(UUID bookId);
}
