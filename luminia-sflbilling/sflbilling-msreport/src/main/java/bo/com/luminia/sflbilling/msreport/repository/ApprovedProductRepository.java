package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.ApprovedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApprovedProductRepository extends JpaRepository<ApprovedProduct, Long> {

    @Query(value = "SELECT \n" +
        "  a.id,\n" +
        "  a.product_code,\n" +
        "  a.description,  \n" +
        "  p.activity_code activity_code_siat,\n" +
        "  p.siat_id product_code_siat,\n" +
        "  m.siat_id measurement_unit_siat\n" +
        "FROM \n" +
        "  public.approved_product a\n" +
        "  INNER JOIN public.product_service p ON a.product_service_id = p.id\n" +
        "  INNER JOIN public.measurement_unit m ON a.measurement_unit_id = m.id\n" +
        "WHERE\n" +
        "  p.active = TRUE \n" +
        "  AND m.active = TRUE\n" +
        "  AND a.company_id = :companyId", nativeQuery = true)
    List findAllByCompanyIdAndActive(@Param("companyId") Long companyId);

    public Optional<ApprovedProduct> findApprovedProductByCompanyIdAndProductCode(Long companyId, String productCode);
}
