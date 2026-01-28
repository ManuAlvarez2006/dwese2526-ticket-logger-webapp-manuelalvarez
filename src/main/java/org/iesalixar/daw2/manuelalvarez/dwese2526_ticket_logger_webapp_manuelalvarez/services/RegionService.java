package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionCreateDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionDetailDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegionService {
    Page<RegionDTO> list(Pageable pageable);

    RegionUpdateDTO getForedit(Long id);

    void create(RegionCreateDTO dto);

    void update(RegionUpdateDTO dto);

    void delete(Long id);

    RegionDetailDTO getDetail(Long id);

}
