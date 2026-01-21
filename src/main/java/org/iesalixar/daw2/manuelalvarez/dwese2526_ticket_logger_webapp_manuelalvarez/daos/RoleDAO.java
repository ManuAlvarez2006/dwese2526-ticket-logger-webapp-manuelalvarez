package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Role;

import java.util.List;
import java.util.Set;

public interface RoleDAO {
    List<Role> listAllRoles();
    List<Role> findAllByIds(Set<Long> ids);


}
