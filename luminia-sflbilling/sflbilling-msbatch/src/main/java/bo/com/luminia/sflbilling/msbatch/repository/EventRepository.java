package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e " +
        "FROM Event e " +
        "INNER JOIN e.pointSale p " +
        "INNER JOIN e.branchOffice b " +
        "INNER JOIN e.branchOffice.company a " +
        "WHERE e.active = TRUE " +
        "AND p.active = TRUE " +
        "AND b.active = TRUE " +
        "AND a.active = TRUE " +
        "AND p.pointSaleSiatId = :pointSaleSiatId " +
        "AND b.branchOfficeSiatId = :branchOfficeSiatId " +
        "AND a.id = :companyId")
    List<Event> findByPointSaleSiatIdBranchOfficeSiatIdActive(Integer branchOfficeSiatId, Integer pointSaleSiatId, Long companyId);

    Optional<Event> findFirstByActiveTrueOrderByIdAsc();

    @Transactional
    @Modifying
    @Query(value = "UPDATE " +
        " \"event\" " +
        "set active = false " +
        "where id in( " +
        "   select e.id " +
        "   from \"event\" e " +
        "   inner join cufd c on e.cufd_event = c.cufd " +
        "   where e.active = true " +
        "   and c.active = false " +
        "   and not exists (select 1 from invoice i where i.cufd_id = c.id and  i.status='PENDING') " +
        ")", nativeQuery = true)
    void disableEventWithoutInvoice();


    List<Event> findByCufdEvent(String cufdEvent);


}
