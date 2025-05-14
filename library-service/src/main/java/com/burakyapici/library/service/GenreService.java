package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.GenreCreateRequest;
import com.burakyapici.library.api.dto.request.GenreUpdateRequest;
import com.burakyapici.library.domain.dto.GenreDto;
import com.burakyapici.library.domain.model.Genre;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GenreService {
    Genre getGenreByIdOrElseThrow(UUID id);
    Set<Genre> getGenresByIdsOrElseThrow(Set<UUID> genreIds);
    GenreDto getGenreById(UUID id);
    List<GenreDto> getAllGenres();
    GenreDto createGenre(GenreCreateRequest genreCreateRequest);
    GenreDto updateGenreById(UUID id, GenreUpdateRequest genreUpdateRequest);
    void deleteGenreById(UUID id);
}
