package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    String COMPANY_BY_NAME = "companyByName";

    @Cacheable(cacheNames = COMPANY_BY_NAME)
    Optional<Company> findOneByName(String name);

    List<Company> findCompanyByNit(Long nit);

    Optional<Company> findByIdAndActiveTrue(Long id);

    List<Company> findCompanyByNitAndActiveIsTrue(Long nit);

    boolean existsByNitAndModalitySiat(Long nit, ModalitySiatEnum modalitySiatEnum);

    Optional<Company> findByBusinessCodeAndActiveTrue(String businessCode);
}
