package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.request.GenreUpdateRequest;
import com.burakyapici.library.api.dto.response.GenreResponse;
import com.burakyapici.library.domain.dto.GenreDto;
import com.burakyapici.library.domain.model.Genre;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "default")
public interface GenreMapper {
    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    GenreDto toGenreDto(Genre genre);

    GenreResponse toGenreResponse(GenreDto genreDto);

    List<GenreDto> toGenreDtoList(List<Genre> genres);

    List<GenreResponse> toGenreResponseList(List<GenreDto> genreDtoList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGenreFromGenreUpdateRequest(GenreUpdateRequest genreUpdateRequest, @MappingTarget Genre genre);
}
