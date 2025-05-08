package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.domain.dto.BorrowDto;
import com.burakyapici.library.security.UserDetailsImpl;

public interface BorrowingService {
    BorrowDto borrowBookCopyByBarcode(
        String barcode,
        BorrowBookCopyRequest borrowBookCopyRequest,
        UserDetailsImpl userDetails
    );
    BorrowDto returnBookCopyByBarcode(
        String barcode,
        BorrowReturnRequest borrowReturnRequest,
        UserDetailsImpl userDetails
    );
}
