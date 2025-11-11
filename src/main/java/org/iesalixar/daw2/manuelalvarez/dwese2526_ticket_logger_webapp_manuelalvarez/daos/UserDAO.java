package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;



import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;

import java.util.List;

public interface UserDAO {

    List<User> listAllUsers() ;
    void insertUser(User user) ;
    void updateUser(User user)  ;
    void deleteUser(Long id) ;
    User getUsersById(Long id) ;
    boolean existsUserByCode(String username) ;
    boolean existsUserByCodeAndNotId(String Username, Long id) ;


}
