package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.user.code.notEmpty}")
    @Size(max = 15, message = "{msg.user.code.size}")
    @Column(name = "username", nullable = false, length = 15)
    private String username;

    @NotEmpty(message = "{msg.user.passwordHash.notEmpty}")
    @Size(max = 100, message = "{msg.user.passwordHash.notEmpty}")
    @Column(name = "passwordHash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "accountNonLocked", nullable = false)
    private Boolean accountNonLocked = true;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "lastPasswordChange")
    private LocalDateTime lastPasswordChange = LocalDateTime.now();

    @Column(name = "passwordExpiresAt")
    private LocalDateTime passwordExpiresAt = LocalDateTime.now().plusDays(90);

    @Column(name = "failedLoginAttempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "emailVerified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "mustChangePassword", nullable = false)
    private Boolean mustChangePassword = true;

    public User(String username, String passwordHash, Boolean active, Boolean accountNonLocked,
                LocalDateTime lastPasswordChange, LocalDateTime passwordExpiresAt,
                Integer failedLoginAttempts, Boolean emailVerified, Boolean mustChangePassword) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.active = active;
        this.accountNonLocked = accountNonLocked;
        this.lastPasswordChange = lastPasswordChange;
        this.passwordExpiresAt = passwordExpiresAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.emailVerified = emailVerified;
        this.mustChangePassword = mustChangePassword;
    }

    public User(Long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }
}