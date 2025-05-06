package com.burakyapici.library.common.mapper;

import com.burakyapici.library.domain.dto.GenreDto;
import com.burakyapici.library.domain.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    GenreDto genreToGenreDTO(Genre genre);
}
