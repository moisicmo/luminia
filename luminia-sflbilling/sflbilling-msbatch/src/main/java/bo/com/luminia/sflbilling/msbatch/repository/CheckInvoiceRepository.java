package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.CheckInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckInvoiceRepository extends JpaRepository<CheckInvoice, Long> {


    @Query(value = "SELECT " +
        " * " +
        "FROM " +
        "public.check_invoice ", nativeQuery = true)
    List<CheckInvoice> findAllNative();

}
