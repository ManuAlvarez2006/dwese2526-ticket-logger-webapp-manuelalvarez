package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;

public interface UserProfileDAO {
        UserProfile getUserProfileByUserId (Long userId);
        void saveOrUpdateUserProfile (UserProfile userProfile);
        boolean existsUserProfileByUserId (Long userId);
    }


