package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.DocumentSector;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentSectorRepository extends JpaRepository<DocumentSector, Long> {

    String DOCUMENT_BY_SIAT_ID_CACHE = "documentBySiatId";

    @Cacheable(cacheNames = DOCUMENT_BY_SIAT_ID_CACHE)
    Optional<DocumentSector> findDocumentBySiatId(Integer siatId);

    List<DocumentSector> findAllByCompanyIdAndActiveIsTrue(Integer companyId);

    Optional<DocumentSector> findDocumentSectorBySiatIdAndCompanyIdAndActiveIsTrue(Integer siatId, Integer companyId);
}
