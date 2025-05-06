package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {
    BORROWED, //Kitap şu anda patronun elindedir.
    RETURNED, //Kitap zamanında veya gecikmeyle kütüphaneye başarıyla iade edilmiştir.
    OVERDUE, //İade tarihi geçmiştir ancak kitap henüz iade edilmemiştir.
    LOST // Bu ödünç kaydıyla ilgili kopya kayıp ilan edilmiştir.
}