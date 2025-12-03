package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
        logger.info("Inserting user with username: {} , passwordHash: {} , accountNonLocked: {}, active: {} , lastPasswordChange: {} , passwordExpiresAt: {}, failedLoginAttempts: {}, emailVerified: {}, mustChangePassword: {}", user.getUsername(), user.getPasswordHash(), user.getAccountNonLocked(), user.getActive(),user.getLastPasswordChange(),user.getPasswordExpiresAt(),user.getFailedLoginAttempts(),user.getEmailVerified(),user.getMustChangePassword());
        if(user.getLastPasswordChange() != null){
            user.setPasswordExpiresAt(user.getLastPasswordChange().plusMonths(3));
        }
        entityManager.persist(user);
        logger.info("Inserted user succesful");


    }

    @Override
    public boolean existsUserByCode(String username)  {
        String hql = "SELECT COUNT(u) FROM User u WHERE UPPER(u.username) = :username";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("username", username.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Region with username: {} exists: {}", username, exists);
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
            case "username" -> sortPath = root.get("username");
            case "passwordHash" -> sortPath = root.get("passwordHash");
            case "accountNonLocked" -> sortPath = root.get("accountNonLocked");
            case "active" -> sortPath = root.get("active");
            case "lastPasswordChange" -> sortPath = root.get("lastPasswordChange");
            case "passwordExpiresAt" -> sortPath = root.get("passwordExpiresAt");
            case "failedLoginAttempts" -> sortPath = root.get("failedLoginAttempts");
            case "emailVerified" -> sortPath = root.get("emailVerified");
            case "mustChangePassword" -> sortPath = root.get("mustChangePassword");
            default -> {
                logger.warn("Unknown sortField '{}', defaulting to 'username'.", sortField);
                sortPath = root.get("username");
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
    public User getUsersById(Long id) {
        logger.info("Retrieving user by id: {}", id);
        User user = entityManager.find(User.class, id);
        if (user != null) {
            logger.info("User retrieved: {} - {} - {} - {} - {} - {} - {} - {} - {}", user.getUsername(), user.getPasswordHash(), user.getAccountNonLocked(),user.getActive(),user.getLastPasswordChange(),user.getPasswordExpiresAt(),user.getFailedLoginAttempts(),user.getEmailVerified(),user.getMustChangePassword());
        } else {
            logger.warn("No user found with id: {}", id);
        }
        return user;
    }
    @Override
    public boolean existsUserByCodeAndNotId(String username, Long id){
        logger.info("Checking if user with username: {} exists excluding id: {}", username, id);
        String hql = "SELECT COUNT(u) FROM User u WHERE UPPER(u.username) = :username AND u.id <> :id";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("username", username.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Username with username {} exists: {}", username, exists);
        return exists;
    }
}

