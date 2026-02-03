package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.dtos.*;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.User;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.mappers.UserMapper;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la lógica de negocio (casos de uso) para el CRUD de {@link User}. * <p>
 * Esta capa se encarga de:
 * <ul>
 * <li>Interactuar con el repositorio para acceder a base de datos. </li>
 * <li>Aplicar reglas de negocio (por ejemplo, evitar códigos duplicados).</li>
 * <li>Transformar entidades a DTOs y viceversa mediante {@link UserMapper).</li>
 * <li>Lanzar excepciones semánticas reutilizables ({@link ResourceNotFoundException})</li>
 * </ul>
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Devuelve una lista paginada de usuarios.
     * @param pageable parámetros de paginación y ordenación
     * @return página de {@link UserDTO}
     */
    @Override
    public Page<UserDTO> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toDTO);
    }

    /**
     * Obtiene los datos necesarios para cargar el formulario de edición.
     * @param id identificador del usuario
     * @return DTO de edición
     * @throws ResourceNotFoundException si no existe el usuario con ese id
     */
    @Override
    public UserUpdateDTO getForedit(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));
        return UserMapper.toUpdateDTO(user);
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param dto datos del formulario de creación
     * @throws DuplicateResourceException si ya existe un usuario con el mismo código
     */
    @Override
    public void create(UserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("user", "email", dto.getEmail());

        }
        User user = UserMapper.toEntity(dto);
        userRepository.save(user);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param dto datos del formulario de edición
     * @throws DuplicateResourceException si el código ya lo está usando otro usuario distinto
     * @throws ResourceNotFoundException si no existe el usuario a actualizar
     */
    @Override
    public void update(UserUpdateDTO dto) {
        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), dto.getId())) {
            throw new DuplicateResourceException("user", "email", dto.getEmail());
        }

        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", dto.getId()));
        UserMapper.copyToExistingEntity(dto, user);
        userRepository.save(user);
    }



    /**
     * Elimina un usuario por id.
     *
     * @param id identificador del usuario
     * @throws ResourceNotFoundException si no existe el usuario a eliminar
     */
    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("user", "id", id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Devuelve el detalle de un usuario (incluyendo sus provincias asociadas).
     *
     * @param id identificador del usuario
     * @return DTO de detalle
     * @throws ResourceNotFoundException si no existe el usuario
     */
    @Override
    public UserDetailDTO getDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));
        return UserMapper.toDetailDTO(user);
    }
}
