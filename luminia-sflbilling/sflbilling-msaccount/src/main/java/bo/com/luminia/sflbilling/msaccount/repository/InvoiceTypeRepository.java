package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceTypeRepository extends JpaRepository<InvoiceType, Long> {
    List<InvoiceType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
