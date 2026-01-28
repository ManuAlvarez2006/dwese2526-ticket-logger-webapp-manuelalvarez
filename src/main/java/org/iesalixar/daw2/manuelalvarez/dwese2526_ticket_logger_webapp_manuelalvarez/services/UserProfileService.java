package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.UserProfileFormDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {

    UserProfileFormDTO getFormByEmail(String email);

    void updateProfile(UserProfileFormDTO profileDto, MultipartFile profileImageFile);
}
