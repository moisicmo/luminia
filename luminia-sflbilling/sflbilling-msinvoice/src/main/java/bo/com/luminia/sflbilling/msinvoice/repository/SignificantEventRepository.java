package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.SignificantEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignificantEventRepository extends JpaRepository<SignificantEvent, Long> {

    List<SignificantEvent> findAllByCompanyIdAndActiveIsTrue(Integer companyId);

    SignificantEvent findByCompanyIdAndSiatIdAndActiveTrue(Integer companyId, Integer siatId);
}
