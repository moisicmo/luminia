package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.InvoiceWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceWrapperRepository extends JpaRepository<InvoiceWrapper, Long> {
    List<InvoiceWrapper> findAllByWrapperId(Long id);
}
