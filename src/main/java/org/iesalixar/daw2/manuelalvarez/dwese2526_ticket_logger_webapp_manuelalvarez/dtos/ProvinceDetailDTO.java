package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDetailDTO {
    private Long id;
    private String code;
    private String name;
    private RegionDTO region;
}
