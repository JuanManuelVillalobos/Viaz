package com.viaz.dashboard.features.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viaz.dashboard.features.role.Role;
import com.viaz.dashboard.features.shipment.Shipment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "viaz_dashboard_users")
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class ViazUser {

    public enum UserStatus {
        ACTIVE, PENDING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private Long userId;


    @Column(unique = true, nullable = false)
    private String username;

    @Column(length = 100)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "customerServiceAgent", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<Shipment> shipments = new HashSet<>();

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "account_non_expired")
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired = true;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "last_login_date")
    private Instant lastLoginDate;

    @Column(name = "password_changed_date")
    private Instant passwordChangedDate;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    public ViazUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public ViazUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountNonLocked = false;
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    public void recordSuccessfulLogin() {
        this.lastLoginDate = Instant.now();
        resetFailedLoginAttempts();
        this.status = UserStatus.ACTIVE;
    }

    public void recordPasswordChange() {
        this.passwordChangedDate = Instant.now();
        this.credentialsNonExpired = true;
    }

    public void setInactive() {
        this.enabled = false;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.enabled = true;
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
    }

    public void activateAccount() {
        this.status = UserStatus.ACTIVE;
        this.enabled = true;
    }

    @Override
    public String toString() {
        return username;
    }
}
