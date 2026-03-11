package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SignatureRepository extends JpaRepository<Signature, Long> {
    @Query("SELECT s " +
        "FROM Signature s " +
        "INNER JOIN s.company c " +
        "WHERE s.active = TRUE " +
        "AND c.id = :companyId")
    Optional<Signature> findByCompanyActive(Long companyId);
}
