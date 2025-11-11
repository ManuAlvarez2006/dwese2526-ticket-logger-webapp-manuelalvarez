package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;


import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public class RegionDAOImpl implements RegionDAO{

    private static final Logger logger = (Logger) LoggerFactory.getLogger(RegionDAOImpl.class);


    private final JdbcTemplate jdbcTemplate;


    public RegionDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Region> listAllRegions(){
        logger.info("Listing all regions from the database");
        String query = "SELECT * FROM regions";
        List<Region> regions = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Region.class));
        logger.info("Retrieved {} regions from the database", regions.size());
        return regions;
    }
@Override
    public void insertRegion(Region region) {
        String query = "INSERT INTO regions (code,name) VALUES (?,?)";
        int rowsAffected = jdbcTemplate.update(query,region.getCode(),region.getName());
        logger.info("Inserted region. Rows affected: {}", rowsAffected);
    }

    @Override
    public boolean existsRegionByCode(String code)  {
        logger.info("Checking if region with code: {} exists", code);
        String query = "SELECT COUNT(*) FROM regions WHERE UPPER(code) = ?";
       Integer count = jdbcTemplate.queryForObject(query, Integer.class, code.toUpperCase());
       boolean exists = count != null && count >0;
       logger.info("Region with code: {} exists: {}", code, exists);
       return exists;

    }
    @Override
    public void updateRegion(Region region) {
        String query = "UPDATE regions SET code = ?, name = ? WHERE ID = ?";
        int rowsAffected = jdbcTemplate.update(query,region.getCode(),region.getName(),region.getId());
        logger.info("Updated region, Rows affected: {}", rowsAffected);
    }
    @Override
    public void deleteRegion(Long id) {
        String query= "Delete FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(query,id);
        logger.info("Deleted user, Rows affected: {}", rowsAffected);
        }

    @Override
    public Region getRegionsById(Long id) {
    String query= "SELECT * FROM regions WHERE id = ?";
    try {
        Region region = jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(Region.class), id);
        logger.info("Region retrieved: {} - {}", region.getCode(),region.getName());
        return region;
    } catch (Exception e){
        logger.warn("No region found with id: {}", id);
        return null;
    }
    }
    @Override
    public boolean existsRegionByCodeAndNotId(String code, Long id){
        String query = "SELECT COUNT(*) FROM regions WHERE UPPER(code) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, code.toUpperCase(), id);
        boolean exists = count != null && count > 0;
        return exists;
    }
}

