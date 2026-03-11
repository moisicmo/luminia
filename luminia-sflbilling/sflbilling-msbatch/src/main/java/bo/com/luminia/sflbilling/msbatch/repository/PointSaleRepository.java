package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.PointSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointSaleRepository extends JpaRepository<PointSale, Long> {

    @Query(value = "SELECT\n" +
        "  c.id company_id,\n" +
        "  b.branch_office_siat_id,\n" +
        "  b.name branch_office,\n" +
        "  p.point_sale_siat_id,\n" +
        "  p.name point_sale\n" +
        "FROM \n" +
        "  public.company c\n" +
        "  INNER JOIN public.branch_office b ON c.id = b.company_id \n" +
        "  INNER JOIN public.point_sale p ON b.id = p.branch_office_id\n" +
        "WHERE\n" +
        "  b.active = TRUE\n" +
        "  AND p.active = TRUE\n" +
        "  AND c.active = TRUE\n" +
        "  AND c.id = :companyId\n" +
        "ORDER BY\n" +
        "  b.branch_office_siat_id,\n" +
        "  p.point_sale_siat_id;", nativeQuery = true)
    List findAllByCompanyIdAndActive(@Param("companyId") Long companyId);

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
    PointSale findByPointSaleSiatIdBranchOfficeSiatIdActive(Integer pointSaleSiatId, Integer branchOfficeSiatId, Long companyId);

    List<PointSale> findAllByBranchOfficeIdAndActiveIsTrue(Long branchOfficeId);

    @Query(value = "SELECT p.* \n" +
        "FROM \n" +
        "  public.company c \n" +
        "  INNER JOIN public.branch_office b ON c.id = b.company_id \n" +
        "  INNER JOIN public.point_sale p ON b.id = p.branch_office_id \n" +
        "WHERE\n" +
        "  b.active = TRUE \n" +
        "  AND p.active = TRUE \n" +
        "  AND c.active = TRUE \n" +
        "  AND c.id = :companyId \n" +
        "  AND p.point_sale_siat_id = :pointSaleSiatId \n" +
        "  AND b.branch_office_siat_id = :branchOfficeSiatId \n" ,
        nativeQuery = true)
    Optional<PointSale> findByCompanyAndSiatId(@Param("companyId") Long companyId, @Param("pointSaleSiatId") Integer pointSaleSiatId,  @Param("branchOfficeSiatId") Integer branchOfficeSiatId);
}
