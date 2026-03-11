package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.InvoiceWrapperEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InvoiceWrapperEventRepository extends JpaRepository<InvoiceWrapperEvent, Long> {
    List<InvoiceWrapperEvent> findAllByWrapperEventId(Long companyId);

    @Query(
       "FROM InvoiceWrapperEvent " +
       "WHERE invoice.id = :invoiceId ")
    Optional<InvoiceWrapperEvent> findByInvoiceId(@Param("invoiceId") Long invoiceId);


    @Transactional
    @Modifying
    void deleteByInvoiceId(long id);
}
