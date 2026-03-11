package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
