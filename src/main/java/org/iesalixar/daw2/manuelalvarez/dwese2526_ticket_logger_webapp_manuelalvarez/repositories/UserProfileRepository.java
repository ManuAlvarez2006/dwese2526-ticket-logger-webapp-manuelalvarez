package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
        UserProfile getUserProfileByUserId (Long userId);
        void saveOrUpdateUserProfile (UserProfile userProfile);
        boolean existsUserProfileByUserId (Long userId);

    Optional<UserProfile> findByUserId(Long id);
}


