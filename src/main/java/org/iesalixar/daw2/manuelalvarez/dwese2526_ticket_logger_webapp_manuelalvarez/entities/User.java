package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data  // Esta anotación de Lombok genera automáticamente los siguientes métodos:
// - Getters y setters para todos los campos (id, code, name).
// - Los métodos `equals()` y `hashCode()` basados en todos los campos no transitorios.
// - El método `toString()` que incluye todos los campos.
// - Un método `canEqual()` que verifica si una instancia puede ser igual a otra.
// Esto evita tener que escribir manualmente todos estos métodos y mejora la mantenibilidad del código.


@NoArgsConstructor  // Esta anotación genera un constructor sin argumentos (constructor vacío),
//  es útil cuando quieres crear un objeto `User` sin inicializarlo inmediatamente
// con valores. Esto es muy útil en frameworks como Hibernate o JPA,
// que requieren un constructor vacío para la creación de entidades.


@AllArgsConstructor  // Esta anotación genera un constructor que acepta todos los campos como parámetros (id, code, name).
// Este constructor es útil cuando necesitas crear una instancia completamente inicializada de `User`.



public class User {


    // Campo que almacena el identificador único de la región. Este campo suele ser autogenerado
    // por la base de datos, lo que lo convierte en un buen candidato para una clave primaria.
    // No añadimos validación en el ID porque en este caso puede ser nulo al insertarse
    private Long id;


    // Campo que almacena el código de la región, normalmente una cadena corta que identifica la región.
    // Ejemplo: "01" para Andalucía.
    @NotEmpty(message = "{msg.user.code.notEmpty}")
    @Size(max = 15, message = "{msg.user.code.size}")
    private String username;


    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @NotEmpty(message = "{msg.user.passwordHash.notEmpty}")
    @Size(max = 100, message = "{msg.user.passwordHash.notEmpty}")
    private String passwordHash;

    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @NotNull
    private Boolean accountNonLocked;

    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @NotNull
    private Boolean active;

    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @Past
    @NotNull
    private LocalDateTime lastPasswordChange;

    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @Future
    @NotNull
    private LocalDateTime passwordExpiresAt;

    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @Min(0)
    @Max(100)
    private Integer failedLoginAttempts;

    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @NotNull
    private Boolean emailVerified;

    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".

    @NotNull
    private Boolean mustChangePassword;

    // Constructor para crear un usuario sin id (usado en inserciones)
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
