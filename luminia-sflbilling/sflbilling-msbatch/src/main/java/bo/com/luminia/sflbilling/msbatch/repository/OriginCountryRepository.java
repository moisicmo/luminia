package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.OriginCountry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OriginCountryRepository extends JpaRepository<OriginCountry, Long> {
    List<OriginCountry> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
