package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDetailDTO {
    private long id;
    private String email;
    private String passwordHash;
    private Boolean accountNonLocked;
    private Boolean active;
    private LocalDateTime lastPasswordChange;
    private LocalDateTime passwordExpiresAt;
    private Integer failedLoginAttempts;
    private Boolean emailVerified;
    private Boolean mustChangePassword;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImage;
    private String bio;
    private String locale;

    private Set<String> roles;

}