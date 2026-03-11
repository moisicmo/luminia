package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.Cuis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
        "AND p.pointSaleSiatId = :pointSaleSiatId " +
        "AND b.branchOfficeSiatId = :branchOfficeSiatId " +
        "AND a.id = :companyId")
    Optional<Cuis> findByPointSaleSiatIdBranchOfficeSiatIdActive(Integer pointSaleSiatId, Integer branchOfficeSiatId, Long companyId);

    @Query(value = "SELECT\n" +
        "  c.id company_id,\n" +
        "  b.branch_office_siat_id,\n" +
        "  b.name branch_office,\n" +
        "  p.point_sale_siat_id,\n" +
        "  p.name point_sale,\n" +
        "  s.cuis\n" +
        "FROM \n" +
        "  public.company c\n" +
        "  INNER JOIN public.branch_office b ON c.id = b.company_id \n" +
        "  INNER JOIN public.point_sale p ON b.id = p.branch_office_id\n" +
        "  INNER JOIN public.cuis s ON p.id = s.point_sale_id\n" +
        "WHERE\n" +
        "  b.active = TRUE\n" +
        "  AND p.active = TRUE\n" +
        "  AND s.active = TRUE\n" +
        "  AND c.id = :companyId\n" +
        "ORDER BY\n" +
        "  b.branch_office_siat_id,\n" +
        "  p.point_sale_siat_id;", nativeQuery = true)
    List findAllByCompanyIdAndActive(@Param("companyId") Long companyId);
}
