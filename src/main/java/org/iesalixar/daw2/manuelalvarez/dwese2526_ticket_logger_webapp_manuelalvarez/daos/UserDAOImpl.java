package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@Repository
public class UserDAOImpl implements UserDAO{


    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserDAOImpl.class);


    private final JdbcTemplate jdbcTemplate;


    public UserDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> listAllUsers(){
        logger.info("Listing all users from the database");
        String hql = "SELECT u FROM User u";
        List<User> users = entityManager.createQuery(hql, User.class).getResultList();
        logger.info("Retrieved {} users from the database", users.size());
        return users;
    }
    @Override
    public void insertUser(User user) {
        logger.info("Inserting user with email: {} , passwordHash: {} , accountNonLocked: {}, active: {} , lastPasswordChange: {} , passwordExpiresAt: {}, failedLoginAttempts: {}, emailVerified: {}, mustChangePassword: {}", user.getEmail(), user.getPasswordHash(), user.getAccountNonLocked(), user.getActive(),user.getLastPasswordChange(),user.getPasswordExpiresAt(),user.getFailedLoginAttempts(),user.getEmailVerified(),user.getMustChangePassword());
        if(user.getLastPasswordChange() != null){
            user.setPasswordExpiresAt(user.getLastPasswordChange().plusMonths(3));
        }
        entityManager.persist(user);
        logger.info("Inserted user succesful");


    }

    @Override
    public boolean existsUserByEmail(String email) {
        logger.info("Checking if user with email: {} exists", email);
        String hql = "SELECT COUNT(u) FROM User u WHERE UPPER(u.email) :email";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("email", email.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("User with email: {} exists: {}", email, exists);
        return exists;
    }

    @Override
    public List<User> listUsersPage(int page, int size, String sortField, String sortDir) {
        logger.info("Listing users page={}, size={},sortField={}, sortDir={} from the database.", page, size,sortField, sortDir);
        int offset = page * size;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
// 2. Determinar el campo de ordenación permitido (whitelist)
        Path<?> sortPath;
        switch (sortField) {
            case "id" -> sortPath = root.get("id");
            case "email" -> sortPath = root.get("email");
            case "passwordHash" -> sortPath = root.get("passwordHash");
            case "accountNonLocked" -> sortPath = root.get("accountNonLocked");
            case "active" -> sortPath = root.get("active");
            case "lastPasswordChange" -> sortPath = root.get("lastPasswordChange");
            case "passwordExpiresAt" -> sortPath = root.get("passwordExpiresAt");
            case "failedLoginAttempts" -> sortPath = root.get("failedLoginAttempts");
            case "emailVerified" -> sortPath = root.get("emailVerified");
            case "mustChangePassword" -> sortPath = root.get("mustChangePassword");
            default -> {
                logger.warn("Unknown sortField '{}', defaulting to 'email'.", sortField);
                sortPath = root.get("email");
            }
        }
// 3. Dirección de ordenación
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        Order order = descending ? cb.desc(sortPath) : cb.asc(sortPath);
// 4. Aplicar ordenación a la query
        cq.select(root).orderBy(order);
// 5. Crear TypedQuery, aplicar paginación y ejecutar
        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countUsers() {
        String hql = "SELECT COUNT(u) FROM User u";
        Long total = entityManager.createQuery(hql, Long.class).getSingleResult();
        return (total != null) ? total : 0L;
    }


    @Override
    public void updateUser(User user) {
        logger.info("Updating user with id: {}", user.getId());
        if(user.getLastPasswordChange() != null){
            user.setPasswordExpiresAt(user.getLastPasswordChange().plusMonths(3));
        }
        entityManager.merge(user);
        logger.info("Updated user with id: {}", user.getId());
    }
    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        User user = entityManager.find(User.class, id);
        if(user != null){
            entityManager.remove(user);
            logger.info("Deleted user with id: {}", id);
        } else{
            logger.warn("User with id: {} not found", id);
        }
    }

    @Override
    public User getUsersByEmail (String email) {
        if (email == null) return null;

        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    @Override
    public boolean existsUserByEmailAndNotId(String email, Long id) {
        logger.info("Checking if user with email: {} exists excluding id: {}", email, id);
        String hql = "SELECT COUNT(u) FROM User u WHERE UPPER(u.email) :email AND u.id != :id";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("email", email.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("User with email: {} exists excluding id {}: {}", email, id, exists);
        return exists;
    }
}

