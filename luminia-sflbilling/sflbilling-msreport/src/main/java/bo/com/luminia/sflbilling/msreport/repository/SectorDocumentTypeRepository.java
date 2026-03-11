package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.SectorDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectorDocumentTypeRepository extends JpaRepository<SectorDocumentType, Long> {
    List<SectorDocumentType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);

    Optional<SectorDocumentType> findAllByCompanyIdAndSiatIdAndActiveIsTrue(Integer companyId, Integer siatId);
}
