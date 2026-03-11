package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.SectorDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectorDocumentTypeRepository extends JpaRepository<SectorDocumentType, Long> {
    List<SectorDocumentType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
