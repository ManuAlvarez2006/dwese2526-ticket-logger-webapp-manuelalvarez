package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Role;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(User entity) {
        if (entity == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setPasswordHash(entity.getPasswordHash());
        dto.setAccountNonLocked(entity.getAccountNonLocked());
        dto.setActive(entity.getActive());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setMustChangePassword(entity.getMustChangePassword());

        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            Set<String> roleNames = entity.getRoles().stream()
                    .map(Role::getName) // o Role::getDisplayName si prefieres
                    .collect(Collectors.toSet());
            dto.setRoles (roleNames);
        } else {
            dto.setRoles (new HashSet<>());
        }

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
        dto.setEmail(entity.getEmail());
        dto.setPasswordHash(entity.getPasswordHash());
        dto.setAccountNonLocked(entity.getAccountNonLocked());
        dto.setActive(entity.getActive());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setMustChangePassword(entity.getMustChangePassword());
        UserProfile profile = entity.getProfile();
        if (profile != null) {
            dto.setFirstName (profile.getFirstName()); dto.setLastName (profile.getLastName()); dto.setPhoneNumber (profile.getPhoneNumber());
            dto.setProfileImage(profile.getProfileImage()); dto.setBio (profile.getBio());
            dto.setLocale (profile.getLocale());
        }
        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            Set<String> roleNames = entity.getRoles().stream()
                    .map(Role::getName)// o Role::getDisplayName si prefieres el nombre legible
                    .collect(Collectors.toSet());
            dto.setRoles (roleNames);
        } else {
            dto.setRoles (new HashSet<>()); // para evitar nulls en la vista
        }
        return dto;
    }


    public static UserUpdateDTO toUpdateDTO(User entity) {
        if (entity == null) return null;
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setPasswordHash(entity.getPasswordHash());
        dto.setAccountNonLocked(entity.getAccountNonLocked());
        dto.setActive(entity.getActive());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setMustChangePassword(entity.getMustChangePassword());

        if (entity.getRoles() != null) {
            Set<Long> roleIds = entity.getRoles().stream()
            .map(Role::getId)
            .collect(Collectors.toSet());
            dto.setRoleIds (roleIds);
        }
        return dto;


    }

    public static User toEntity(UserCreateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setEmail(dto.getEmail());
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
        user.setEmail(dto.getEmail());
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
        entity.setEmail(dto.getEmail());
        entity.setPasswordHash(dto.getPasswordHash());
        entity.setAccountNonLocked(dto.getAccountNonLocked());
        entity.setActive(dto.getActive());
        entity.setLastPasswordChange(dto.getLastPasswordChange());
        entity.setPasswordExpiresAt(dto.getPasswordExpiresAt());
        entity.setFailedLoginAttempts(dto.getFailedLoginAttempts());
        entity.setEmailVerified(dto.getEmailVerified());
        entity.setMustChangePassword(dto.getMustChangePassword());
    }
    /**

     Crea una nueva entidad {@link User} desde un {@link UserCreateDTO}

     y un conjunto de {@link Role} ya resueltos.

     Este método es útil cuando, desde el controlador/servicio,

     ya has convertido los roleIds del DTO en entidades Role usando un DAO.
     */
    public static User toEntity(UserCreateDTO dto, Set<Role> roles) {
        if (dto == null) return null;

        User e = toEntity(dto); // reutilizamos la lógica existente
        e.setRoles (roles);
        return e;
    }

    /**

     Crea una nueva entidad {@link User} desde un {@link UserUpdateDTO}

     y un conjunto de {@link Role} ya resueltos.

     Útil si trabajas con update por reemplazo completo.
     */
    public static User toEntity (UserUpdateDTO dto, Set<Role> roles) {
        if (dto == null) return null;

        User e = toEntity(dto); // reutilizamos la lógica existente
        e.setRoles (roles);
        return e;
    }

}
