package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    @Query(value = "SELECT \n" +
        "b.reception_code,\n" +
        "b.date,\n" +
        "b.status\n" +
        "FROM public.batch b \n" +
        "INNER JOIN public.company c ON b.company_id = c.id \n" +
        "WHERE c.business_code = :businessCode\n" +
        "AND b.date >= :startDate\n" +
        "AND b.date <= :endDate\n" +
        "ORDER BY b.date ASC ", nativeQuery = true)
    List findByBusinessCode(@Param("businessCode") String businessCode,
                            @Param("startDate") Timestamp startDate,
                            @Param("endDate") Timestamp endDate);
}
