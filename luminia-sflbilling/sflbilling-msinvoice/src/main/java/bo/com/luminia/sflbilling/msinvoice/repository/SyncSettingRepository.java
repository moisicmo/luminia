package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.SyncSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncSettingRepository extends JpaRepository<SyncSetting, Long> {
}
