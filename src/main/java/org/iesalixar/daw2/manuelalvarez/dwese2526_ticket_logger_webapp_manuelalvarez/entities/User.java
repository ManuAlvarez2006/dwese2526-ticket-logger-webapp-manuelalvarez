package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString(exclude = "profile")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.user.email.notEmpty}")
    @Size(max = 50, message = "{msg.user.email.size}")
    @Column(name = "email", nullable = false, length = 50)
    private String email;

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

    public User(String email, String passwordHash, Boolean active, Boolean accountNonLocked,
                LocalDateTime lastPasswordChange, LocalDateTime passwordExpiresAt,
                Integer failedLoginAttempts, Boolean emailVerified, Boolean mustChangePassword) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.accountNonLocked = accountNonLocked;
        this.lastPasswordChange = lastPasswordChange;
        this.passwordExpiresAt = passwordExpiresAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.emailVerified = emailVerified;
        this.mustChangePassword = mustChangePassword;
    }

    public User(Long id, String email, String passwordHash) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserProfile profile;

    @ManyToMany (fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
    joinColumns= @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns= @JoinColumn(name = "role_id", referencedColumnName = "id")
    )

    private Set<Role> roles = new HashSet<>();
}