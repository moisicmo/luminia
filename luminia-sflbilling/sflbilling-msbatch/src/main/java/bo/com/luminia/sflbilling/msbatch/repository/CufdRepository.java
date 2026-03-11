package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Cufd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CufdRepository extends JpaRepository<Cufd, Long> {

    @Query("SELECT d " +
        "FROM Cufd d " +
        "INNER JOIN d.cuis c " +
        "INNER JOIN d.cuis.pointSale p " +
        "INNER JOIN d.cuis.pointSale.branchOffice b " +
        "INNER JOIN d.cuis.pointSale.branchOffice.company a " +
        "WHERE d.active = TRUE " +
        "AND c.active = TRUE " +
        "AND p.active = TRUE " +
        "AND b.active = TRUE " +
        "AND a.active = TRUE " +
        "AND p.pointSaleSiatId = :pointSaleSiatId " +
        "AND b.branchOfficeSiatId = :branchOfficeSiatId " +
        "AND a.id = :companyId")
    Optional<Cufd> findByPointSaleSiatIdBranchOfficeSiatIdActive(Integer pointSaleSiatId, Integer branchOfficeSiatId, Long companyId);

    Optional<Cufd> findTop1ByActiveTrueAndCuisIdOrderByStartDateDesc(Long cuisId);


}
