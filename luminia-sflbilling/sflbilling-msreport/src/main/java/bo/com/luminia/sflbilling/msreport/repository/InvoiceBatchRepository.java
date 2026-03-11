package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.InvoiceBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceBatchRepository extends JpaRepository<InvoiceBatch, Long> {

    @Query(value = "SELECT \n" +
        "ib.invoice_number, \n" +
        "i.cuf, \n" +
        "CASE \n" +
        "WHEN iw.id IS NOT NULL THEN iw.status\n" +
        "WHEN ib.invoice_id IS NOT NULL THEN i.status\n" +
        "WHEN ib.invoice_id IS NULL THEN 'PENDING'\n" +
        "END status,\n" +
        "CASE \n" +
        "WHEN iw.id IS NOT NULL THEN 'SIAT'\n" +
        "WHEN ib.invoice_id IS NOT NULL THEN 'LUMINIA'\n" +
        "WHEN ib.invoice_id IS NULL THEN null \n" +
        "END validation_source,\n" +
        "CASE \n" +
        "WHEN iw.id IS NOT NULL THEN iw.response_message\n" +
        "WHEN ib.invoice_id IS NOT NULL THEN ib.response_message\n" +
        "WHEN ib.invoice_id IS NULL THEN null \n" +
        "END response_message\n" +
        "FROM batch b \n" +
        "INNER JOIN invoice_batch ib ON b.id = ib.batch_id\n" +
        "LEFT JOIN invoice i ON ib.invoice_id = i.id \n" +
        "LEFT JOIN invoice_wrapper iw ON ib.batch_id = iw.invoice_batch_id\n" +
        "WHERE b.reception_code = :receptionCode ", nativeQuery = true)
    List findAllByReceptionCode(@Param("receptionCode") String receptionCode);
}
