package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.repositories;



import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

        /**
         * Comprueba si existe una región con el código indicado.
         * Equivalente a: existsRegionByCode(code).
         *
         * @param code código de la región
         * @return true si existe; false si no
         */
        boolean existsByCode(String code);

        /**
         * Comprueba si existe una región con el código indicado excluyendo un id.
         * Equivalente a: existsRegionByCodeAndNotId(code, id).
         *
         * @param id   id que se excluye (normalmente el que estás editando)
         * @return true si existe otra región con ese código; false si n* */
          boolean existsByCodeAndNotId(String code, Long id);
          /**
         * Busca por id.
         * Equivalente a: getRegionById(id), pero usando Optional para evitar null.
         * @return Optional con la región si existe
         */
    /**
     * Recupera una {@link Region} por su id cargando también sus {@code provinces) en la misma consulta.
     * Se usa un <i>fetch join</i> para evitar problemas de carga perezosa (por ejemplo,
     * {@code LazyInitializationException}) cuando la vista o el mapeo a DTO necesita acceder
     * a la colección de provincias fuera del contexto de persistencia.
     * </p>
     *
     * @param id identificador de la región
     * @return {@link Optional) con la región (incluyendo provincias) si existe; {@link Optional#empty()} si no existe
     */
    @Query("select r from Region r left join fetch r.provinces where г.id = :id")
    Optional<Region> findByIdWithProvinces(@Param("id") Long id);
        @Override
        Optional<Region> findById(Long id);
    }

