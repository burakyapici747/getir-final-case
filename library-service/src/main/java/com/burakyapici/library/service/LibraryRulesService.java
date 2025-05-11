package com.burakyapici.library.service;

import com.burakyapici.library.config.LibraryRulesConfig;
import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.model.Borrowing;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class LibraryRulesService {
    private final LibraryRulesConfig rulesConfig;
    
    public LibraryRulesService(LibraryRulesConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
    }
    
    /**
     * Bir kullanıcının daha fazla kitap ödünç alıp alamayacağını kontrol eder
     */
    public boolean canPatronBorrowMoreBooks(int currentlyBorrowedCount) {
        return currentlyBorrowedCount < rulesConfig.getMaxBooksPerPatron();
    }
    
    /**
     * Bir kullanıcının daha fazla rezervasyon yapıp yapamayacağını kontrol eder
     */
    public boolean canPatronMakeMoreReservations(int currentReservationCount) {
        return currentReservationCount < rulesConfig.getMaxReservationsPerPatron();
    }
    
    /**
     * Bir kitabın süresini uzatıp uzatamayacağını kontrol eder
     */
    public boolean canRenewBook(Borrowing borrowing, int currentRenewalCount) {
        // Maksimum yenileme sayısı kontrolü
        if (currentRenewalCount >= rulesConfig.getMaxRenewalCount()) {
            return false;
        }

        // Kitap gecikmiş mi kontrolü
        if (borrowing.getStatus() == BorrowStatus.OVERDUE) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Ödünç alma için son tarih hesaplar
     */
    public LocalDateTime calculateDueDate(LocalDateTime borrowDate) {
        return borrowDate.plusDays(rulesConfig.getMaxLoanPeriodDays());
    }
    
    /**
     * Yenileme durumunda yeni son tarih hesaplar
     */
    public LocalDateTime calculateRenewalDueDate(LocalDateTime currentDueDate) {
        return currentDueDate.plusDays(rulesConfig.getRenewalPeriodDays());
    }
    
    /**
     * Gecikme cezası hesaplar
     */
    public double calculateOverdueFine(LocalDateTime dueDate, LocalDateTime returnDate) {
        if (returnDate.isBefore(dueDate.plusDays(rulesConfig.getGracePeriodDays()))) {
            return 0.0; // Tolerans süresi içinde
        }
        
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
        double fine = daysOverdue * rulesConfig.getOverdueFinePerDay();
        
        // Maksimum ceza limiti kontrolü
        return Math.min(fine, rulesConfig.getMaxFineAmount());
    }
    
    /**
     * Kitap hasarı veya kaybı durumunda ceza hesaplar
     */
    public double calculatePenalty(BorrowStatus status) {
        if (status == BorrowStatus.LOST) {
            return rulesConfig.getLostBookPenalty();
        } else if (status == BorrowStatus.RETURNED_DAMAGED) {
            return rulesConfig.getDamagedBookPenalty();
        }
        return 0.0;
    }
    
    /**
     * Kullanıcının engellenmesi gerekip gerekmediğini kontrol eder
     */
    public boolean shouldBlockPatron(double totalFineAmount) {
        return totalFineAmount >= rulesConfig.getBlockThresholdAmount();
    }

    /**
     * Rezervasyon süresi dolmuş mu kontrol eder
     */
    public boolean isReservationExpired(LocalDateTime readyForPickupDate) {
        LocalDateTime expiryDate = readyForPickupDate.plusDays(rulesConfig.getMaxReservationHoldDays());
        return LocalDateTime.now().isAfter(expiryDate);
    }
} 