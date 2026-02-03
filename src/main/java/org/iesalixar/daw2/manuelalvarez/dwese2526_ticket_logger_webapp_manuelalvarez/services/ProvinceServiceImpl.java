package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Province;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.ProvinceMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Override
    public Page<ProvinceDTO> list(Pageable pageable) {
        return provinceRepository.findAll(pageable).map(ProvinceMapper::toDTO);
    }

    @Override
    public ProvinceUpdateDTO getForedit(Long id) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("province", "id", id));
        return ProvinceMapper.toUpdateDTO(province);
    }

    @Override
    public void create(ProvinceCreateDTO dto) {
        if (provinceRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("province", "code", dto.getCode());
        }
        Province province = ProvinceMapper.toEntity(dto);
        provinceRepository.save(province);
    }

    @Override
    public void update(ProvinceUpdateDTO dto) {
        if (provinceRepository.existsByCodeAndIdNot(dto.getCode(), dto.getId())) {
            throw new DuplicateResourceException("province", "code", dto.getCode());
        }

        Province province = provinceRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("province", "id", dto.getId()));
        ProvinceMapper.copyToExistingEntity(dto, province);
        provinceRepository.save(province);
    }

    @Override
    public void delete(Long id) {
        if (!provinceRepository.existsById(id)) {
            throw new ResourceNotFoundException("province", "id", id);
        }
        provinceRepository.deleteById(id);
    }

    @Override
    public ProvinceDetailDTO getDetail(Long id) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("province", "id", id));
        return ProvinceMapper.toDetailDTO(province);
    }
}
