package com.burakyapici.library.common.mapper;

import com.burakyapici.library.domain.dto.BorrowDto;
import com.burakyapici.library.domain.model.Borrowing;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BorrowMapper {
    BorrowMapper INSTANCE = Mappers.getMapper(BorrowMapper.class);


    BorrowDto toDto(Borrowing borrow);
}
