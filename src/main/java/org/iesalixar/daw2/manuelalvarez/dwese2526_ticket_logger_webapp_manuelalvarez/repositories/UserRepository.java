package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories;



import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Comprueba si existe una región con el código indicado.
     * Equivalente a: existsUserByCode(code).
     *
     * @param email nombre de la región
     * @return true si existe; false si no
     */
    boolean existsByEmail(String email);

    /**
     * Comprueba si existe una región con el código indicado excluyendo un id.
     * Equivalente a: existsUserByCodeAndNotId(code, id).
     *
     * @param id   id que se excluye (normalmente el que estás editando)
     * @return true si existe otra región con ese código; false si n* */
    boolean existsByEmailAndIdNot(String email, Long id);
    /**
     * Busca por id.
     * Equivalente a: getUserById(id), pero usando Optional para evitar null.
     */
    /**
     * Recupera una {@link User} por su id cargando también sus {@code provinces) en la misma consulta.
     * Se usa un <i>fetch join</i> para evitar problemas de carga perezosa (por ejemplo,
     * {@code LazyInitializationException}) cuando la vista o el mapeo a DTO necesita acceder
     * a la colección de provincias fuera del contexto de persistencia.
     * </p>
     *
     * @param id identificador de la región
     * @return {@link Optional) con la región (incluyendo provincias) si existe; {@link Optional#empty()} si no existe
     */
    @Query("select u from User u where u.id = :id")
    Optional<User> findById(Long id);

    Optional<User> findRolesById(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    /**
     * Localiza un usuario por email (ignorando mayúsculas/minúsculas) y asegura que sus roles
     * queden cargados en la misma consulta.
     *
     * @param email email del usuario (usado como identificador/username del sistema).
     * @return {@link java.util.Optional} con el usuario y sus roles; {@code Optional.empty()} si no existe.
     */
    Optional<User> findByEmailIgnoreCase(String email);
}

