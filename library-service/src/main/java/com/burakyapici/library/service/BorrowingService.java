package com.burakyapici.library.service;

import com.burakyapici.library.domain.dto.BorrowDto;
import com.burakyapici.library.security.UserDetailsImpl;

import java.util.UUID;

public interface BorrowingService {
    BorrowDto borrowBook(UUID bookCopyBarcode, UserDetailsImpl userDetails);
}
