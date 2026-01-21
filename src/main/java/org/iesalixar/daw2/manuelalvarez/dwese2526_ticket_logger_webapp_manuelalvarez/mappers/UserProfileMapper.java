package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.UserProfileFormDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;

public class UserProfileMapper {
    public static UserProfileFormDTO toFormDto (User user, UserProfile profile) {
        if (user == null) {
        return null;

    }
        UserProfileFormDTO dto = new UserProfileFormDTO();
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());
        if (profile != null) {
            dto.setFirstName (profile.getFirstName());
            dto.setLastName (profile.getLastName());
            dto.setPhoneNumber (profile.getPhoneNumber());
            dto.setProfileImage(profile.getProfileImage());
            dto.setBio (profile.getBio());
            dto.setLocale (profile.getLocale());
        }
        return dto;
    }
    public static UserProfile toNewEntity (UserProfileFormDTO dto, User user) {
        if (dto == null || user == null) {
            return null;
    }
        UserProfile profile = new UserProfile();
// Relación 1:1 con shared primary key
        profile.setUser(user);
// Opcional: se puede establecer el id explícitamente profile.setId(user.getId());
        profile.setFirstName (dto.getFirstName());
        profile.setLastName (dto.getLastName());
        profile.setPhoneNumber (dto.getPhoneNumber());
        profile.setProfileImage(dto.getProfileImage());
        profile.setBio(dto.getBio());
        profile.setLocale (dto.getLocale());
        return profile;
    }
    public static void copyToExistingEntity (UserProfileFormDTO dto, UserProfile profile) {
        if (dto == null || profile == null) {
            return;
        }
        profile.setFirstName (dto.getFirstName());
        profile.setLastName (dto.getLastName());
        profile.setPhoneNumber (dto.getPhoneNumber());
        profile.setProfileImage(dto.getProfileImage());
        profile.setBio(dto.getBio());
        profile.setLocale (dto.getLocale());

    }
}


