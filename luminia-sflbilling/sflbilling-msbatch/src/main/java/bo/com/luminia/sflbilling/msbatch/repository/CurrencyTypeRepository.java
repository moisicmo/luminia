package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyTypeRepository extends JpaRepository<CurrencyType, Long> {
    List<CurrencyType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
