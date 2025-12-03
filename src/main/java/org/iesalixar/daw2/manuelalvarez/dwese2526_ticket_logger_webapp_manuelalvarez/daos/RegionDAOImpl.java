package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
@Transactional
public class RegionDAOImpl implements RegionDAO{

    private static final Logger logger = (Logger) LoggerFactory.getLogger(RegionDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Region> listAllRegions(){
        logger.info("Listing all regions from the database");
        String hql = "SELECT r FROM Region r";
        List<Region> regions = entityManager.createQuery(hql, Region.class).getResultList();
        logger.info("Retrieved {} regions from the database", regions.size());
        return regions;
    }
@Override
    public void insertRegion(Region region) {
        logger.info("Inserting region with code: {} and name: {}", region.getCode(), region.getName());
        entityManager.persist(region);
        logger.info("Inserted region succesful");
    }

    @Override
    public boolean existsRegionByCode(String code) {
        String hql = "SELECT COUNT(r) FROM Region r WHERE UPPER(r.code) = :code";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("code", code.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Region with code: {} exists: {}", code, exists);
        return exists;
    }

    @Override
    public List<Region> listRegionsPage(int page, int size, String sortField, String sortDir) {
        logger.info("Listing regions page={}, size={},sortField={}, sortDir={} from the database.", page, size,sortField, sortDir);
        int offset = page * size;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Region.class);
        Root<Region> root = cq.from(Region.class);
// 2. Determinar el campo de ordenación permitido (whitelist)
        Path<?> sortPath;
        switch (sortField) {
            case "id" -> sortPath = root.get("id");
            case "code" -> sortPath = root.get("code");
            case "name" -> sortPath = root.get("name");
            default -> {
                logger.warn("Unknown sortField '{}', defaulting to 'name'.", sortField);
                sortPath = root.get("name");
            }
        }
// 3. Dirección de ordenación
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        Order order = descending ? cb.desc(sortPath) : cb.asc(sortPath);
// 4. Aplicar ordenación a la query
        cq.select(root).orderBy(order);
// 5. Crear TypedQuery, aplicar paginación y ejecutar
        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countRegions() {
        String hql = "SELECT COUNT(r) FROM Region r";
        Long total = entityManager.createQuery(hql, Long.class).getSingleResult();
        return (total != null) ? total : 0L;
    }

    @Override
    public void updateRegion(Region region) {
        logger.info("Updating region with id: {}", region.getId());
        entityManager.merge(region);
        logger.info("Updated region with id: {}", region.getId());
    }
    @Override
    public void deleteRegion(Long id) {
        logger.info("Deleting region with id: {}", id);
        Region region = entityManager.find(Region.class, id);
        if(region != null){
            entityManager.remove(region);
            logger.info("Deleted region with id: {}", id);
        } else{
            logger.warn("Region with id: {} not found", id);
        }
        }

    @Override
    public Region getRegionById(Long id) {
        logger.info("Retrieving region by id: {}", id);
        Region region = entityManager.find(Region.class, id);
        if (region != null) {
            logger.info("Region retrieved: {} - {}", region.getCode(), region.getName());
        } else {
            logger.warn("No region found with id: {}", id);
        }
        return region;

    }

    @Override
    public boolean existsRegionByCodeAndNotId(String code, Long id) {
        logger.info("Checking if region with code: {} exists excluding id: {}", code, id);
        String hql = "SELECT COUNT(r) FROM Region r WHERE UPPER(r.code) = :code AND r.id <> :id";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("code", code.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Region with code {} exists: {}", code, exists);
        return exists;
}
}

