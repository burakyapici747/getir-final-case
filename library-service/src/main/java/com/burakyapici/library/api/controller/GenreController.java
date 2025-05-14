package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.GenreCreateRequest;
import com.burakyapici.library.api.dto.request.GenreUpdateRequest;
import com.burakyapici.library.api.dto.response.ApiResponse;
import com.burakyapici.library.api.dto.response.GenreResponse;
import com.burakyapici.library.common.mapper.GenreMapper;
import com.burakyapici.library.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getGenres() {
        List<GenreResponse> genres = GenreMapper.INSTANCE.toGenreResponseList(genreService.getAllGenres());
        return ApiResponse.okResponse(genres, "Genres retrieved successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreById(@PathVariable("id") UUID id) {
        return ApiResponse.okResponse(
            GenreMapper.INSTANCE.toGenreResponse(genreService.getGenreById(id)),
            "Genre retrieved successfully."
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<GenreResponse>> createGenre(@RequestBody GenreCreateRequest genreCreateRequest) {
        GenreResponse createdGenre = GenreMapper.INSTANCE.toGenreResponse(genreService.createGenre(genreCreateRequest));

        return ApiResponse.createdResponse(createdGenre, "Book created successfully.", createdGenre.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<GenreResponse>> updateGenreById(
        @PathVariable("id") UUID id,
        @RequestBody GenreUpdateRequest genreUpdateRequest
    ) {
        GenreResponse updatedGenre = GenreMapper.INSTANCE.toGenreResponse(
            genreService.updateGenreById(id, genreUpdateRequest)
        );

        return ApiResponse.okResponse(updatedGenre, "Genre updated successfully.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteGenreById(@PathVariable String id) {
        genreService.deleteGenreById(UUID.fromString(id));
        return ApiResponse.noContentResponse("Genre deleted successfully.");
    }
}
