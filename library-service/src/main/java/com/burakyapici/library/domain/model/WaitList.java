package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.WaitListStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wait_list")
public class WaitList extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable=false)
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private WaitListStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id")
    private BookCopy reservedBookCopy;
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public WaitListStatus getStatus() {
        return status;
    }
    
    public void setStatus(WaitListStatus status) {
        this.status = status;
    }
    
    public BookCopy getReservedBookCopy() {
        return reservedBookCopy;
    }
    
    public void setReservedBookCopy(BookCopy reservedBookCopy) {
        this.reservedBookCopy = reservedBookCopy;
    }
    
    @Override
    public UUID getId() {
        return super.getId();
    }
}
