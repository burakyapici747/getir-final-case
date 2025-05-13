package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.response.BorrowingResponse;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.model.Borrowing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "default")
public interface BorrowMapper {
    BorrowMapper INSTANCE = Mappers.getMapper(BorrowMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userFirstName", source = "user.firstName")
    @Mapping(target = "userLastName", source = "user.lastName")
    @Mapping(target = "bookCopyId", source = "bookCopy.id")
    @Mapping(target = "bookCopyBarcode", source = "bookCopy.barcode")
    @Mapping(target = "bookId", source = "bookCopy.book.id")
    @Mapping(target = "bookTitle", source = "bookCopy.book.title")
    @Mapping(target = "bookIsbn", source = "bookCopy.book.isbn")
    @Mapping(target = "borrowedByStaffId", source = "borrowedByStaff.id")
    @Mapping(target = "borrowedByStaffName", expression = "java(borrow.getBorrowedByStaff().getFirstName() + ' ' + borrow.getBorrowedByStaff().getLastName())")
    @Mapping(target = "returnedByStaffId", source = "returnedByStaff.id")
    @Mapping(target = "returnedByStaffName", expression = "java(borrow.getReturnedByStaff() != null ? borrow.getReturnedByStaff().getFirstName() + ' ' + borrow.getReturnedByStaff().getLastName() : null)")
    BorrowingDto toBorrowingDto(Borrowing borrow);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "userEmail", source = "userEmail")
    @Mapping(target = "userFullName", expression = "java(borrowingDto.userFirstName() + ' ' + borrowingDto.userLastName())")
    @Mapping(target = "bookCopyId", source = "bookCopyId")
    @Mapping(target = "bookCopyBarcode", source = "bookCopyBarcode")
    @Mapping(target = "bookId", source = "bookId")
    @Mapping(target = "bookTitle", source = "bookTitle")
    @Mapping(target = "bookIsbn", source = "bookIsbn")
    @Mapping(target = "borrowedByStaffId", source = "borrowedByStaffId")
    @Mapping(target = "borrowedByStaffName", source = "borrowedByStaffName")
    @Mapping(target = "returnedByStaffId", source = "returnedByStaffId")
    @Mapping(target = "returnedByStaffName", source = "returnedByStaffName")
    BorrowingResponse toBorrowingResponse(BorrowingDto borrowingDto);
}
