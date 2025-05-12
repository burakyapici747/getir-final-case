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
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.service.AuthorService;
import com.burakyapici.library.service.GenreService;
import com.burakyapici.library.service.WaitListService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Stream;

@Mapper(
    componentModel = "default",
    collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    uses = { WaitListMapper.class }
)
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDto toBookDto(Book book);

    List<BookDto> toBookDtoList(List<Book> books);

    List<BookResponse> toResponse(List<BookDto> bookDtoList);

    @Mapping(target = "availableCopies", expression = "java(getBookCopiesCount(book.getBookCopies()))")
    BookDetailDto toBookDetailDto(Book book);

    BookResponse toResponse(BookDto bookDto);

    BookDetailResponse toDetailResponse(BookDetailDto bookDetailDto);

    PageableResponse<BookResponse> toPageableResponse(PageableDto<BookDto> pageableDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "waitLists", ignore = true)
    @Mapping(
        target                     = "authors",
        source                     = "authorIds",
        qualifiedByName            = "mapAuthors",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(
        target                     = "genres",
        source                     = "genreIds",
        qualifiedByName            = "mapGenres",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @BeanMapping(
        nullValueCheckStrategy           = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
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

    @Named("getBookCopiesCount")
    default int getBookCopiesCount(Set<BookCopy> bookCopies) {
        return Stream.ofNullable(bookCopies)
            .map(Set::size)
            .findFirst()
            .orElse(0);
    }
}
