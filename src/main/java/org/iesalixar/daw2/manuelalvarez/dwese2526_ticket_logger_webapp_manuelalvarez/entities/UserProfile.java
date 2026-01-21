package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    /**
     * BIGINT PRIMARY KEY, también FK a users.id
     */
    @Id
    @Column(name = "user_id")
    private Long id;
/**
 * Relación 1:1 con User usando clave primaria compartida.
 * 'user_id' actúa como PK y FK a 'users.id'.
 */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    /**
     * VARCHAR(100) NOT NULL
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    /**
     * VARCHAR(100) NOT NULL
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    /** VARCHAR(30) NULL */
    @Column(name = "phone_number", length = 30) private String phoneNumber;
    /** VARCHAR(255) NULL
     Ruta/URL de la imagen de perfil */
    @Column(name = "profile_image", length = 255)
    private String profileImage;
    /** VARCHAR(500) NULL Pequeña descripción / biografía */
    @Column(name = "bio", length = 500)
    private String bio;
    /** VARCHAR(10) NULL - Código de idioma/locale (es_ES, en_US, ...) */ @Column(name = "locale", length = 10)
    private String locale;
    /** DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP (gestionado por la BD) */ @Column(name = "created_at", nullable = false, updatable = false, insertable = false) private LocalDateTime createdAt;
    /** DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP (BD) */ @Column(name = "updated_at", nullable = false, insertable = false, updatable = false) private LocalDateTime updatedAt;
    public UserProfile (User user,
                        String firstName,
                        String lastName,
                        String phoneNumber,
                        String profileImage,
                        String bio,
                        String locale) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.bio = bio;
        this.locale = locale;
    }

}