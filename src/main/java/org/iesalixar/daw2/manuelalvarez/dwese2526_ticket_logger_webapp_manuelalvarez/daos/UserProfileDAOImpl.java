package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserProfileDAOImpl implements UserProfileDAO {

    @PersistenceContext
    private EntityManager entityManager;
    /**
     * Obtiene el perfil asociado a un usuario por su id.
     *
     * @param userId id del usuario (coincide con user_profiles.user_id).
     * @return el perfil del usuario, o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfile getUserProfileByUserId (Long userId) {
        if (userId == null) {
            return null;

        }
        return entityManager.find(UserProfile.class, userId);
    }

    @Override
    public void saveOrUpdateUserProfile(UserProfile userProfile) {
        if (userProfile == null) {
            return;
        }
        if (userProfile.getId() == null || !existsUserProfileByUserId (userProfile.getId())) {
            entityManager.persist (userProfile);
        } else {
            entityManager.merge(userProfile);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserProfileByUserId(Long userId) {
        if (userId == null) {
            return false;
        }
        String jpql = "SELECT COUNT(up) FROM UserProfile up WHERE up.id = :userId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
                query.setParameter("userId", userId);
        Long count = query.getSingleResult();
        return count != null && count > 0;
    }


}
