package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;


import jakarta.validation.Valid;
import org.apache.catalina.mapper.Mapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos.ProvinceDAO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos.ProvinceDAOImpl;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos.RegionDAO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Province;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.ProvinceMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.RegionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {


    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProvinceDAO provinceDAO;

    @Autowired
    private RegionDAO regionDAO;


    @GetMapping
    public String ListProvinces (@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortField", defaultValue = "name") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model,
            Locale locale) {
        logger.info("Solicitando la lista de provincias... page={}, size={}, sortField={},sortDir={}", page, size, sortField, sortDir);
        if (page < 0) page = 0;
        if (size <=0) size = 10;
        try {
            Long totalElements = provinceDAO.countProvinces();
            int totalPages = (int) Math.ceil((double) totalElements / size);
// Ajustar si se pide una página fuera de rango
            if (totalPages > 0 && page >= totalPages) {
                page= totalPages - 1;
            }
            List<Province> entities = provinceDAO.listProvincesPage(page, size,sortField,sortDir);
            List<ProvinceDTO> dtos = ProvinceMapper.toDTOList(entities);
            logger.info("Se han cargado {} provincias en la página ().", dtos.size(), page);
            model.addAttribute("listProvinces", dtos);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortField);
            model.addAttribute("reverseSortDir", "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");

        } catch (Exception e) {
            logger.error("Error al listar las provincias: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/provinces/province-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model, Locale locale) {
        logger.info("Mostrando formulario para nueva provincia.");
        try {
            List<RegionDTO> listRegionsDTOs = RegionMapper.toDTOList(regionDAO.listAllRegions());
            model.addAttribute("province", new ProvinceCreateDTO());
            model.addAttribute("listRegions", listRegionsDTOs);
        } catch (Exception e) {
            logger.error("Error al cargar las regiones para el formulario de la provincia: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/provinces/province-form";
    }


    @PostMapping("/insert")
    public String insertProvinces(@Valid @ModelAttribute("province") ProvinceCreateDTO provinceDTO,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model,
                                  Locale locale) {
        logger.info("Insertando nueva provincia con codigo {}", provinceDTO.getCode());
        try {
            if (result.hasErrors()) {
                List<Region> listRegions = regionDAO.listAllRegions();
                List<RegionDTO> listRegionsDTOs = RegionMapper.toDTOList(listRegions);
                model.addAttribute("listRegions", listRegionsDTOs);
                return "views/provinces/province-form";
            }
            if (provinceDAO.existsProvinceByCode(provinceDTO.getCode())) {
                logger.warn("El codigo de la provincia {} ya existe", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/new";
            }
            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceDAO.insertProvince(province);
            logger.info("Provincia {} insertada con exito.", provinceDTO.getCode());
        } catch (Exception e) {
            logger.error("Error al insertar la provincia {}: {}", provinceDTO.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, Locale locale) {
        logger.info("Mostrando formulario de edición para la provincia con ID {}", id);
        try {
            Province province = provinceDAO.getProvinceById(id);
            ProvinceUpdateDTO provinceDTO = ProvinceMapper.toUpdateDTO(province);
            if (province == null) {
                logger.warn("No se encontró la provincia con ID {}", id);
                String errorMessage = messageSource.getMessage("msg.province-controller.edit.notfound", null, locale);
                model.addAttribute("errorMessage", errorMessage);
            } else {
                List<RegionDTO> listRegionsDTOs = RegionMapper.toDTOList(regionDAO.listAllRegions());
                model.addAttribute("province", provinceDTO);
                model.addAttribute("listRegions", listRegionsDTOs);
            }
        } catch (Exception e) {
            logger.error("Error al obtener la provincia con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/provinces/province-form";
    }

    @PostMapping("/update")
    public String updateProvince(@Valid @ModelAttribute("province") ProvinceUpdateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {
        logger.info("Actualizando provincia con ID {}", provinceDTO.getId());
        try {
            if (result.hasErrors()) {
                List<Region> listRegions = regionDAO.listAllRegions();
                model.addAttribute("listRegions", listRegions);
                return "views/provinces/province-form";
            }

            if (provinceDAO.existsProvinceByCodeAndNotId(provinceDTO.getCode(), provinceDTO.getId())) {

                logger.warn("El código de la provincia {} ya existe para otra provincia.", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/edit?id=" + provinceDTO.getId();
            }
            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceDAO.updateProvince(province);
            logger.info("Provincia con ID {} actualizada con éxito.", province.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar la provincia con ID {}: {}", provinceDTO.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }

    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale) {
        logger.info("Eliminado provincia con id: {}", id);
        try {
            provinceDAO.deleteProvince(id);
            logger.info("Province con ID {} eliminada con exito,", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle de la provincia con ID {}", id);
        try {
            Province province = provinceDAO.getProvinceById(id);
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