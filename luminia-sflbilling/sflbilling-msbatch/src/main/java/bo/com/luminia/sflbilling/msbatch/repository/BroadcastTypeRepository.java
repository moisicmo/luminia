package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.BroadcastType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BroadcastTypeRepository extends JpaRepository<BroadcastType, Long> {
    List<BroadcastType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);

    BroadcastType getBySiatIdAndCompanyIdAndActiveIsTrue(Integer siatId, Integer companyId);
}
