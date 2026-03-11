package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.InvoiceLegend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceLegendRepository extends JpaRepository<InvoiceLegend, Long> {

    List<InvoiceLegend> findAllByCompanyIdAndActiveIsTrue(Integer id);
}
