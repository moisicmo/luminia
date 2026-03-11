package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.ApprovedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApprovedProductRepository extends JpaRepository<ApprovedProduct, Long> {

    Optional<ApprovedProduct> findByCompanyIdAndId(Long companyId, Long id);

    Optional<ApprovedProduct> findByCompanyIdAndProductCode(Long companyId, String productCode);

    List<ApprovedProduct> findAllByCompanyId(Long companyId);

    List<ApprovedProduct> findAllByCompanyIdAndProductServiceId(Long companyId, Long productServiceId);

    List<ApprovedProduct> findAllByCompanyIdAndMeasurementUnitId(Long companyId, Long measurementUnitId);

    @Query(value = "SELECT \n" +
        "  products.*\n" +
        "FROM(\n" +
        "  SELECT DISTINCT\n" +
        "  json_array_elements(CAST(i.invoice_json AS JSON) -> 'detalle') ->> 'codigoProducto' AS product_code\n" +
        "  FROM public.invoice i \n" +
        "  INNER JOIN public.cufd d ON i.cufd_id = d.id\n" +
        "  INNER JOIN public.cuis s ON d.cuis_id = s.id\n" +
        "  INNER JOIN public.point_sale p ON s.point_sale_id = p.id\n" +
        "  INNER JOIN public.branch_office b ON p.branch_office_id = b.id\n" +
        "WHERE \n" +
        "  b.company_id = :companyId \n" +
        "  AND b.active = TRUE\n" +
        "  AND p.active = TRUE) products\n" +
        "WHERE\n" +
        "  products.product_code = :productCode ", nativeQuery = true)
    List findAllByCompanyIdAndProductCode(@Param("companyId") Long companyId, @Param("productCode") String productCode);
}
