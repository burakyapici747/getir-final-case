package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.api.dto.response.AuthorResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Author;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    AuthorDto authorToAuthorDto(Author author);
    List<AuthorDto> authorListToAuthorDtoList(List<Author> authors);

    AuthorResponse authorDtoToAuthorResponse(AuthorDto authorDto);

    PageableResponse<AuthorResponse> pageableDtoToPageableResponse(PageableDto<AuthorDto> pageableResponse);

    @Mapping(target = "books", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAuthorFromAuthorUpdateRequest(AuthorUpdateRequest authorUpdateRequest, @MappingTarget Author author);
}
