package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.InvoiceLegend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceLegendRepository extends JpaRepository<InvoiceLegend, Long> {

    List<InvoiceLegend> findAllByCompanyIdAndActiveIsTrue(Integer id);

    @Query(
        "SELECT il from InvoiceLegend il , DocumentSector ds " +
            "WHERE ds.activityCode=il.activityCode " +
            "and il.companyId =?1 " +
            "and ds.siatId=?2 " +
            "and ds.active = true"
    )
    List<InvoiceLegend> findAllByCompanyIdAndActivityCodeAndActiveIsTrue(Integer id, Integer activityId);

}
