package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.Signature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureRepository extends JpaRepository<Signature, Long> {

    Optional<Signature> findByCompanyIdAndIdAndActiveIsTrue(Long companyId, Long id);

    Optional<Signature> findByCompanyIdAndActiveIsTrue(Long companyId);
}
