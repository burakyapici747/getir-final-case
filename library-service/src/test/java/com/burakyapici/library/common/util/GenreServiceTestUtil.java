package com.burakyapici.library.common.util;

import com.burakyapici.library.domain.model.Genre;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenreServiceTestUtil {

    public static Genre createSampleGenre() {
        return Genre.builder()
            .name("Fiction")
            .description("Fiction books")
            .build();
    }

    public static Set<Genre> createSampleGenres(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> Genre.builder()
                .name("Fiction" + i)
                .description("Fiction books" + i)
                .build())

            .collect(Collectors.toSet());
    }
}