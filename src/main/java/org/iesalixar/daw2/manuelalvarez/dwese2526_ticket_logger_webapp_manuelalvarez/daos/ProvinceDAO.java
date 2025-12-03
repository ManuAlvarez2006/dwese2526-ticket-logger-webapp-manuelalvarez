package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.daos;

import org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities.Province;

import java.util.List;

public interface ProvinceDAO {

    List<Province> listAllProvinces();
    List<Province> listProvincesPage(int page, int size, String sortField, String sortDir);
    long countProvinces();
    void deleteProvince(Long id);
    boolean existsProvinceByCodeAndNotId(String code, Long id);
    void insertProvince(Province province);
    boolean existsProvinceByCode(String code);
    void updateProvince(Province province);
    Province getProvinceById(Long id);
}
