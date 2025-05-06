package com.burakyapici.library.service.impl;

import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.repository.BookCopyRepository;
import com.burakyapici.library.exception.BookCopyNotFoundException;
import com.burakyapici.library.service.BookCopyService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookCopyServiceImpl implements BookCopyService {
    private final BookCopyRepository bookCopyRepository;

    public BookCopyServiceImpl(BookCopyRepository bookCopyRepository) {
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    public BookCopy getBookCopyByIdOrElseThrow(UUID id) {
        return bookCopyRepository.findById(id)
            .orElseThrow(() -> new BookCopyNotFoundException("Book copy not found with id: " + id));
    }

    @Override
    public BookCopy getBookCopyByBarcodeOrElseThrow(UUID barcode) {
        return bookCopyRepository.findByBarcode(barcode)
            .orElseThrow(() -> new BookCopyNotFoundException("Book copy not found with barcode: " + barcode));
    }
}
