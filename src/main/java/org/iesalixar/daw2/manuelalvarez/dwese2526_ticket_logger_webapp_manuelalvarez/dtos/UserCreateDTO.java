package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {

    private Long id;

    @NotEmpty(message = "{msg.user.email.notEmpty}")
    @Size(max = 15, message = "{msg.user.email.size}")
    private String email;

    @NotEmpty(message = "{msg.user.passwordHash.notEmpty}")
    @Size(max = 100, message = "{msg.user.passwordHash.notEmpty}")
    private String passwordHash;

    private Boolean accountNonLocked = true;

    private Boolean active = true;

    private LocalDateTime lastPasswordChange = LocalDateTime.now();

    private LocalDateTime passwordExpiresAt = LocalDateTime.now().plusDays(90);

    private Integer failedLoginAttempts = 0;

    private Boolean emailVerified = false;


    private Boolean mustChangePassword = true;
}