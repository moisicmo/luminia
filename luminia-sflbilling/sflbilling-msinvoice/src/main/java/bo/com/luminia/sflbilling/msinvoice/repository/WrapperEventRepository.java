package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.WrapperEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrapperEventRepository extends JpaRepository<WrapperEvent, Long> {
}
