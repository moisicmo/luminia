package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Batch;
import bo.com.luminia.sflbilling.domain.enumeration.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    Optional<Batch> findFirstByStatusOrderByDateAsc(BatchStatusEnum status);
}
