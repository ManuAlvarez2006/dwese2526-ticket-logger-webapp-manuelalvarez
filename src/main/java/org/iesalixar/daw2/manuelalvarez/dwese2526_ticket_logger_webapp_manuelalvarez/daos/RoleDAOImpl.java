package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
// Indica que todos los métodos de esta clase deben ejecutarse dentro de una transacción.
// Si algún método lanza una excepción en tiempo de ejecución, la transacción se revierte automáticamente (rollback).
// Esto asegura la integridad de los datos: todas las operaciones dentro del método se confirman o se cancelan juntas. public class RoleDAOImpl implements RoleDAO {

public class RoleDAOImpl implements RoleDAO {

    private static final Logger logger = LoggerFactory.getLogger(RoleDAOImpl.class);


    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lista todos los roles de la base de datos.
     *
     * @return Lista de roles
     */
    public List<Role> listAllRoles() {
        logger.info("Listing all roles from the database.");
        String hql = "SELECT r FROM Role r ORDER BY r.name";
        List<Role> roles = entityManager.createQuery(hql, Role.class).getResultList();
        logger.info("Retrieved {} roles from the database.", roles.size());
        return roles;
    }

    public List<Role> findAllByIds (Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            logger.info("findAllByIds called with null or empty ids. Returning empty list.");
            return List.of();
        }
        logger.info("Finding roles by ids: {}", ids);
        String hql = "SELECT r FROM Role r WHERE r.id IN :ids";
        List<Role> roles = entityManager.createQuery (hql, Role.class)
                .setParameter("ids", ids)
                .getResultList();
        logger.info("Found {} roles matching the given ids.", roles.size());
        return roles;
    }
}
