package com.burakyapici.library.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "genre")
public class Genre extends BaseModel {
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @ManyToMany(mappedBy = "genres")
    private List<Book> books = new ArrayList<>();
}
