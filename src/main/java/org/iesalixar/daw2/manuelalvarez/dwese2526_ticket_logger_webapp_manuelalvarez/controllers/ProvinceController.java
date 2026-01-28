package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;


import jakarta.validation.Valid;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.ProvinceRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.RegionRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Province;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.ProvinceMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.RegionMapper;
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

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {


    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private RegionRepository regionRepository;


    @GetMapping
    public String listProvinces(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction. ASC) Pageable pageable, Model model) {
        logger.info("Listando provincias page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        try {
            Page<ProvinceDTO> listProvincesDTOs = provinceRepository.findAll(pageable).map(ProvinceMapper::toDTO);
            logger.info("Se han cargado {} provincias en la página {}.",
                    listProvincesDTOs.getNumberOfElements(), listProvincesDTOs.getNumber());
            model.addAttribute("page", listProvincesDTOs);
            //Para mantener el sort actual en los enlaces de la vista (sort=campo, asc|desc)
            String sortParam = "name,asc";
            if (listProvincesDTOs.getSort().isSorted()) {
                Sort.Order order = listProvincesDTOs.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar las provincias: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar las provincias.");
        }
        return "views/province/province-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva provincia");
        model.addAttribute("province", new RegionCreateDTO());
        return "views/province/province-form";
    }


    @PostMapping("/insert")
    public String insertProvince (@Valid @ModelAttribute("province") ProvinceCreateDTO provinceDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Locale locale) {
        logger.info("Insertando nueva región con código {}", provinceDTO.getCode());
        try {
            if (result.hasErrors()) {
                return "views/province/province-form"; // Devuelve el formulario para mostrar los errores de validación
            }
            if (provinceRepository.existsByCode(provinceDTO.getCode())) {
                logger.warn("El código de la región {} ya existe.", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/new";
            }
            // Mapear DTO -> Entity y persistir
            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceRepository.save(province);
            logger.info("provincia {} insertada con éxito.", province.getCode());
        } catch (Exception e) {
            logger.error("Error al insertar la región {}: {}", provinceDTO.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces"; // Redirigir a la lista de provincias
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, Locale locale) {
        logger.info("Mostrando formulario de edición para la región con ID {}", id);
        Optional<Province> provinceOpt;
        ProvinceUpdateDTO provinceDTO = null;
        try {
// Spring Data: findById devuelve Optional
            provinceOpt = provinceRepository.findById(id);

            if (provinceOpt.isEmpty()) {
                logger.warn("No se encontró la región con ID {}", id);
                String msg = messageSource.getMessage("msg.province.error.notfound", new Object[]{id}, locale);
                model.addAttribute("errorMessage", msg);
            } else {
                Province province = provinceOpt.get();
                provinceDTO = ProvinceMapper.toUpdateDTO(province);
            }

        } catch (Exception e) {
            logger.error("Error al obtener la región con ID ): {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.province.error.load", null, locale);
            model.addAttribute("errorMessage", msg);
        }
        model.addAttribute("province", provinceDTO);
        return "views/province/province-form";
    }

    @PostMapping("/update")
    public String updateProvince(
            @Valid @ModelAttribute("province") ProvinceUpdateDTO provinceDTO, BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        logger.info("Actualizando región con ID {}", provinceDTO.getId());
        try {
            if (result.hasErrors()) {
                return "views/province/province-form"; // mostrar errores de validación
            }
            if (provinceRepository.existsByCodeAndIdNot(provinceDTO.getCode(), provinceDTO.getId())) {
                logger.warn("El código de la región {} ya existe para otra región.", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/edit?id=" + provinceDTO.getId();
            }
            // Cargar entidad existente (Spring Data -> Optional)
            Optional<Province> provinceOpt = provinceRepository.findById(provinceDTO.getId());
            if (provinceOpt.isEmpty()) {
                logger.warn("No se encontró la región con ID {}", provinceDTO.getId());
                String notFound = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/provinces";
            }
            Province province = provinceOpt.get();
            ProvinceMapper.copyToexistingEntity(provinceDTO, province);
// Spring Data: save() actualiza si el id existe
            provinceRepository.save(province);
            logger.info("Región con ID {} actualizada con éxito.", province.getId());
        } catch(Exception e){
            logger.error("Error al actualizar la región con ID : {}", provinceDTO.getId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }
    /**
     * Elimina una región de la base de datos.
     * @param id
    ID de la región a eliminar.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */

    @PostMapping("/delete")
    public String deleteProvince(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        logger.info("Eliminando región con ID {}", id);
        try {
// (Recomendable) comprobar existencia antes de borrar
            Optional<Province> provinceOpt = provinceRepository.findById(id);
            if (provinceOpt.isEmpty()) {
                logger.warn("No se encontró la región con ID {}", id);
                String notFound = messageSource.getMessage("msg.Province-controller.detail.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/provinces";
            }
            provinceRepository.deleteById(id);
            logger.info("Región con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la región con ID {}: {}", id, e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces"; // Redirigir a la lista de provincias
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle de la provincia con ID {}", id);
        try {
            Province province = provinceRepository.getProvinceById(id);
            ProvinceDetailDTO provinceDTO = ProvinceMapper.toDetailDTO(province);
            if (province == null) {
                logger.warn("No se encontró la provincia con ID {}", id);
                String msg = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/provinces";
            }
            model.addAttribute("province", provinceDTO);
            return "views/provinces/province-detail";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la provincia {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.province-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";
        }
    }
}