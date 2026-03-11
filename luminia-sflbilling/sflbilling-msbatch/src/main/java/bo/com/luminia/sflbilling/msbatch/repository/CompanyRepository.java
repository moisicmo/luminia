package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findAllByEventSendIsTrueAndActiveIsTrue();

    Optional<Company> findByIdAndActiveTrue(Long id);

    Optional<Company> findCompanyByIdAndActiveIsTrue(Long id);

    Optional<Company> findByBusinessCodeAndActiveTrue(String businessCode);

    List<Company> findAllByActiveTrue();
}
