package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = "SELECT \n" +
        "  i.id,\n" +
        "  i.invoice_xml\n" +
        "FROM public.invoice i\n" +
        "INNER JOIN public.cufd d ON i.cufd_id = d.id\n" +
        "INNER JOIN public.cuis s ON d.cuis_id = s.id\n" +
        "INNER JOIN public.point_sale p ON s.point_sale_id = p.id\n" +
        "INNER JOIN public.branch_office b ON p.branch_office_id = b.id\n" +
        "INNER JOIN public.company c ON b.company_id = c.id\n" +
        "INNER JOIN public.broadcast_type bt ON i.broadcast_type_id = bt.id\n" +
        "INNER JOIN public.sector_document_type st ON i.sector_document_type_id = st.id\n" +
        "WHERE\n" +
        "  b.active = TRUE\n" +
        "  AND p.active = TRUE\n" +
        "  AND s.active = TRUE\n" +
        "  AND bt.active = TRUE\n" +
        "  AND st.active = TRUE\n" +
        "  AND c.active = TRUE\n" +
        "  AND st.siat_id = :sectorDocumentType\n" +
        "  AND bt.siat_id = :broadcastType\n" +
        "  AND i.status = 'PENDING'\n" +
        "  AND c.id = :companyId\n" +
        "  AND b.branch_office_siat_id = :branchOfficeSiat\n" +
        "  AND p.point_sale_siat_id = :pointSaleSiat\n" +
        "  AND i.id NOT IN (SELECT ib.invoice_id \n" +
        "                   FROM public.invoice_wrapper iw\n" +
        "                   INNER JOIN public.invoice_batch ib ON iw.invoice_batch_id = ib.id\n" +
        "                   WHERE i.id = ib.invoice_id)\n" +
        "LIMIT :numberInvoice ", nativeQuery = true)
    List findAllMassiveInvoice(@Param("companyId") Long companyId,
                               @Param("branchOfficeSiat") Integer branchOfficeSiat,
                               @Param("pointSaleSiat") Integer pointSaleSiat,
                               @Param("sectorDocumentType") Integer sectorDocumentType,
                               @Param("broadcastType") Integer broadcastType,
                               @Param("numberInvoice") Integer numberInvoice);

    @Query(value = "SELECT \n" +
        "  i.id,\n" +
        "  i.invoice_xml\n" +
        "FROM public.invoice i\n" +
        "INNER JOIN public.cufd d ON i.cufd_id = d.id\n" +
        "INNER JOIN public.cuis s ON d.cuis_id = s.id\n" +
        "INNER JOIN public.point_sale p ON s.point_sale_id = p.id\n" +
        "INNER JOIN public.branch_office b ON p.branch_office_id = b.id\n" +
        "INNER JOIN public.company c ON b.company_id = c.id\n" +
        "INNER JOIN public.broadcast_type bt ON i.broadcast_type_id = bt.id\n" +
        "INNER JOIN public.sector_document_type st ON i.sector_document_type_id = st.id\n" +
        "WHERE\n" +
        "  b.active = TRUE\n" +
        "  AND p.active = TRUE\n" +
        "  AND s.active = TRUE\n" +
        "  AND bt.active = TRUE\n" +
        "  AND st.active = TRUE\n" +
        "  AND c.active = TRUE\n" +
        "  AND st.siat_id = :sectorDocumentType\n" +
        "  AND bt.siat_id = :broadcastType\n" +
        "  AND i.status = 'PENDING'\n" +
        "  AND c.id = :companyId\n" +
        "  AND b.branch_office_siat_id = :branchOfficeSiat\n" +
        "  AND p.point_sale_siat_id = :pointSaleSiat\n" +
        "  AND i.id NOT IN (SELECT iw.invoice_id \n" +
        "                   FROM public.invoice_wrapper_event iw\n" +
        "                   WHERE i.id = iw.invoice_id)\n" +
        "LIMIT :numberInvoice ", nativeQuery = true)
    List findAllPackInvoice(@Param("companyId") Long companyId,
                            @Param("branchOfficeSiat") Integer branchOfficeSiat,
                            @Param("pointSaleSiat") Integer pointSaleSiat,
                            @Param("sectorDocumentType") Integer sectorDocumentType,
                            @Param("broadcastType") Integer broadcastType,
                            @Param("numberInvoice") Integer numberInvoice);

    @Query(value = "SELECT \n" +
        "  i.id,\n" +
        "  i.invoice_xml\n" +
        "FROM public.invoice i\n" +
        "INNER JOIN public.cufd d ON i.cufd_id = d.id\n" +
        "INNER JOIN public.cuis s ON d.cuis_id = s.id\n" +
        "INNER JOIN public.point_sale p ON s.point_sale_id = p.id\n" +
        "INNER JOIN public.branch_office b ON p.branch_office_id = b.id\n" +
        "INNER JOIN public.company c ON b.company_id = c.id\n" +
        "INNER JOIN public.broadcast_type bt ON i.broadcast_type_id = bt.id\n" +
        "INNER JOIN public.sector_document_type st ON i.sector_document_type_id = st.id\n" +
        "WHERE\n" +
        "  b.active = TRUE\n" +
        "  AND p.active = TRUE\n" +
        "  AND s.active = TRUE\n" +
        "  AND bt.active = TRUE\n" +
        "  AND st.active = TRUE\n" +
        "  AND c.active = TRUE\n" +
        "  AND st.siat_id = :sectorDocumentType\n" +
        "  AND d.cufd = :cufd\n" +
        "  AND bt.siat_id = :broadcastType\n" +
        "  AND i.status = 'PENDING'\n" +
        "  AND c.id = :companyId\n" +
        "  AND b.branch_office_siat_id = :branchOfficeSiat\n" +
        "  AND p.point_sale_siat_id = :pointSaleSiat\n" +
        "  AND i.id NOT IN (SELECT iw.invoice_id \n" +
        "                   FROM public.invoice_wrapper_event iw\n" +
        "                   WHERE i.id = iw.invoice_id)\n" +
        "LIMIT :numberInvoice ", nativeQuery = true)
    List findPackInvoicePending(@Param("companyId") Long companyId,
                                @Param("branchOfficeSiat") Integer branchOfficeSiat,
                                @Param("pointSaleSiat") Integer pointSaleSiat,
                                @Param("sectorDocumentType") Integer sectorDocumentType,
                                @Param("cufd") String cufd,
                                @Param("broadcastType") Integer broadcastType,
                                @Param("numberInvoice") Integer numberInvoice);

    @Query(value = "SELECT \n" +
        "  MAX(i.broadcast_date) \n" +
        "FROM public.invoice i\n" +
        "INNER JOIN public.cufd d ON i.cufd_id = d.id\n" +
        "INNER JOIN public.cuis s ON d.cuis_id = s.id\n" +
        "INNER JOIN public.point_sale p ON s.point_sale_id = p.id\n" +
        "INNER JOIN public.branch_office b ON p.branch_office_id = b.id\n" +
        "INNER JOIN public.company c ON b.company_id = c.id\n" +
        "INNER JOIN public.broadcast_type bt ON i.broadcast_type_id = bt.id\n" +
        "WHERE\n" +
        "  b.active = TRUE\n" +
        "  AND p.active = TRUE\n" +
        "  AND s.active = TRUE\n" +
        "  AND bt.active = TRUE\n" +
        "  AND c.active = TRUE\n" +
        "  AND d.cufd = :cufd\n" +
        "  AND bt.siat_id = :broadcastType\n" +
        "  AND i.status = 'PENDING'\n" +
        "  AND c.id = :companyId\n" +
        "  AND b.branch_office_siat_id = :branchOfficeSiat\n" +
        "  AND p.point_sale_siat_id = :pointSaleSiat", nativeQuery = true)
    Timestamp findEndDatePackInvoicePending(@Param("companyId") Long companyId,
                                            @Param("branchOfficeSiat") Integer branchOfficeSiat,
                                            @Param("pointSaleSiat") Integer pointSaleSiat,
                                            @Param("cufd") String cufd,
                                            @Param("broadcastType") Integer broadcastType);


    Optional<Invoice> findByCuf(String cuf);


    @Query(value = "SELECT MAX(i.id) FROM Invoice i")
    long getMaxId();


}
