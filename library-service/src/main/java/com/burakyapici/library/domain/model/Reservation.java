package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Book.class)
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable=false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable=false)
    private User user;

    @Column(nullable = false)
    private OffsetDateTime reservationDate;

    @Column(nullable = false)
    private OffsetDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private boolean notificationSent;
}
