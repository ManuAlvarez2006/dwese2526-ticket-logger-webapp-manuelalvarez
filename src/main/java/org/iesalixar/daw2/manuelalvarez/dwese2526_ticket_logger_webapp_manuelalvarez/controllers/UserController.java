package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos.UserDAO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue="10") int size,
            @RequestParam(name = "sortField", defaultValue = "name") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        logger.info("Solicitando la lista de usuarios... page={}, size={}, sortField={},sortDir={}", page, size,sortField,sortDir);
        if (page < 0) page = 0;
        if (size <= 0) size= 10;
        try {
            Long totalElements = userDAO.countUsers();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            if (totalPages > 0 && page >= totalPages) {
                page = totalPages - 1;
            }
            List<User> listUsers = userDAO.listUsersPage(page, size, sortField, sortDir);
            List<UserDTO> listUsersDTOs = UserMapper.toDTOList(listUsers);
            logger.info("Se han cargado {} usuarios en la página {}.", listUsersDTOs.size(), page);
            model.addAttribute("listUsers", listUsersDTOs);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir","asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");
        } catch (Exception e) {
            logger.error("Error al listar las usuarios: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar las usuarios.");
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
    public String insertUser(
            @Valid @ModelAttribute("user") UserCreateDTO userDTO,
            BindingResult result,
            Model model,  // 🔹 añadimos Model para pasar el DTO si hay errores
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Insertando nuevo usuario con email {}", userDTO.getEmail());

        try {
            // 1️⃣ Validación
            if (result.hasErrors()) {
                model.addAttribute("user", userDTO); // 🔹 crucial para Thymeleaf
                return "views/user/user-form";
            }

            // 2️⃣ Comprobar si el usuario ya existe
            if (userDAO.existsUserByCode(userDTO.getEmail())) {
                logger.warn("El email {} ya existe.", userDTO.getEmail());
                String errorMessage = messageSource.getMessage(
                        "msg.user-controller.insert.codeExist",
                        null,
                        locale
                );
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/new"; // 🔹 corregido
            }

            // 3️⃣ Convertir DTO a entidad e insertar
            User user = UserMapper.toEntity(userDTO);
            userDAO.insertUser(user);
            logger.info("Usuario {} insertado con éxito.", userDTO.getEmail());

        } catch (Exception e) {
            logger.error("Error al insertar el usuario {}: {}", userDTO.getEmail(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                    "msg.user-controller.insert.error",
                    null,
                    locale
            );
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/new";
        }

        // 4️⃣ Redirigir a listado
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edicion para el user con ID {}", id);
        User user = null;
        UserUpdateDTO userDTO = null;
        try {
            user = userDAO.getUserById(id);
            if (user == null) {
                logger.warn("No se encontró el user con ID {}", id);
            }
            userDTO = UserMapper.toUpdateDTO(user);
        } catch (Exception e) {
            logger.error("Error al obtener el user con ID: {}", id, e.getMessage());
            model.addAttribute("errorMessage", " error al obtener la user");
        }
        model.addAttribute("user", userDTO);
        return "views/user/user-form";
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UserUpdateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        if (result.hasErrors()) {
            return "views/user/user-form";
        }

        User existingUser = userDAO.getUserById(userDTO.getId());
        if (existingUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/users";
        }

        if (userDAO.existsUserByCodeAndNotId(userDTO.getEmail(), userDTO.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username ya existe");
            return "redirect:/users/edit?id=" + userDTO.getId();
        }

        UserMapper.copyToExistingEntity(userDTO, existingUser);
        userDAO.updateUser(existingUser);

        return "redirect:/users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando usuario con ID {}", id);
        try {
            userDAO.deleteUser(id);
            logger.info("User con ID {} eliminada con exito", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la user con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la user");
        }
        return "redirect:/users";
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle de la región con ID {}", id);
        try {
            User user = userDAO.getUserById(id);
            if (user == null) {
                String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/users";
            }
            UserDetailDTO userDTO = UserMapper.toDetailDTO(user);
            model.addAttribute("user", userDTO);
            return "views/user/user-detail";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la región (: ", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }
}
