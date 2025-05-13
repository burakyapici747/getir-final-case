package com.burakyapici.library.common.util;

import com.burakyapici.library.domain.model.Author;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuthorServiceTestUtil {

    public static Author createSampleAuthor() {
        Author author = Author.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(1980, 1, 1))
            .build();
        // ID ataması, eğer BaseModel veya Author'da setId varsa yapılabilir.
        // Eğer ID veritabanı tarafından atanıyorsa, bu metot ID'siz bir Author döndürebilir.
        // Testlerde spesifik ID gerekiyorsa createSampleAuthorWithId kullanılmalı.
        // author.setId(UUID.randomUUID()); 
        return author;
    }

    public static Author createSampleAuthorWithId(UUID authorId) {
        Author author = Author.builder()
            .firstName("TestUtilFirstName")
            .lastName("TestUtilLastName")
            .dateOfBirth(LocalDate.of(1975, 5, 10))
            .build();
        author.setId(authorId); // Assuming Author extends BaseModel and has setId
        return author;
    }

    public static Set<Author> createSampleAuthors(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> {
                Author author = Author.builder()
                    .firstName("John" + i)
                    .lastName("Doe" + i)
                    .dateOfBirth(LocalDate.of(1980 + i, 1, 1))
                    .build();

                author.setId(UUID.randomUUID());
                return author;
            })
        .collect(Collectors.toSet());
    }
}