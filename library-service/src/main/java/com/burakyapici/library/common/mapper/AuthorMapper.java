package com.burakyapici.library.common.mapper;

import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    AuthorDto toDto(Author author);
}
