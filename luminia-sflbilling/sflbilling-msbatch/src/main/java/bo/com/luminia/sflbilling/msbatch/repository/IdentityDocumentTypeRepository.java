package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.IdentityDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdentityDocumentTypeRepository extends JpaRepository<IdentityDocumentType, Long> {
    List<IdentityDocumentType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
