package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.ProvinceCreateDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.ProvinceDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.ProvinceDetailDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.ProvinceUpdateDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Province;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;

import java.util.List;

public class ProvinceMapper {

    public static ProvinceDTO toDTO(Province entity) {
        if (entity == null) return null;
        ProvinceDTO dto = new ProvinceDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegionName(entity.getRegion().getName());
        return dto;
    }

    public static List<ProvinceDTO> toDTOList(List<Province> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(ProvinceMapper::toDTO).toList();
    }

    public static ProvinceDetailDTO toDetailDTO(Province entity) {
        if (entity == null) return null;
        ProvinceDetailDTO dto = new ProvinceDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegion(RegionMapper.toDTO(entity.getRegion()));
        return dto;
    }

    public static ProvinceUpdateDTO toUpdateDTO(Province entity) {
        if (entity == null) return null;
        ProvinceUpdateDTO dto = new ProvinceUpdateDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegionId(entity.getRegion() != null ? entity.getRegion().getId() : null);
        return dto;
    }

    public static Province toEntity(ProvinceCreateDTO dto) {
        if (dto == null)return null;
        Province e = new Province();
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        Region region = new Region();
        region.setId(dto.getRegionId());
        e.setRegion(region);
        return e;
    }

    public static Province toEntity(ProvinceUpdateDTO dto) {
        if (dto == null) return null;
        Province e = new Province();
        e.setId(dto.getId());
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        Region region = new Region();
        region.setId(dto.getRegionId());
        e.setRegion(region);
        return e;
    }
    public static void copyToexistingEntity(ProvinceUpdateDTO dto, Province entity) {
        if (dto == null || entity == null) return;
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        Region region = new Region();
        region.setId(dto.getRegionId());
        entity.setRegion(region);
    }
}
