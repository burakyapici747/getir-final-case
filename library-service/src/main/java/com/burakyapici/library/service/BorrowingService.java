package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.security.UserDetailsImpl;

import java.util.List;
import java.util.UUID;

public interface BorrowingService {
    BorrowingDto borrowBookCopyByBarcode(
        String barcode,
        BorrowBookCopyRequest borrowBookCopyRequest,
        UserDetailsImpl userDetails
    );
    
    BorrowingDto returnBookCopyByBarcode(
        String barcode,
        BorrowReturnRequest borrowReturnRequest,
        UserDetailsImpl userDetails
    );

    void deleteAllByBookCopyId(UUID bookCopyId);

    void deleteAllByBookId(UUID bookId);

    List<BorrowingDto> getCurrentUserBorrowings(UUID userId);

    List<BorrowingDto> getUserBorrowingsById(UUID userId);

    void processOverdueBorrowings();
}
