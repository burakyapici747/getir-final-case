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

@Mapper(componentModel = "default")
public interface AuthorMapper {
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    AuthorDto toAuthorDto(Author author);
    List<AuthorDto> toAuthorDtoList(List<Author> authors);

    AuthorResponse toAuthorResponse(AuthorDto authorDto);

    PageableResponse<AuthorResponse> toPageableResponse(PageableDto<AuthorDto> pageableResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAuthorFromAuthorUpdateRequest(AuthorUpdateRequest authorUpdateRequest, @MappingTarget Author author);
}
