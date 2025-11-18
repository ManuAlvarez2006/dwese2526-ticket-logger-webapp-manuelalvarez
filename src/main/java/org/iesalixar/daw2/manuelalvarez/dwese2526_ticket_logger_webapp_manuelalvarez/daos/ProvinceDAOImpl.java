package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Province;
import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class ProvinceDAOImpl implements ProvinceDAO{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ProvinceDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public ProvinceDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Province> provinceRowMapper = (rs, rowNum) -> {
        Province province = new Province();

        province.setId(rs.getLong("id"));
        province.setCode(rs.getString("code"));
        province.setName(rs.getString("name"));

        Region region = new Region();
        region.setId(rs.getLong("region_id"));
        region.setCode(rs.getString("region_code"));
        region.setName(rs.getString("region_name"));

        province.setRegion(region);

        return province;

    };

    @Override
    public List<Province> listAllProvinces(){
        logger.info("Listing all provinces with their regions from the database");
        String query =
                "SELECT p.id, p.code, p.name, " +
                        " r.id AS region_id, r.code AS region_code, r.name AS region_name " +
                        "FROM provinces p " +
                        "JOIN regions r ON p.region_id = r.id";
        List<Province> provinces = jdbcTemplate.query(query,provinceRowMapper);
        logger.info("Retrieved {} provinces from the database.", provinces.size());
        return provinces;
    }

    @Override
    public void deleteProvince(Long id) {
    logger.info("Deleting province with id: {}", id);
    String query = "DELETE FROM provinces WHERE id=?";
    int rowsAffected = jdbcTemplate.update(query, id);
    logger.info("Deleted province, Rows affected: {}", rowsAffected);
    }

    @Override
    public boolean existsProvinceByCode(String code){
        logger.info("Checking if province with code: {} exists", code);
        String query = "SELECT COUNT(*) FROM provinces WHERE UPPER(code) = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, code.toUpperCase());
        boolean exists = count != null && count > 0;
        logger.info("Province with code: {} exists {}", code, exists);
        return exists;
    }

    @Override
    public void updateProvince(Province province) {
        logger.info("Updating province with id: {}", province.getId());
        String query = "UPDATE provinces SET code = ?, name = ?, region_id = ? WHERE id= ?";
        int rowsAffected = jdbcTemplate.update(query,
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null,
                province.getId()
        );
        logger.info("Updated province, Rows affected: {}", rowsAffected);
    }


    @Override
    public boolean existsProvinceByCodeAndNotId(String code, Long id) {
        logger.info("Cheking if province with code: {} exists excluding id: {}", code, id);
        String query = "SELECT COUNT(*) FROM provinces WHERE UPPER(code) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, code.toUpperCase(), id);
        boolean exists = count !=null && count > 0;
        logger.info("Province with code: {} exists excluding id {}: {}", code, id, exists);
        return exists;
    }

    @Override
    public void insertProvince(Province province){
        logger.info("Inserting province with code: {}, name: {}, regionId: {}",
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null
        );
        String query= "INSERT INTO provinces (code, name, region_id) VALUES (?,?,?)";
        int rowsAffected = jdbcTemplate.update(query,
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null
        );

        logger.info("Inserted province, Rows affected: {}", rowsAffected);
    }
    @Override
    public Province getProvinceById(Long id){
        logger.info("Retrieving province by id: {}", id);
        String query =
                "SELECT p.id, p.code, p.name, " +
                        "       r.id AS region_id, r.code AS region_code, r.name AS region_name " +
                        "FROM provinces p " +
                        "JOIN regions r ON p.region_id = r.id " +
                        "WHERE p.id = ?";
        try {
            Province province = jdbcTemplate.queryForObject(query, provinceRowMapper, id);
            if (province != null){
            logger.info("province retrieved: {} - {}", province.getCode(), province.getName());
            }
            return province;
        } catch (Exception e){
            logger.warn("No province found with id: {}", id);
            return null;
        }
    }
}
