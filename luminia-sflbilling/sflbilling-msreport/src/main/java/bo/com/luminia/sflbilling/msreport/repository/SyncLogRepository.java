package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
}
