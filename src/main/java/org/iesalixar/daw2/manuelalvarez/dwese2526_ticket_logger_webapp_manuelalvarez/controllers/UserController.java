package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos.UserDAO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private UserDAO userDAO;

    @GetMapping
    public String listUsers(Model model){
        logger.info("Solicitando lista de todos las usuarios");
        List<User> listUsers = null;
        try {
            listUsers = userDAO.listAllUsers();
            logger.info("Se han cargado {} usuarios",listUsers.size());

        } catch (Exception e){
            logger.error("Error al listar las usuarios.");
            model.addAttribute("errorMessage", "Error al listar los mensajes");
        }
        model.addAttribute("listUsers", listUsers);
        return "views/user/user-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model){
        logger.info("Mostrando formulario para nuevo usuario");
        model.addAttribute("user", new User());
        return "views/user/user-form";
    }


    @PostMapping("/insert")
    public String insertUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nueva región con código {}", user.getUsername());
        try {
            if (result.hasErrors()) {
                return "views/user/user-form";
            }
            if (userDAO.existsUserByCode(user.getUsername())) {
                logger.warn("El código de la región {} ya existe.", user.getUsername());
                String errorMessage = messageSource.getMessage("msg.user-controller.insert.usernameExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/new";
            }
            userDAO.insertUser(user);
            logger.info("Región {} insertada con éxito.", user.getUsername());
        } catch (Exception e) {
            logger.error("Error al insertar la región {}: {}", user.getUsername(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/users"; // Redirigir a la lista de usuarios
    }



    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model){
        logger.info("Mostrando formulario de edicion para el usuario con ID {}", id);
        User user = null;
        try {
            user = userDAO.getUsersById(id);
            if(user == null){
                logger.warn("No se encontró el usuario con ID {}", id);
            }
        } catch (Exception e){
            logger.error("Error al obtener el usuario con ID: {}", id, e.getMessage());
            model.addAttribute("errorMessage"," error al obtener el usuario");
        }
        model.addAttribute("user", user);
        return "views/user/user-form";
    }
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {


        logger.info("Actualizando usuario con ID {}", user.getId());
        try {
            if (result.hasErrors()) {
                return "views/user/user-form";
            }
            if (userDAO.existsUserByCodeAndNotId(user.getUsername(), user.getId())) {
                logger.warn("El nombre del usuario {} ya existe para otro usuario.", user.getUsername());
                String errorMessage = messageSource.getMessage("msg.user-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/edit?id=" + user.getId();
            }
            userDAO.updateUser(user);
            logger.info("Usuario con ID {} actualizada con éxito.", user.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar el usuario con ID {}: {}", user.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/users"; // Redirigir a la lista de usuarios
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes){


        logger.info("Eliminando usuario con ID {}", id);
        try {
            userDAO.deleteUser(id);
            logger.info("User con ID {} eliminada con exito", id);
        } catch (Exception e){
            logger.error("Error al eliminar la region con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la region");

        }
        return "redirect:/users";
    }
    @Autowired
    private MessageSource messageSource;
}

