package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
