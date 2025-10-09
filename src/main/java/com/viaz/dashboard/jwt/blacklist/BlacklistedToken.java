package com.viaz.dashboard.jwt.blacklist;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "blacklisted_tokens",
    indexes = {
        @Index(name = "idx_token", columnList = "token"),
        @Index(name = "idx_expiration_date", columnList = "expirationDate")
    }
)
public class BlacklistedToken {
    @Id
    private UUID id;

    private String token;

    private Instant expirationDate;

    // Constructor for creating a new BlacklistedToken
    public BlacklistedToken(String token, Instant expirationDate) {
        this.id = UUID.randomUUID();
        this.token = token;
        this.expirationDate = expirationDate;
    }
}
