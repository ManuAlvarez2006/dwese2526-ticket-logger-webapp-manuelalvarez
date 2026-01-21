package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;

import java.util.List;

public interface UserDAO {

    List<User> listAllUsers() ;
    void insertUser(User user) ;
    void updateUser(User user)  ;
    void deleteUser(Long id) ;
    User getUsersByEmail(String email) ;
    boolean existsUserByEmail(String email) ;
    boolean existsUserByEmailAndNotId(String email, Long id) ;
    List<User> listUsersPage(int page, int size, String sortField, String sortDir);
    long countUsers();

    User getUserById(Long userId);

    boolean existsUserByCode(@NotEmpty(message = "{msg.user.username.notEmpty}") @Size(max = 15, message = "{msg.user.username.size}") String username);

    boolean existsUserByCodeAndNotId(@NotBlank @Size(max = 15) String username, @NotNull Long id);
}
