package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.InvoiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InvoiceRequestRepository extends JpaRepository<InvoiceRequest, Long> {
}
