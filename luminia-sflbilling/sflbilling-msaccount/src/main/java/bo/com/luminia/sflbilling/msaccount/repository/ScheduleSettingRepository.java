package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.ScheduleSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleSettingRepository extends JpaRepository<ScheduleSetting, Long> {
}
