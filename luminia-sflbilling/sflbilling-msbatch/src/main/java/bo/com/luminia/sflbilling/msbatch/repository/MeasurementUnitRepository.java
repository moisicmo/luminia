package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long> {
    List<MeasurementUnit> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
