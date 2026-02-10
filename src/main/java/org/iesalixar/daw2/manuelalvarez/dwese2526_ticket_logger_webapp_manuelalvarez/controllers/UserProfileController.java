package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.InvalidFileException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.UserRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.UserProfileRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.UserProfileFormDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.UserProfile;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.UserProfileMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services.FileStorageService;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services.UserProfileService;
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

import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Muestra el formulario de perfil (alta/edición) para el usuario. * <p>
     * Si no existe el usuario, se muestra la vista con mensaje de error. * Si existe, el service devuelve el DTO ya preparado para la vista.
     * </p>
     *
     * @param model  modelo para pasar datos a la vista
     * @param locale locale actual para i18n
     * @return plantilla Thymeleaf del formulario de perfil
     */
    @GetMapping("/edit")
    public String showProfileForm(Model model, Locale locale, Principal principal) {
       String email = principal.getName();
        try {
            UserProfileFormDTO formDto = userProfileService.getFormByEmail(email);
            model.addAttribute("userProfileForm", formDto);
            return "views/user/user-profile-form";

        } catch (
                ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario para cargar el perfil: {}", ex.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.notfound", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "views/user-profile/user-profile-form";
        } catch (Exception ex) {
            logger.error("Error inesperado cargando el formulario de perfil: {}", ex.getMessage(), ex);
            String errorMessage = messageSource.getMessage("msg.userProfile.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "views/user/user-profile-form";
        }
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("userProfileForm") UserProfileFormDTO profileDto, BindingResult result,
                                @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
                                 RedirectAttributes redirectAttributes,
                                Locale locale, Principal principal) {

        String email = principal.getName();
        logger.info("Actualizando perfil para email={}", email);
        // 1) Si hay errores de Bean Validation, volvemos a la vista (sin redirect)
        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de perfil para email={}", email);
            return "views/user/user-profile-form";
        }
        try {
            userProfileService.updateProfile(email, profileDto, profileImageFile);
            // 3) Mensaje de éxito
            String successMessage = messageSource.getMessage("msg.userProfile.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se pudo actualizar el perfil porque falta un recurso: {}", ex.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.notfound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } catch (InvalidFileException ex) {
            logger.warn("Imagen de perfil inválida: {}", ex.getMessage());
        // Puedes reutilizar tus keys existentes o crear una genérica.
        // Aquí pongo una genérica para no duplicar lógica de tipo/tamaño en el controller.
            String errorMessage = messageSource.getMessage("msg.userProfile.image.invalid", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } catch (Exception ex) {
            logger.error("Error inesperado actualizando el perfil: {}", ex.getMessage(), ex);
            String errorMessage = messageSource.getMessage("msg.userProfile.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
// 4) Redirect siempre al formulario (patrón PRG)
        return "redirect:/profile/edit";
    }
}

