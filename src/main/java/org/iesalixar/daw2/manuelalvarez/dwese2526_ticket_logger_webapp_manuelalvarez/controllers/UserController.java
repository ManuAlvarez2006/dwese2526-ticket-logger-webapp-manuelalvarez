package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.RegionMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.RoleRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.UserRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.UserMapper;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    private static final int PASSWORD_EXPIRY_DAYS = 90;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listUsers(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction. ASC) Pageable pageable, Model model) {
        logger.info("Listando usuarios page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        try {
            Page<UserDTO> listUsersDTOs = userRepository.findAll(pageable).map(UserMapper::toDTO);
            logger.info("Se han cargado {} usuarios en la página {}.",
                    listUsersDTOs.getNumberOfElements(), listUsersDTOs.getNumber());
            model.addAttribute("page", listUsersDTOs);
            //Para mantener el sort actual en los enlaces de la vista (sort=campo, asc|desc)
            String sortParam = "name,asc";
            if (listUsersDTOs.getSort().isSorted()) {
                Sort.Order order = listUsersDTOs.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar las usuarios: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar los usuarios.");
        }
        return "views/user/user-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nuevo usuario");
        model.addAttribute("user", new UserCreateDTO());

        model.addAttribute("allRoles", roleRepository.listAllRoles());
        return "views/user/user-form";
    }

    @PostMapping("/insert")
    public String insertUser (@Valid @ModelAttribute("user") UserCreateDTO userDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Locale locale) {

        logger.info("Insertando nuevo usuario con usuario {}", userDTO.getEmail());

        try {
            if (result.hasErrors()) {
                return "views/user/user-form"; // Devuelve el formulario para mostrar los errores de validación
            }

            if (userRepository.existsByUsername(userDTO.getEmail())) {
                logger.warn("El código del usuario {} ya existe.", userDTO.getEmail());
                String errorMessage = messageSource.getMessage("msg.user-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/new";
            }

            // Mapear DTO -> Entity y persistir
            User user = UserMapper.toEntity(userDTO);
            userRepository.save(user);

            logger.info("Usuario {} insertado con éxito.", user.getEmail());

        } catch (Exception e) {
            logger.error("Error al insertar el usuario {}: {}", userDTO.getEmail(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/users"; // Redirigir a la lista de usuarios
    }


    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, Locale locale) {
        logger.info("Mostrando formulario de edición para el usuario con ID {}", id);
        Optional<User> userOpt;
        UserUpdateDTO userDTO = null;

        try {
            // Spring Data: findById devuelve Optional
            userOpt = userRepository.findById(id);

            if (userOpt.isEmpty()) {
                logger.warn("No se encontró el usuario con ID {}", id);
                String msg = messageSource.getMessage("msg.user.error.notfound", new Object[]{id}, locale);
                model.addAttribute("errorMessage", msg);
            } else {
                User user = userOpt.get();
                userDTO = UserMapper.toUpdateDTO(user);
            }

        } catch (Exception e) {
            logger.error("Error al obtener el usuario con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user.error.load", null, locale);
            model.addAttribute("errorMessage", msg);
        }

        model.addAttribute("user", userDTO);
        return "views/user/user-form";
    }

    @PostMapping("/update")
    public String updateUser(
            @Valid @ModelAttribute("user") UserUpdateDTO userDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Actualizando usuario con ID {}", userDTO.getId());

        try {
            if (result.hasErrors()) {
                return "views/user/user-form"; // mostrar errores de validación
            }

            if (userRepository.existsByEmailAndNotId(userDTO.getEmail(), userDTO.getId())) {
                logger.warn("El código del usuario {} ya existe para otro usuario.", userDTO.getEmail());
                String errorMessage = messageSource.getMessage("msg.user-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/edit?id=" + userDTO.getId();
            }

            // Cargar entidad existente (Spring Data -> Optional)
            Optional<User> userOpt = userRepository.findById(userDTO.getId());
            if (userOpt.isEmpty()) {
                logger.warn("No se encontró el usuario con ID {}", userDTO.getId());
                String notFound = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/users";
            }

            User user = userOpt.get();
            UserMapper.copyToExistingEntity(userDTO, user);

            // Spring Data: save() actualiza si el id existe
            userRepository.save(user);
            logger.info("Usuario con ID {} actualizado con éxito.", user.getId());

        } catch(Exception e) {
            logger.error("Error al actualizar el usuario con ID {}: {}", userDTO.getId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.user-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/users";
    }


    @PostMapping("/delete")
    public String deleteUser(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Eliminando usuario con ID {}", id);

        try {
            // (Recomendable) comprobar existencia antes de borrar
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                logger.warn("No se encontró el usuario con ID {}", id);
                String notFound = messageSource.getMessage("msg.user-controller.detail.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/users";
            }

            userRepository.deleteById(id);
            logger.info("Usuario con ID {} eliminado con éxito.", id);

        } catch (Exception e) {
            logger.error("Error al eliminar el usuario con ID {}: {}", id, e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.user-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/users"; // Redirigir a la lista de usuarios
    }


    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        logger.info("Mostrando detalle del usuario con ID {}", id);

        try {
            Optional<User> userOpt = userRepository.findRolesById(id); // Ajusta el método según relaciones de User

            if (userOpt.isEmpty()) {
                String msg = messageSource.getMessage("msg.user-controller.detail.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/users";
            }

            User user = userOpt.get();
            // Mapear Entity -> DTO de detalle (incluye relaciones si aplica)
            UserDetailDTO userDTO = UserMapper.toDetailDTO(user);
            model.addAttribute("user", userDTO);

            return "views/user/user-detail";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle del usuario {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }

}
