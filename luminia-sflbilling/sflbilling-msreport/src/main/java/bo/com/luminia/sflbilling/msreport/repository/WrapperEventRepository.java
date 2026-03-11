package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.WrapperEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface WrapperEventRepository extends JpaRepository<WrapperEvent, Long> {

    @Query(value = "SELECT \n" +
        "  w.id,\n" +
        "  e.cufd_event,\n" +
        "  e.start_date,\n" +
        "  e.end_date,\n" +
        "  w.reception_code,\n" +
        "  w.status,\n" +
        "  c.name company_name,\n" +
        "  c.id company_id,\n" +
        "  b.name branch_office_name,\n" +
        "  w.branch_office_id,\n" +
        "  p.name point_sale_name,\n" +
        "  w.point_sale_id,\n" +
        "  se.description significant_event_name,\n" +
        "  e.description significant_event_description,\n" +
        "  e.significant_event_id,  \n" +
        "  s.description sector_document_type_name,\n" +
        "  w.sector_document_type_id\n" +
        "FROM \n" +
        "  public.wrapper_event w\n" +
        "  INNER JOIN public.event e ON w.event_id = e.id\n" +
        "  INNER JOIN public.point_sale p ON w.point_sale_id = p.id\n" +
        "  INNER JOIN public.branch_office b ON w.branch_office_id = b.id\n" +
        "  INNER JOIN public.company c ON b.company_id = c.id\n" +
        "  INNER JOIN public.sector_document_type s ON w.sector_document_type_id = s.id\n" +
        "  INNER JOIN public.significant_event se ON e.significant_event_id = se.id\n" +
        "WHERE\n" +
        "  c.id = :companyId\n" +
        "  AND e.start_date >= :startDate\n" +
        "  AND e.end_date <= :endDate ", nativeQuery = true)
    List findFullByCompanyId(@Param("companyId") Long companyId,
                             @Param("startDate") Timestamp startDate,
                             @Param("endDate") Timestamp endDate);
}
