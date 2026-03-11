package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long> {
    List<MeasurementUnit> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
    Optional<MeasurementUnit> findFirstBySiatIdAndCompanyIdAndActiveIsTrue(Integer siatId, Integer companyId);
}
