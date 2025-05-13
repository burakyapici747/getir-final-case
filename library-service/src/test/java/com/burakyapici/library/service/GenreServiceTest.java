package com.burakyapici.library.service;

import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.common.util.GenreServiceTestUtil;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.repository.GenreRepository;
import com.burakyapici.library.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    @DisplayName("Should return the Genre entity for an existing ID")
    void givenExistingGenreId_whenGetGenreByIdOrElseThrow_thenReturnGenre() {
        UUID genreId = UUID.randomUUID();
        Genre expectedGenre = GenreServiceTestUtil.createSampleGenre();
        expectedGenre.setId(genreId);

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(expectedGenre));

        Genre actualGenre = genreService.getGenreByIdOrElseThrow(genreId);

        assertNotNull(actualGenre);
        assertEquals(expectedGenre, actualGenre);
        verify(genreRepository, times(1)).findById(genreId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when a non-existing ID is given")
    void givenNonExistingGenreId_whenGetGenreByIdOrElseThrow_thenThrowEntityNotFoundException() {
        UUID genreId = UUID.randomUUID();
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> genreService.getGenreByIdOrElseThrow(genreId)
        );

        assertEquals("Genre not found", exception.getMessage());
        verify(genreRepository, times(1)).findById(genreId);
    }

    @Test
    @DisplayName("Should return a set of Genres when all IDs exist")
    void givenExistingGenreIds_whenGetGenresByIdsOrElseThrow_thenReturnGenreSet() {
        int count = 3;
        Set<Genre> expectedGenres = GenreServiceTestUtil.createSampleGenres(count);
        Set<UUID> genreIds = expectedGenres.stream()
            .map(genre -> {
                genre.setId(UUID.randomUUID());
                return genre.getId();
            })
            .collect(Collectors.toSet());
        List<Genre> genreList = new ArrayList<>(expectedGenres);

        when(genreRepository.findAllById(genreIds)).thenReturn(genreList);

        Set<Genre> actualGenres = genreService.getGenresByIdsOrElseThrow(genreIds);

        assertNotNull(actualGenres);
        assertEquals(expectedGenres.size(), actualGenres.size());
        assertTrue(actualGenres.containsAll(expectedGenres));
        verify(genreRepository, times(1)).findAllById(genreIds);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when some IDs do not exist")
    void givenSomeNonExistingGenreIds_whenGetGenresByIdsOrElseThrow_thenThrowEntityNotFoundException() {
        int existingCount = 2;
        Set<Genre> existingGenres = GenreServiceTestUtil.createSampleGenres(existingCount);
        Set<UUID> existingGenreIds = existingGenres.stream()
            .map(genre -> {
                genre.setId(UUID.randomUUID());
                return genre.getId();
            })
            .collect(Collectors.toSet());

        UUID missingId1 = UUID.randomUUID();
        UUID missingId2 = UUID.randomUUID();
        Set<UUID> requestedIds = new HashSet<>(existingGenreIds);
        requestedIds.add(missingId1);
        requestedIds.add(missingId2);

        List<Genre> foundGenreList = new ArrayList<>(existingGenres);

        when(genreRepository.findAllById(requestedIds)).thenReturn(foundGenreList);

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> genreService.getGenresByIdsOrElseThrow(requestedIds)
        );

        String expectedMessagePart1 = "The following genre IDs could not be found:";
        String missingId1Str = missingId1.toString();
        String missingId2Str = missingId2.toString();

        assertTrue(exception.getMessage().startsWith(expectedMessagePart1));
        assertTrue(exception.getMessage().contains(missingId1Str));
        assertTrue(exception.getMessage().contains(missingId2Str));

        verify(genreRepository, times(1)).findAllById(requestedIds);
    }

    @Test
    @DisplayName("Should return an empty set when given an empty ID set")
    void givenEmptyGenreIds_whenGetGenresByIdsOrElseThrow_thenReturnEmptyGenreSet() {
        Set<UUID> emptyIds = Collections.emptySet();
        when(genreRepository.findAllById(emptyIds)).thenReturn(Collections.emptyList());

        Set<Genre> actualGenres = genreService.getGenresByIdsOrElseThrow(emptyIds);

        assertNotNull(actualGenres);
        assertTrue(actualGenres.isEmpty());
        verify(genreRepository, times(1)).findAllById(emptyIds);
    }

}
