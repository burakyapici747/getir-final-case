package com.burakyapici.library.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@Table(name = "author")
@NoArgsConstructor
@AllArgsConstructor
public class Author extends BaseModel {
    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Column(name = "date-of-birth")
    private LocalDate dateOfBirth;
}
