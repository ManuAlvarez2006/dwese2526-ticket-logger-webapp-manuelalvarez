package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;



import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Region;

import java.util.List;

public interface RegionDAO {

    List<Region> listAllRegions() ;
    void insertRegion(Region region) ;
    void updateRegion(Region region) ;
    void deleteRegion(Long id) ;
    Region getRegionsById(Long id) ;
    boolean existsRegionByCode(String code) ;
    boolean existsRegionByCodeAndNotId(String code, Long id) ;


}
