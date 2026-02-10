package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.UserProfileFormDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.InvalidFileException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.UserProfileMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.UserProfileRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);
    /**
     * Limite de tamaño máximo permitido para la imagen (2MB).
     */
    private static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private FileStorageService fileStorageService;
    /**
     * Construye el DTO del formulario de perfil para un usuario identificado por email. * <p>
     * Caso de uso típico: página "Mi perfil" donde el usuario se identifica por email
     * (en tu ejemplo, un email fijo). El método:
     * <ul>
     <li>busca el {@code User} por email, </li>
     . <li>busca su {@code UserProfile} (puede no existir),</li>
     ⭑
     <li>mapea {@code User + UserProfile
    UserProfileFormDT0}.</li>
     * </ul>
     * </p>
     *
     * @param email email del usuario a cargar.
     * @return DTO con los datos del formulario (si no existe perfil, campos vacíos).
     *
     */
    @Override
    public UserProfileFormDTO getFormByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", email));
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(user.getId());

        UserProfile profile = profileOpt.orElse(null);
        return UserProfileMapper.toFormDto(user, profile);
    }



    /**
     * Actualiza o crea el perfil de usuario a partir del DTO del formulario.
     * <p>
     * Aplica la lógica:
     *
     *
     *
     <ul>
     <li>comprueba que el {@code User} existe, </li>
     <li>carga el {@code UserProfile) si existe, </li>
     *
    <li>si se adjunta imagen: valida tipo/tamaño, guarda nueva y borra anterior, </li>
     *
     * </ul>
    <li>crea o actualiza la entidad perfil y persiste con Spring Data. </li>
     * </p>
     *
     * @param profileDto
    datos del formulario (incluye (@code userId}).
     * @param profileImageFile imagen subida (puede ser {@code null} o vacía).
    si no existe el usuario asociado al {@code userId).
     * @throws org.iesalixar.daw2.nombrealumno.dwese2526_ticket_logger_webapp_nombrealumno.exceptions. InvalidFileException si la imagen no cumple validaciones (tipo/tamaño) o no se puede guardar.
     *
     */
    @Override
    public void updateProfile (String email,UserProfileFormDTO profileDto, MultipartFile profileImageFile) {
        logger.info("Actualizando perfil para email={}", email);
// 1) Comprobar que existe el User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", email));

        Long userId = user.getId();
// 2) Cargar perfil (puede no existir)
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        boolean isNew = (profile == null);

        // 3) Si hay imagen nueva, validar + guardar + borrar anterior
        if (profileImageFile != null && !profileImageFile.isEmpty()) {

            // Validaciones semánticas (lanzan InvalidFileException)
            validateProfileImage(profileImageFile);

            String oldImagePath = profileDto.getProfileImage(); // ruta actual (puede ser null/blank)

            String newImageWebPath = fileStorageService.saveFile(profileImageFile);
            if (newImageWebPath == null || newImageWebPath.isBlank()) {
                // Se Lanza la excepción (resource, field, value, detail)
                throw new InvalidFileException(
                        "userProfile",
                        "profileImageFile",
                        profileImageFile.getOriginalFilename(),
                        "No se pudo guardar la imagen de perfil."
                );
            }

            profileDto.setProfileImage(newImageWebPath);

            // Borrar anterior si existía
            if (oldImagePath != null && !oldImagePath.isBlank()) {
                fileStorageService.deleteFile(oldImagePath);
            }
        }
        if (isNew) {
            profile = UserProfileMapper.toNewEntity(profileDto, user);
        } else {
            UserProfileMapper.copyToExistingEntity(profileDto, profile);

        }
// 5) Persistir
        userProfileRepository.save(profile);
    }
    private void validateProfileImage(MultipartFile file) {
        String contentType = file.getContentType();
        // MIME inválido
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileException(
                    "userProfile",
                    "profileImageFile",
                    contentType,
                    "Tipo de archivo no permitido"
            );
        }
        // Tamaño excedido
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new InvalidFileException(
                    "userProfile",
                    "profileImageFile",
                    file.getSize(),
                    "Archivo demasiado grande (máximo " + MAX_IMAGE_SIZE_BYTES + "bytes)"
            );


        }
    }
}