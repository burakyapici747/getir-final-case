package com.burakyapici.library.service;

import com.burakyapici.library.domain.model.Genre;

import java.util.Set;
import java.util.UUID;

public interface GenreService {
    Genre getGenreByIdOrElseThrow(UUID id);
    Set<Genre> getGenresByIdsOrElseThrow(Set<UUID> genreIds);
}
