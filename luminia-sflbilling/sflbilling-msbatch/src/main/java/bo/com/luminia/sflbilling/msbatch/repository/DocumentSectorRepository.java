package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.DocumentSector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentSectorRepository extends JpaRepository<DocumentSector, Long> {
    List<DocumentSector> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
