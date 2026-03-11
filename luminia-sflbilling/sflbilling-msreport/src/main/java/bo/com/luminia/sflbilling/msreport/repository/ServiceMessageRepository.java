package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.ServiceMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceMessageRepository extends JpaRepository<ServiceMessage, Long> {

    List<ServiceMessage> findAllByCompanyIdAndActiveIsTrue(Integer id);
}
