package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.WaitList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WaitListRepository extends JpaRepository<WaitList, UUID> {
    boolean existsByBook_Id(UUID bookId);
    Page<WaitList> findAllByBook_Id(UUID bookId, Pageable pageable);
    List<WaitList> findByUser_Id(UUID id);
    List<WaitList> findByBook_IdAndStatus(UUID bookId, WaitListStatus status);
    List<WaitList> findByUser_IdAndStatusIn(UUID userId, Collection<WaitListStatus> statuses);
    WaitList findByUser_IdAndBook_IdAndStatus(UUID userId, UUID bookId, WaitListStatus status);
    Page<WaitList> findByBook_Id(UUID bookId, Pageable pageable);
}
