package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.GenreCreateRequest;
import com.burakyapici.library.api.dto.request.GenreUpdateRequest;
import com.burakyapici.library.common.mapper.GenreMapper;
import com.burakyapici.library.domain.dto.GenreDto;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.repository.GenreRepository;
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
            .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
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

            throw new EntityNotFoundException(
                "The following genre IDs could not be found: " +
                missingGenreIds.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(", "))
            );
        }

        return new HashSet<>(genres);
    }

    @Override
    public GenreDto getGenreById(UUID id) {
        return GenreMapper.INSTANCE.toGenreDto(getGenreByIdOrElseThrow(id));
    }

    @Override
    public List<GenreDto> getAllGenres() {
        return GenreMapper.INSTANCE.toGenreDtoList(genreRepository.findAll());
    }

    @Override
    public GenreDto createGenre(GenreCreateRequest genreCreateRequest) {
        validateIsNotExist(genreCreateRequest.name());
        Genre genre = Genre.builder()
            .name(genreCreateRequest.name())
            .description(genreCreateRequest.description())
            .build();

        return GenreMapper.INSTANCE.toGenreDto(genreRepository.save(genre));
    }

    @Override
    public GenreDto updateGenreById(UUID id, GenreUpdateRequest genreUpdateRequest) {
        Genre genre = findByIdOrElseThrow(id);

        GenreMapper.INSTANCE.updateGenreFromGenreUpdateRequest(genreUpdateRequest, genre);

        return GenreMapper.INSTANCE.toGenreDto(genreRepository.save(genre));
    }

    @Override
    public void deleteGenreById(UUID id) {
        Genre genre = findByIdOrElseThrow(id);
        genreRepository.delete(genre);
    }

    private Genre findByIdOrElseThrow(UUID id) {
        return genreRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
    }

    private void validateIsNotExist(String name){
        if(genreRepository.existsByName(name)){
            throw new DataConflictException("Genre with name " + name + " already exists");
        }
    }
}
