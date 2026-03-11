package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.Company;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    String COMPANY_BY_NAME = "companyByName";
    String COMPANY_BY_HASH = "companyByHash";

    @Cacheable(cacheNames = COMPANY_BY_NAME)
    Optional<Company> findOneByName(String name);

    Optional<Company> findByBusinessCodeAndActiveTrue(String businessCode);

    Optional<Company> findByBusinessCode(String businessCode);
}
