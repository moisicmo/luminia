package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Wrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WrapperRepository extends JpaRepository<Wrapper, Long> {

    @Query("SELECT w " +
        "FROM Wrapper w " +
        "INNER JOIN w.branchOffice b " +
        "INNER JOIN w.branchOffice.company c " +
        "INNER JOIN w.pointSale p " +
        "WHERE b.active = TRUE " +
        "AND p.active = TRUE " +
        "AND c.active = TRUE " +
        "AND w.status = 'PENDING' " +
        "AND c.id = :companyId")
    List<Wrapper> findPendingByCompanyId(Long companyId);
}
