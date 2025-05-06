package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.api.dto.response.BookDetailResponse;
import com.burakyapici.library.api.dto.response.BookResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.service.AuthorService;
import com.burakyapici.library.service.GenreService;
import com.burakyapici.library.service.WaitListService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDto bookToBookDto(Book book);
    List<BookDto> bookListToBookDtoList(List<Book> books);
    BookDetailDto bookToBookDetailDto(Book book);


    BookResponse bookDtoToBookResponse(BookDto bookDto);
    BookDetailResponse bookDetailDtoToBookDetailResponse(BookDetailDto bookDetailDto);

    PageableResponse<BookResponse> bookPageableDtoListToPageableResponse(PageableDto<BookDto> pageableDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "copies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "author", source = "authorIds", qualifiedByName = "mapAuthors")
    @Mapping(target = "genres", source = "genreIds", qualifiedByName = "mapGenres")
    @Mapping(target = "waitList", source = "waitListIds", qualifiedByName = "mapWaitLists")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBookFromBookUpdateRequest(
        BookUpdateRequest bookUpdateRequest,
        @MappingTarget Book book,
        @Context AuthorService authorService,
        @Context GenreService genreService,
        @Context WaitListService waitListService
    );

    @Named("mapAuthors")
    default Set<Author> mapAuthors(Set<UUID> authorIds, @Context AuthorService authorService) {
        return Optional.ofNullable(authorIds)
            .filter(ids -> !ids.isEmpty())
            .map(authorService::getAuthorsByIdsOrElseThrow)
            .orElse(null);
    }

    @Named("mapGenres")
    default Set<Genre> mapGenres(Set<UUID> genreIds, @Context GenreService genreService) {
        return Optional.ofNullable(genreIds)
            .filter(ids -> !ids.isEmpty())
            .map(genreService::getGenresByIdsOrElseThrow)
            .orElse(null);
    }

    @Named("mapWaitLists")
    default Set<WaitList> mapWaitLists(Set<UUID> waitListIds, @Context WaitListService waitListService) {
        return Optional.ofNullable(waitListIds)
            .filter(ids -> !ids.isEmpty())
            .map(waitListService::getWaitListsByIdsOrElseThrow)
            .orElse(null);
    }
}
