package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.CancellationReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CancellationReasonRepository extends JpaRepository<CancellationReason, Long> {
    List<CancellationReason> findAllByCompanyIdAndActiveIsTrue(Integer id);
}
