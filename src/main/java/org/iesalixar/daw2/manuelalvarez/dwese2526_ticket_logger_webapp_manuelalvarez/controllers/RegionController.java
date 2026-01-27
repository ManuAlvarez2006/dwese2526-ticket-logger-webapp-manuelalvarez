package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import jakarta.validation.Valid;
import org.apache.catalina.mapper.Mapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.RegionRepository;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionCreateDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionDetailDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.RegionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
@RequestMapping("/regions")
public class RegionController {

    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Lista paginada de regiones usando el Pageable estándar de Spring Data.
     * La vista trabajará directamente con un objeto Page (contenido + metadatos).
     *
     * @param pageable paginación/ordenación (page, size, sort) resuelta automáticamente desde la URL * @param model
    modelo para la vista
     * @return plantilla Thyme leaf del listado
     */
    @GetMapping
    public String listRegions(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction. ASC) Pageable pageable, Model model) {
        logger.info("Listando regiones page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        try {
            Page<RegionDTO> listRegionsDTOs = regionRepository.findAll(pageable).map(RegionMapper::toDTO);
            logger.info("Se han cargado {} regiones en la página {}.",
                    listRegionsDTOs.getNumberOfElements(), listRegionsDTOs.getNumber());
            model.addAttribute("page", listRegionsDTOs);
            //Para mantener el sort actual en los enlaces de la vista (sort=campo, asc|desc)
            String sortParam = "name,asc";
            if (listRegionsDTOs.getSort().isSorted()) {
                Sort.Order order = listRegionsDTOs.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar las regiones.");
        }
        return "views/region/region-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva region");
        model.addAttribute("region", new RegionCreateDTO());
        return "views/region/region-form";
    }


    @PostMapping("/insert")
    public String insertRegion (@Valid @ModelAttribute("region") RegionCreateDTO regionDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Locale locale) {
        logger.info("Insertando nueva región con código {}", regionDTO.getCode());
        try {
            if (result.hasErrors()) {
                return "views/region/region-form"; // Devuelve el formulario para mostrar los errores de validación
            }
            if (regionRepository.existsByCode(regionDTO.getCode())) {
                logger.warn("El código de la región {} ya existe.", regionDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/regions/new";
            }
            // Mapear DTO -> Entity y persistir
            Region region = RegionMapper.toEntity(regionDTO);
            regionRepository.save(region);
            logger.info("Región {} insertada con éxito.", region.getCode());
        } catch (Exception e) {
            logger.error("Error al insertar la región {}: {}", regionDTO.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, Locale locale) {
        logger.info("Mostrando formulario de edición para la región con ID {}", id);
        Optional<Region> regionOpt;
        RegionUpdateDTO regionDTO = null;
        try {
// Spring Data: findById devuelve Optional
            regionOpt = regionRepository.findById(id);

            if (regionOpt.isEmpty()) {
                logger.warn("No se encontró la región con ID {}", id);
                String msg = messageSource.getMessage("msg.region.error.notfound", new Object[]{id}, locale);
                model.addAttribute("errorMessage", msg);
            } else {
                Region region = regionOpt.get();
                regionDTO = RegionMapper.toUpdateDTO(region);
            }

        } catch (Exception e) {
            logger.error("Error al obtener la región con ID ): {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.region.error.load", null, locale);
            model.addAttribute("errorMessage", msg);
        }
        model.addAttribute("region", regionDTO);
        return "views/region/region-form";
    }

    @PostMapping("/update")
    public String updateRegion(
            @Valid @ModelAttribute("region") RegionUpdateDTO regionDTO, BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        logger.info("Actualizando región con ID {}", regionDTO.getId());
        try {
            if (result.hasErrors()) {
                return "views/region/region-form"; // mostrar errores de validación
            }
                if (regionRepository.existsByCodeAndNotId(regionDTO.getCode(), regionDTO.getId())) {
                    logger.warn("El código de la región {} ya existe para otra región.", regionDTO.getCode());
                    String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                    return "redirect:/regions/edit?id=" + regionDTO.getId();
                }
                    // Cargar entidad existente (Spring Data -> Optional)
                    Optional<Region> regionOpt = regionRepository.findById(regionDTO.getId());
                    if (regionOpt.isEmpty()) {
                        logger.warn("No se encontró la región con ID {}", regionDTO.getId());
                        String notFound = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
                        redirectAttributes.addFlashAttribute("errorMessage", notFound);
                        return "redirect:/regions";
                    }
                    Region region = regionOpt.get();
                    RegionMapper.copyToExistingEntity(regionDTO, region);
// Spring Data: save() actualiza si el id existe
                    regionRepository.save(region);
                    logger.info("Región con ID {} actualizada con éxito.", region.getId());
                } catch(Exception e){
                    logger.error("Error al actualizar la región con ID : {}", regionDTO.getId(), e.getMessage(), e);
                    String errorMessage = messageSource.getMessage("msg.region-controller.update.error", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                }
                return "redirect:/regions";
    }
    /**
     * Elimina una región de la base de datos.
     * @param id
    ID de la región a eliminar.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PostMapping("/delete")
    public String deleteRegion(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        logger.info("Eliminando región con ID {}", id);
        try {
// (Recomendable) comprobar existencia antes de borrar
            Optional<Region> regionOpt = regionRepository.findById(id);
            if (regionOpt.isEmpty()) {
                logger.warn("No se encontró la región con ID {}", id);
                String notFound = messageSource.getMessage("msg.region-controller.detail.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/regions";
            }
            regionRepository.deleteById(id);
            logger.info("Región con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la región con ID {}: {}", id, e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.region-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }
    @GetMapping("/detail")
    public String showDetail (@RequestParam("id") Long id,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              Locale locale) {
        logger.info("Mostrando detalle de la región con ID {}", id);
        try {
            Optional<Region> regionOpt = regionRepository.findByIdWithProvinces(id);

            if (regionOpt.isEmpty()) {
                String msg = messageSource.getMessage("msg.region-controller.detail.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/regions";
            }

            Region region = regionOpt.get();
            // Mapear Entity> DTO de detalle (incluye provincias)
            RegionDetailDTO regionDTO = RegionMapper.toDetailDTO (region);
            model.addAttribute("region", regionDTO);
            return "views/region/region-detail";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la región {}: {}", id, e.getMessage(), e); String msg = messageSource.getMessage("msg.region-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }
}
