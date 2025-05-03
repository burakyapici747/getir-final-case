package com.burakyapici.library.domain.enums;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum Role {
    PATRON("Patron", "Library member with borrowing privileges", false),
    LIBRARIAN("Librarian", "Library staff with book management access", true);

    private final String displayName;
    private final String description;
    private final boolean hasAdminAccess;

    Role(String displayName, String description, boolean hasAdminAccess) {
        this.displayName = displayName;
        this.description = description;
        this.hasAdminAccess = hasAdminAccess;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    public SimpleGrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority(getAuthority());
    }
}