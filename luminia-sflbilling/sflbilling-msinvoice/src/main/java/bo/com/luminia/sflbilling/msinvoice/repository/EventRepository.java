package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT DISTINCT e " +
        "FROM Event e " +
        "INNER JOIN e.pointSale p " +
        "INNER JOIN e.branchOffice b " +
        "INNER JOIN e.branchOffice.company a  " +
        "WHERE e.active = TRUE " +
        "AND p.active = TRUE " +
        "AND b.active = TRUE " +
        "AND a.active = TRUE " +
        "AND p.pointSaleSiatId = :pointSaleSiatId " +
        "AND b.branchOfficeSiatId = :branchOfficeSiatId " +
        "AND a.id = :companyId")
    List<Event> findDistinctByPointSaleSiatIdBranchOfficeSiatIdActive(Integer branchOfficeSiatId, Integer pointSaleSiatId, Long companyId);
}
