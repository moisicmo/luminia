package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    public Optional<Invoice> findInvoiceByCuf(String cuf);

    @Query(value = "select amount from view_amount_from_invoice(:id) "
        , nativeQuery = true)
    Double findAmount(@Param("id") Long id);

}
