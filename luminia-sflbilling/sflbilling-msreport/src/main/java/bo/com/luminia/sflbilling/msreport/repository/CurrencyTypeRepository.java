package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CurrencyTypeRepository extends JpaRepository<CurrencyType, Long> {
    List<CurrencyType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);

    Optional<CurrencyType> findCurrencyTypeBySiatIdAndActiveIsTrue(Integer siatId);

    Optional<CurrencyType> findDistinctFirstBySiatIdAndActiveIsTrue(Integer siatId);

    Optional<CurrencyType> findAllByCompanyIdAndSiatIdAndActiveIsTrue(Integer companyId, Integer siatId);
}
