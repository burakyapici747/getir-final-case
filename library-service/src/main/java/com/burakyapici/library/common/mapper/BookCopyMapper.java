package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.api.dto.response.BookCopyResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.BookCopy;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "default")
public interface BookCopyMapper {
    BookCopyMapper INSTANCE = Mappers.getMapper(BookCopyMapper.class);

    BookCopyDto bookCopyToBookCopyDto(BookCopy bookCopy);

    List<BookCopyDto> bookCopyListToBookCopyDtoList(List<BookCopy> bookCopyList);

    PageableResponse<BookCopyResponse> bookCopyPageableDtoListToPageableResponse(PageableDto<BookCopyDto> pageableDto);

    BookCopyResponse bookCopyDtoToBookCopyResponse(BookCopyDto bookCopyDto);

    @Mapping(target = "book", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBookCopyFromBookCopyUpdateRequest(BookCopyUpdateRequest bookCopyUpdateRequest, @MappingTarget BookCopy bookCopy);
}
