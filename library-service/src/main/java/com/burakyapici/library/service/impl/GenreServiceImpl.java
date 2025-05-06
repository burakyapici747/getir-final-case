package com.burakyapici.library.service.impl;

import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.repository.GenreRepository;
import com.burakyapici.library.exception.GenreNotFoundException;
import com.burakyapici.library.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre getGenreByIdOrElseThrow(UUID id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
    }

    public Set<Genre> getGenresByIdsOrElseThrow(Set<UUID> genreIds) {
        List<Genre> genres = genreRepository.findAllById(genreIds);

        if (genres.size() != genreIds.size()) {
            Set<UUID> foundGenreIds = genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<UUID> missingGenreIds = genreIds.stream()
                    .filter(id -> !foundGenreIds.contains(id))
                    .collect(Collectors.toSet());

            throw new GenreNotFoundException(
                "The following genre IDs could not be found: " +
                    missingGenreIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.joining(", "))
            );
        }

        return new HashSet<>(genres);
    }
}
