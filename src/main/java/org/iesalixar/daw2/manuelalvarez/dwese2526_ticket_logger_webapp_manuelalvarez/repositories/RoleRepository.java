package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Opción 1: usar el método de JpaRepository
    // List<Role> findAll();  <-- ya viene incluido

    // Opción 2: si quieres un método explícito
    @Query("SELECT r FROM Role r")
    List<Role> listAllRoles();

    // Buscar roles por un conjunto de ids
    List<Role> findAllByIdIn(Set<Long> ids);

    Optional<Role> findByName(String name);
}
