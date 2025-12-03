package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserMapper {

    public static UserDTO toDTO(User entity) {
        if (entity == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPasswordHash(entity.getPasswordHash());
        dto.setAccountNonLocked(entity.getAccountNonLocked());
        dto.setActive(entity.getActive());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setMustChangePassword(entity.getMustChangePassword());


        return dto;
    }


    public static List<UserDTO> toDTOList(List<User> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(UserMapper::toDTO).toList();
    }

    public static UserDetailDTO toDetailDTO(User entity) {
        if (entity == null) return null;
        UserDetailDTO dto = new UserDetailDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPasswordHash(entity.getPasswordHash());
        dto.setAccountNonLocked(entity.getAccountNonLocked());
        dto.setActive(entity.getActive());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setMustChangePassword(entity.getMustChangePassword());
        return dto;
    }
    public static UserUpdateDTO toUpdateDTO(User entity) {
        if (entity == null) return null;
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPasswordHash(entity.getPasswordHash());
        dto.setAccountNonLocked(entity.getAccountNonLocked());
        dto.setActive(entity.getActive());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setMustChangePassword(entity.getMustChangePassword());
        return dto;
    }

    public static User toEntity(UserCreateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(dto.getPasswordHash());

        user.setActive(dto.getActive() != null ? dto.getActive() : true);
        user.setAccountNonLocked(dto.getAccountNonLocked() != null ? dto.getAccountNonLocked() : true);
        user.setLastPasswordChange(dto.getLastPasswordChange() != null ? dto.getLastPasswordChange() : LocalDateTime.now());
        user.setPasswordExpiresAt(dto.getPasswordExpiresAt() != null ? dto.getPasswordExpiresAt() : LocalDateTime.now().plusDays(90));
        user.setFailedLoginAttempts(dto.getFailedLoginAttempts() != null ? dto.getFailedLoginAttempts() : 0);
        user.setEmailVerified(dto.getEmailVerified() != null ? dto.getEmailVerified() : false);
        user.setMustChangePassword(dto.getMustChangePassword() != null ? dto.getMustChangePassword() : true);

        return user;
    }

    public static User toEntity(UserUpdateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPasswordHash(dto.getPasswordHash());
        user.setActive(dto.getActive() != null ? dto.getActive() : true);
        user.setAccountNonLocked(dto.getAccountNonLocked() != null ? dto.getAccountNonLocked() : true);
        user.setLastPasswordChange(dto.getLastPasswordChange() != null ? dto.getLastPasswordChange() : LocalDateTime.now());
        user.setPasswordExpiresAt(dto.getPasswordExpiresAt() != null ? dto.getPasswordExpiresAt() : LocalDateTime.now().plusDays(90));
        user.setFailedLoginAttempts(dto.getFailedLoginAttempts() != null ? dto.getFailedLoginAttempts() : 0);
        user.setEmailVerified(dto.getEmailVerified() != null ? dto.getEmailVerified() : false);
        user.setMustChangePassword(dto.getMustChangePassword() != null ? dto.getMustChangePassword() : true);

        return user;
    }
    public static void copyToExistingEntity(UserUpdateDTO dto, User entity) {
        if (dto == null || entity == null) return;
        entity.setUsername(dto.getUsername());
        entity.setPasswordHash(dto.getPasswordHash());
        entity.setAccountNonLocked(dto.getAccountNonLocked());
        entity.setActive(dto.getActive());
        entity.setLastPasswordChange(dto.getLastPasswordChange());
        entity.setPasswordExpiresAt(dto.getPasswordExpiresAt());
        entity.setFailedLoginAttempts(dto.getFailedLoginAttempts());
        entity.setEmailVerified(dto.getEmailVerified());
        entity.setMustChangePassword(dto.getMustChangePassword());
    }

}
