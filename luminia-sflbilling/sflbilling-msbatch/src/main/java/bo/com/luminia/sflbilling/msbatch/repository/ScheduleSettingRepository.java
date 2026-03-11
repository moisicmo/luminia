package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.ScheduleSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleSettingRepository extends JpaRepository<ScheduleSetting, Long> {
    List<ScheduleSetting> findAllByActiveTrue();
}
