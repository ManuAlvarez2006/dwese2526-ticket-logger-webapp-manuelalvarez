package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;


import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public class UserDAOImpl implements UserDAO{

    private static final Logger logger = (Logger) LoggerFactory.getLogger(RegionDAOImpl.class);


    private final JdbcTemplate jdbcTemplate;


    public UserDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> listAllUsers(){
        logger.info("Listing all users from the database");
        String query = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(User.class));
        logger.info("Retrieved {} regions from the database", users.size());
        return users;
    }
    @Override
    public void insertUser(User user) {
        String query = "INSERT INTO users (username,passwordHash,accountNonLocked,active,lastPasswordChange,passwordExpiresAt,failedLoginAttempts,emailVerified,mustChangePassword) VALUES (?,?,?,?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(query,
                user.getUsername(),
                user.getPasswordHash(),
                user.getAccountNonLocked(),
                user.getActive(),
                user.getLastPasswordChange(),
                user.getPasswordExpiresAt(),
                user.getFailedLoginAttempts(),
                user.getEmailVerified(),
                user.getMustChangePassword());
        logger.info("Inserted user. Rows affected: {}", rowsAffected);
    }

    @Override
    public boolean existsUserByCode(String username)  {
        logger.info("Checking if user with username: {} exists", username);
        String query = "SELECT COUNT(*) FROM users WHERE UPPER(username) = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, username.toUpperCase());
        boolean exists = count != null && count >0;
        logger.info("User with username: {} exists: {}", username, exists);
        return exists;

    }
    @Override
    public void updateUser(User user) {
        String query = "UPDATE users SET username = ?,passwordHash = ?, accountNonLocked = ?,active = ?,lastPasswordChange = ?, passwordExpiresAt = ?,failedLoginAttempts = ?, emailVerified = ?,mustChangePassword = ?  WHERE ID = ?";
        int rowsAffected = jdbcTemplate.update(query, user.getUsername(),user.getPasswordHash(),user.getAccountNonLocked(),user.getActive(),user.getLastPasswordChange(),user.getPasswordExpiresAt(),user.getFailedLoginAttempts(),user.getEmailVerified(),user.getMustChangePassword(),user.getId());
        logger.info("Updated user, Rows affected: {}", rowsAffected);
    }
    @Override
    public void deleteUser(Long id) {
        String query= "Delete FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(query,id);
        logger.info("Deleted user, Rows affected: {}", rowsAffected);
    }

    @Override
    public User getUsersById(Long id) {
        String query= "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(User.class), id);
            logger.info("User retrieved: {} - {}",user.getUsername(),user.getPasswordHash(),user.getAccountNonLocked(),user.getActive(),user.getLastPasswordChange(),user.getPasswordExpiresAt(),user.getFailedLoginAttempts(),user.getEmailVerified(),user.getMustChangePassword());
            return user;
        } catch (Exception e){
            logger.warn("No user found with id: {}", id);
            return null;
        }
    }
    @Override
    public boolean existsUserByCodeAndNotId(String username, Long id){
        String query = "SELECT COUNT(*) FROM users WHERE UPPER(username) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, username.toUpperCase(), id);
        boolean exists = count != null && count > 0;
        return exists;
    }
}

