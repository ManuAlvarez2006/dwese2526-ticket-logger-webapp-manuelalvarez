package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // Devuelve Optional para evitar null
    Optional<UserProfile> findByUserId(Long userId);

    // Comprueba existencia
    boolean existsByUserId(Long userId);

    // save() ya viene de JpaRepository, no hace falta declarar saveOrUpdate
}
