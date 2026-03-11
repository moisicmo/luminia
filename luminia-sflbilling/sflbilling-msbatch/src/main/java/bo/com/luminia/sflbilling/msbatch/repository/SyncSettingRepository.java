package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.SyncSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncSettingRepository extends JpaRepository<SyncSetting, Long> {
    List<SyncSetting> findAllBySyncTypeAndActiveTrue(Integer syncType);
}
