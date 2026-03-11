package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.ValidIdentityDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ValidIdentityDocumentTypeRepository extends JpaRepository<ValidIdentityDocumentType, Long> {

    @Query(
        value = "SELECT * FROM valid_identity_document_type " +
                "WHERE siat_code = :siatCode AND document = :document " +
                " LIMIT 1"
        , nativeQuery = true
    )
    Optional<ValidIdentityDocumentType> findBySiatCodeAndDocument(Byte siatCode, String document);

    List<ValidIdentityDocumentType> findByDocument(String document);
}
