package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.PointSaleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointSaleTypeRepository extends JpaRepository<PointSaleType, Long> {
    List<PointSaleType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
