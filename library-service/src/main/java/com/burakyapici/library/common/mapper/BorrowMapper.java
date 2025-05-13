package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.response.BorrowingResponse;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.model.Borrowing;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "default")
public interface BorrowMapper {
    BorrowMapper INSTANCE = Mappers.getMapper(BorrowMapper.class);

    BorrowingDto toBorrowingDto(Borrowing borrow);

    BorrowingResponse toBorrowingResponse(BorrowingDto borrowingDto);
}
