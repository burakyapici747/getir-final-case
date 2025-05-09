package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.WaitListStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wait_list")
public class WaitList extends BaseModel {
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Book.class)
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable=false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable=false)
    private User user;

    @Column(name = "start_date",nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private WaitListStatus status;
}
