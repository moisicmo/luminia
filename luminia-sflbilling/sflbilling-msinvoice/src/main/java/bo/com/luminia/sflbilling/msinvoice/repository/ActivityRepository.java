package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.Activity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    String ACTIVITY_BY_SIAT_ID_CACHE = "activityBySiatId";

    @Cacheable(cacheNames = ACTIVITY_BY_SIAT_ID_CACHE)
    Optional<Activity> findActivityBySiatId(Integer siatId);

    List<Activity> findAllByCompanyIdAndActiveIsTrue(Integer activityId);
}
