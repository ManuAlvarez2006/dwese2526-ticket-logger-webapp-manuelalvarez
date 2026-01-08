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
        if (user == null) {
            logger.warn("No se encontró el usuario con email {}", fixedEmail);
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.notfound", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "views/user-profile/user-profile-form";
        }
        UserProfile profile = userProfileDAO.getUserProfileByUserId(user.getId());
        UserProfileFormDTO formDto = UserProfileMapper.toFormDto(user, profile);
        model.addAttribute("userProfileForm", formDto);

        return "views/user-profile/user-profile-form";
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
            logger.warn("Errores de validación en el formulario para userId={}", profileDto.getUserId());
            return "views/user-profile/user-profile-form";
        }

        try {
            Long userId = profileDto.getUserId();

            User user = userDAO.getUsersByEmail(profileDto.getEmail());
            if (user == null) {
                logger.warn("Usuario no encontrado con email {}", profileDto.getEmail());
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.user-controller.edit.notfound", null, locale));
                return "redirect:/profile/edit";
            }

            UserProfile profile = userProfileDAO.getUserProfileByUserId(userId);
            boolean isNew = (profile == null);

            if (isNew) {
                profile = UserProfileMapper.toNewEntity(profileDto, user);
            } else {
                UserProfileMapper.copyToExistingEntity(profileDto, profile);
            }

            if (profileImageFile != null && !profileImageFile.isEmpty()) {

                logger.info("Subiendo nueva imagen para el usuario {}", userId);

                // Validar tipo
                String contentType = profileImageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            messageSource.getMessage("msg.userProfile.image.invalidType", null, locale));
                    return "redirect:/profile/edit";
                }

                // Validar tamaño (2MB)
                long maxSizeBytes = 2 * 1024 * 1024;
                if (profileImageFile.getSize() > maxSizeBytes) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            messageSource.getMessage("msg.userProfile.image.tooLarge", null, locale));
                    return "redirect:/profile/edit";
                }

                // Guardar archivo
                String newImageWebPath = fileStorageService.saveFile(profileImageFile);
                if (newImageWebPath == null) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            messageSource.getMessage("msg.userProfile.image.saveError", null, locale));
                    return "redirect:/profile/edit";
                }

                // Borrar imagen anterior si existe
                if (profile.getProfileImage() != null && !profile.getProfileImage().isBlank()) {
                    fileStorageService.deleteFile(profile.getProfileImage());
                }

                // Asignar nueva imagen
                profile.setProfileImage(newImageWebPath);
            }

            // Guardar perfil con o sin imagen
            userProfileDAO.saveOrUpdateUserProfile(profile);

            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.userProfile.success", null, locale));

        } catch (Exception e) {
            logger.error("Error al actualizar el perfil del usuario {}: {}", profileDto.getUserId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.userProfile.error", null, locale));
        }

        return "redirect:/profile/edit";
    }


}