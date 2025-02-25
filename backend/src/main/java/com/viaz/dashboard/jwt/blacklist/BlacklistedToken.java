package com.viaz.dashboard.jwt.blacklist;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;
import java.util.UUID;


@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "blacklisted_token")
public class BlacklistedToken {
    @Id
    private UUID id; // No @GeneratedValue annotation

    private String token;

    private Date expirationDate; // Use LocalDateTime instead of Date

    // Constructor for creating a new BlacklistedToken
    public BlacklistedToken(String token, Date expirationDate) {
        this.id = UUID.randomUUID(); // Generate a new UUID
        this.token = token;
        this.expirationDate = expirationDate;
    }

}
