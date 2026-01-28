package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services;

import org.apache.catalina.mapper.Mapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionCreateDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionDetailDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.RegionMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la lógica de negocio (casos de uso) para el CRUD de {@link Region}. * <p>
 * Esta capa se encarga de:
 * <ul>
 *
 *
 *
 *
 *
 <li>Interactuar con el repositorio para acceder a base de datos. </li> <li>Aplicar reglas de negocio (por ejemplo, evitar códigos duplicados).</li> <li>Transformar entidades a DTOs y viceversa mediante {@link RegionMapper).</li> <li>Lanzar excepciones semánticas reutilizables ({@link ResourceNotFoundException},
 * </ul>
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService{
    @Autowired
    private RegionRepository regionRepository;
    /**
    * Devuelve una lista paginada de regiones.
     * @param pageable parámetros de paginación y ordenación
    * @return página de {@link RegionDTO}
*/
    @Override
    public Page<RegionDTO> list(Pageable pageable) {
        return regionRepository.findAll(pageable).map(RegionMapper::toDTO);
    }

    /**
     * Obtiene los datos necesarios para cargar el formulario de edición.
     * @param id identificador de la región
     * @return DTO de edición
     * @throws ResourceNotFoundException si no existe la región con ese id
     */
    @Override
    public RegionUpdateDTO getForedit(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("region", "id", id));
        return RegionMapper.toUpdateDTO(region);
    }

    /**
     @
     * Crea una nueva región.
     *
     * @param dto datos del formulario de creación
     * @throws DuplicateResourceException si ya existe una región con el mismo código
     */
    @Override
    public void create (RegionCreateDTO dto) {
        if (regionRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("region", "code", dto.getCode());

        }
        Region region = RegionMapper.toEntity(dto);
        regionRepository.save(region);
    }
    /**
     * Actualiza una región existente.
     *
     * @param dto datos del formulario de edición
     * @throws DuplicateResourceException si el código ya lo está usando otra región distinta
     * @throws ResourceNotFoundException si no existe la región a actualizar
     */
    @Override
    public void update (RegionUpdateDTO dto) {
        if (regionRepository.existsByCodeAndIdNot(dto.getCode(), dto.getId())) {
            throw new DuplicateResourceException("region", "code", dto.getCode());
        }

        Region region = regionRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("region", "id", dto.getId()));
        RegionMapper.copyToExistingEntity(dto, region);
        regionRepository.save(region);
    }
    /**
     * Elimina una región por id.
     *
     * @param id identificador de la región
     * @throws ResourceNotFoundException si no existe la región a eliminar
     */
    @Override
    public void delete (Long id) {
        if (!regionRepository.existsById(id)) {
            throw new ResourceNotFoundException("region", "id", id);
        }
        regionRepository.deleteById(id);
    }

    /**
     * Devuelve el detalle de una región (incluyendo sus provincias asociadas).
     *
     * @param id identificador de la región
     * @return DTO de detalle
     * @throws ResourceNotFoundException si no existe la región
     */
    @Override
    public RegionDetailDTO getDetail (Long id) {
        Region region = regionRepository.findByIdWithProvinces(id)
                .orElseThrow(()-> new ResourceNotFoundException("region", "id", id));
        return RegionMapper.toDetailDTO(region);
    }
}
