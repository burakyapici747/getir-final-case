package com.burakyapici.library.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_detail")
public class BookDetail extends BaseModel {
    @Size(max = 255)
    private String publisher;

    @Size(max = 50)
    private String language;

    private Integer pageCount;

    @Lob
    private String description;

    @Size(max = 255)
    private String coverImageUrl;
}
