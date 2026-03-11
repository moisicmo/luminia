package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.InvoiceBatch;
import bo.com.luminia.sflbilling.domain.InvoiceWrapperEvent;
import bo.com.luminia.sflbilling.domain.PendingToFix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PendingToFixRepository extends JpaRepository<PendingToFix, Long> {


    @Query(
        "SELECT p FROM PendingToFix p " +
            "WHERE p.invoiceId = :invoiceId ")
    Optional<PendingToFix> findByInvoiceId(Long invoiceId);


}
