package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.PointSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointSaleRepository extends JpaRepository<PointSale, Long> {

    @Query("SELECT p " +
        "FROM PointSale p " +
        "INNER JOIN p.branchOffice b " +
        "WHERE b.active = TRUE " +
        "AND b.id = :branchOfficeId")
    List<PointSale> findAllByCompanyId(Long branchOfficeId);


    @Query("SELECT p " +
        "FROM PointSale p " +
        "INNER JOIN p.branchOffice b " +
        "INNER JOIN p.branchOffice.company c " +
        "WHERE p.active = TRUE " +
        "AND b.active = TRUE " +
        "AND c.active = TRUE " +
        "AND p.pointSaleSiatId = :pointSaleSiatId " +
        "AND b.branchOfficeSiatId = :branchOfficeSiatId " +
        "AND c.id = :companyId")
    List<PointSale> findByPointSaleSiatIdBranchOfficeSiatIdActive(Integer pointSaleSiatId, Integer branchOfficeSiatId, Long companyId);

}
