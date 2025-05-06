package com.burakyapici.library.service;

import com.burakyapici.library.domain.model.BookCopy;

import java.util.UUID;

public interface BookCopyService {
    BookCopy getBookCopyByIdOrElseThrow(UUID id);
    BookCopy getBookCopyByBarcodeOrElseThrow(UUID barcode);
}
