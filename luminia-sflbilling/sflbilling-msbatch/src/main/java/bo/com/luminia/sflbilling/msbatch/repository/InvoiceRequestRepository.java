package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.InvoiceRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InvoiceRequestRepository extends JpaRepository<InvoiceRequest, Long> {

    @Query(value = "SELECT i \n" +
        "FROM InvoiceRequest i\n" +
        "WHERE \n" +
        "     i.response = false \n" +
        "     AND  i.errorChecked = false \n" +
        "     AND i.type = 'EMIT' \n" +
        "ORDER BY i.id \n"
    )
    List findFaildRequests(Pageable pageable);





}
