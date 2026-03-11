package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.InvoiceWrapperEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceWrapperEventRepository extends JpaRepository<InvoiceWrapperEvent, Long> {
}
