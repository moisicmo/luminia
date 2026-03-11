package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.WrapperEvent;
import bo.com.luminia.sflbilling.domain.enumeration.WrapperEventStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WrapperEventRepository extends JpaRepository<WrapperEvent, Long> {

    @Query("SELECT w " +
        "FROM WrapperEvent w " +
        "INNER JOIN w.branchOffice b " +
        "INNER JOIN w.branchOffice.company c " +
        "INNER JOIN w.pointSale p " +
        "WHERE b.active = TRUE " +
        "AND p.active = TRUE " +
        "AND c.active = TRUE " +
        "AND w.status = 'PENDING' " +
        "AND c.id = :companyId")
    List<WrapperEvent> findPendingByCompanyId(Long companyId);

    Optional<WrapperEvent> findFirstByStatusOrderByIdAsc(WrapperEventStatusEnum status);
}
