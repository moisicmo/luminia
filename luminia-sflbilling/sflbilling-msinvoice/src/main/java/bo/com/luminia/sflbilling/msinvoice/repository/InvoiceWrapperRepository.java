package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.InvoiceWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceWrapperRepository extends JpaRepository<InvoiceWrapper, Long> {
}
