package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.InvoiceBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceBatchRepository extends JpaRepository<InvoiceBatch, Long> {
    List<InvoiceBatch> findByBatchId(Long batchId);
    InvoiceBatch findByInvoiceId(Long invoiceId);
}
