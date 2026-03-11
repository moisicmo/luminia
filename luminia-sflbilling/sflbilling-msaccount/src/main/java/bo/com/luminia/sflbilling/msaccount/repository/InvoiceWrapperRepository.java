package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.InvoiceWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceWrapperRepository extends JpaRepository<InvoiceWrapper, Long> {
}
