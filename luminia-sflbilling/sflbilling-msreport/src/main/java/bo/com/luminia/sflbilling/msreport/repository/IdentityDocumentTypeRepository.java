package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.IdentityDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdentityDocumentTypeRepository extends JpaRepository<IdentityDocumentType, Long> {
    List<IdentityDocumentType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
    Optional<IdentityDocumentType> findAllByCompanyIdAndSiatIdAndActiveIsTrue(Integer companyId, Integer siatId);
}
