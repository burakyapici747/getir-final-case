package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
}
