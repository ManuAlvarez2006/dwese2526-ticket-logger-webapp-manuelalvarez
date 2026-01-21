package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos.UserDAO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos.UserProfileDAO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.UserProfileFormDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.UserProfileMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/edit")
    public String showProfileForm(Model model, Locale locale) {

        final String fixedEmail = "admin@app.local";
        logger.info("Mostrando formulario de perfil para el usuario fijo {}", fixedEmail);

        User user = userDAO.getUsersByEmail(fixedEmail);
        UserProfileFormDTO formDto;

        if (user != null) {
            UserProfile profile = userProfileDAO.getUserProfileByUserId(user.getId());
            formDto = UserProfileMapper.toFormDto(user, profile);
        } else {
            logger.warn("No se encontró el usuario con email {}", fixedEmail);
            String errorMessage = messageSource.getMessage(
                    "msg.user-controller.edit.notfound", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            formDto = new UserProfileFormDTO();
        }

        model.addAttribute("userProfileForm", formDto);
        return "views/user/user-profile-form";
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("userProfileForm") UserProfileFormDTO profileDto,
            BindingResult result,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Actualizando perfil para el usuario con ID {}", profileDto.getUserId());

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de perfil para userId={}",
                    profileDto.getUserId());
            return "views/user-profile/user-profile-form";
        }

        try {
            Long userId = profileDto.getUserId();
            User user = userDAO.getUserById(userId);

            if (user == null) {
                logger.warn("No se encontró el usuario con ID {}", userId);
                String errorMessage = messageSource.getMessage(
                        "msg.user-controller.edit.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/profile/edit";
            }

            UserProfile profile = userProfileDAO.getUserProfileByUserId(userId);
            boolean isNew = (profile == null);
            if (isNew) {
// Crear un nuevo perfil a partir del DTO y el User
                profile = UserProfileMapper.toNewEntity(profileDto, user);
            } else {
                UserProfileMapper.copyToExistingEntity(profileDto, profile);
            }
            userProfileDAO.saveOrUpdateUserProfile(profile);

// 4. Gestión de la imagen de perfil (si se ha subido una nueva)
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                logger.info("Se ha subido un nuevo archivo de imagen para el perfil del usuario {}", userId);
// Validación de tipo MIME
                String contentType = profileImageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    logger.warn("Archivo de tipo no permitido: {}", contentType);
                    String msg = messageSource.getMessage("msg.userProfile.image.invalidType", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", msg);
                    return "redirect:/profile/edit";
                }
// Validación de tamaño (ejemplo: 2MB máximo)
                long maxSizeBytes = 2 * 1024 * 1024; // 2 MB
                if (profileImageFile.getSize() > maxSizeBytes) {
                    logger.warn("Archivo demasiado grande: {} bytes (limite {} bytes)", profileImageFile.getSize(), maxSizeBytes);
                    String msg = messageSource.getMessage("msg.userProfile.image.tooLarge", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", msg);
                    return "redirect:/profile/edit";
                }
// Si llega aquí, el archivo pasa las validaciones lo guardamos
                String oldImagePath = profileDto.getProfileImage(); // ruta actual (puede ser null)
                String newImageWebPath=fileStorageService.saveFile(profileImageFile);
                if (newImageWebPath == null) {
                    logger.error("No se pudo guardar la nueva imagen de perfil para el usuario {}", userId); String msg = messageSource.getMessage("msg.userProfile.image.saveError", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", msg);
                    logger.info("Nueva imagen de perfil guardada en {}", newImageWebPath);
                }

// Actualizar en el DTO la ruta de la imagen
                profileDto.setProfileImage(newImageWebPath);
                if(oldImagePath != null && !oldImagePath.isBlank()){
                    logger.info("Eliminando imagen anterior de perfil: {}", oldImagePath);
                    fileStorageService.deleteFile(oldImagePath);
                }
                if(isNew){
                    profile = UserProfileMapper.toNewEntity(profileDto, user);
                } else {
                    UserProfileMapper.copyToExistingEntity(profileDto, profile);
                }
                userProfileDAO.saveOrUpdateUserProfile(profile);
// 5. Mensaje de éxito
            String successMessage = messageSource.getMessage("msg.userProfile.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        }
        } catch (Exception e) {
            logger.error("Error al actualizar el perfil del usuario con ID {}: {}", profileDto.getUserId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                    "msg.userProfile.error",
                    null,
                    locale
            );
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
// 6. Redirigir de vuelta al formulario de perfil
            return "redirect:/profile/edit";
    }
}

