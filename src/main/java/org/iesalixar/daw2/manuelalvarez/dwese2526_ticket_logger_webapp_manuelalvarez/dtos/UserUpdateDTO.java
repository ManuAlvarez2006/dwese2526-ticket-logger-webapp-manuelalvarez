package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class UserUpdateDTO {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 15)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String passwordHash;

    @NotNull
    private Boolean accountNonLocked = false;

    @NotNull
    private Boolean active = false;

    @Past
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastPasswordChange;

    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime passwordExpiresAt;

    @Min(0)
    @Max(100)
    private Integer failedLoginAttempts = 0;

    @NotNull
    private Boolean emailVerified = false;

    @NotNull
    private Boolean mustChangePassword = false;
}
