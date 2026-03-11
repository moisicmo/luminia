package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.Invoice;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByCufAndStatus(String cuf, InvoiceStatusEnum status);
    Optional<Invoice> findByCuf(String cuf);
}
