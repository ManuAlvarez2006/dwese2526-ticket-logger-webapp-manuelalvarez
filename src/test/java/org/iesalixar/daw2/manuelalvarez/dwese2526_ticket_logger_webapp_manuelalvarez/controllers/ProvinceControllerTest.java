package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.controllers;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.ProvinceDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services.ProvinceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProvinceControllerTest {

    @Mock
    private ProvinceService provinceService;

    @InjectMocks
    private ProvinceController controller;

    private Pageable defaultPageable() {
        return PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
    }

    private Page<ProvinceDTO> samplePage() {
        return new PageImpl<>(List.of(new ProvinceDTO(), new ProvinceDTO()), defaultPageable(), 2);
    }

    @Test
    @DisplayName("listProvinces OK -> devuelve vista listado y mete page + sortParam")
    void listProvinces_ok() {
        Pageable pageable = defaultPageable();
        Model model = new ExtendedModelMap();

        when(provinceService.list(pageable)).thenReturn(samplePage());

        // CORREGIDO: solo pageable y model
        String view = controller.listProvinces(pageable, model);

        assertEquals("views/province/province-list", view);
        assertTrue(model.containsAttribute("page"));
        assertEquals("name,asc", model.getAttribute("sortParam"));
        verify(provinceService).list(pageable);
    }
}
