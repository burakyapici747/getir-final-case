package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.domain.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_users_phone_number", columnNames = "phone_number")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseModel {
    @Email
    @NotBlank
    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "patron_status")
    private PatronStatus patronStatus;

    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role.toGrantedAuthority());
    }

    @Transient
    public boolean isPatron() {
        return role == Role.PATRON;
    }

    @Transient
    public boolean isLibrarian() {
        return role == Role.LIBRARIAN;
    }

    public Role getRole() {
        return role;
    }

    public PatronStatus getPatronStatus() {
        return patronStatus;
    }

    @Override
    public UUID getId() {
        return super.getId();
    }
}
