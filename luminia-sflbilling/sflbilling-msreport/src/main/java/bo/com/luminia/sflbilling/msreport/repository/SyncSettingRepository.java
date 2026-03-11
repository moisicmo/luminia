package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.SyncSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncSettingRepository extends JpaRepository<SyncSetting, Long> {
}
