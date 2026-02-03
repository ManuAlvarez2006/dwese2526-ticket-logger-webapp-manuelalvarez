package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listUsers(
            @PageableDefault(size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {
        logger.info("Listando usuarios page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        try {
            // Obtenemos la página de DTOs
            Page<UserDTO> pageUsers = userService.list(pageable);
            logger.info("Se han cargado {} usuarios en la página {}.",
                    pageUsers.getNumberOfElements(), pageUsers.getNumber());

            model.addAttribute("page", pageUsers);
            model.addAttribute("listUsers", pageUsers.getContent());

            String sortParam = "email,asc"; // <- cambiar aquí también
            if (pageUsers.getSort().isSorted()) {
                Sort.Order order = pageUsers.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);

        } catch (Exception e) {
            logger.error("Error al listar los usuarios: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar los usuarios.");
        }
        return "views/user/user-list";
    }


    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nuevo usuario");
        model.addAttribute("user", new UserCreateDTO());
        return "views/user/user-form";
    }

    @PostMapping("/insert")
    public String insertUser(@Valid @ModelAttribute("user") UserCreateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Insertando nuevo usuario con email {}", userDTO.getEmail());
        try {
            if (result.hasErrors()) {
                return "views/user/user-form";
            }

            userService.create(userDTO);
            logger.info("Usuario {} insertado con éxito.", userDTO.getEmail());
            return "redirect:/users";

        } catch (DuplicateResourceException ex) {
            logger.warn("El email {} ya existe.", userDTO.getEmail());
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.emailExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/new";

        } catch (Exception e) {
            logger.error("Error al insertar el usuario {}: {}", userDTO.getEmail(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/new";
        }
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        logger.info("Mostrando formulario de edición para el usuario con ID {}", id);
        try {
            UserUpdateDTO userDTO = userService.getForedit(id);
            model.addAttribute("user", userDTO);
            return "views/user/user-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario con ID {}", id);
            String msg = messageSource.getMessage("msg.user.error.notfound", new Object[]{id}, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error al obtener el usuario con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user.error.load", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UserUpdateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Actualizando usuario con ID {}", userDTO.getId());
        try {
            if (result.hasErrors()) {
                return "views/user/user-form";
            }

            userService.update(userDTO);
            logger.info("Usuario con ID {} actualizado con éxito.", userDTO.getId());
            return "redirect:/users";

        } catch (DuplicateResourceException ex) {
            logger.warn("El email {} ya existe para otro usuario.", userDTO.getEmail());
            String errorMessage = messageSource.getMessage("msg.user-controller.update.emailExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/edit?id=" + userDTO.getId();

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario con ID {}", userDTO.getId());
            String notFound = messageSource.getMessage("msg.user-controller.detail.notfound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error al actualizar el usuario con ID {}: {}", userDTO.getId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.user-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/edit?id=" + userDTO.getId();
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Eliminando usuario con ID {}", id);
        try {
            userService.delete(id);
            logger.info("Usuario con ID {} eliminado con éxito.", id);
            return "redirect:/users";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario con ID {}", id);
            String notFound = messageSource.getMessage("msg.user-controller.detail.notfound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error al eliminar el usuario con ID {}: {}", id, e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.user-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users";
        }
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle del usuario con ID {}", id);
        try {
            UserDetailDTO userDTO = userService.getDetail(id);
            model.addAttribute("user", userDTO);
            return "views/user/user-detail";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.user-controller.detail.notfound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle del usuario {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }
}
