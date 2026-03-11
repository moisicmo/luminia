package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.Cuis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CuisRepository extends JpaRepository<Cuis, Long> {

    @Query("SELECT c " +
        "FROM Cuis c " +
        "INNER JOIN c.pointSale p " +
        "INNER JOIN c.pointSale.branchOffice b " +
        "INNER JOIN c.pointSale.branchOffice.company a " +
        "WHERE c.active = TRUE " +
        "AND p.active = TRUE " +
        "AND b.active = TRUE " +
        "AND a.active = TRUE " +
        "AND p.pointSaleSiatId = :pointSaleSiatId " +
        "AND b.branchOfficeSiatId = :branchOfficeSiatId " +
        "AND a.id = :companyId")
    Optional<Cuis> findByPointSaleSiatIdBranchOfficeSiatIdActive(Integer pointSaleSiatId, Integer branchOfficeSiatId, Long companyId);
}
