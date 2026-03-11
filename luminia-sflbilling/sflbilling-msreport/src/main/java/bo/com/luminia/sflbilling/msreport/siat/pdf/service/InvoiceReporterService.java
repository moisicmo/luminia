package bo.com.luminia.sflbilling.msreport.siat.pdf.service;

import bo.com.luminia.sflbilling.msreport.repository.*;
import bo.com.luminia.sflbilling.msreport.web.rest.request.*;
import bo.com.luminia.sflbilling.msreport.web.rest.response.*;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.Invoice;
import bo.com.luminia.sflbilling.domain.User;
import bo.com.luminia.sflbilling.msreport.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msreport.repository.*;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceBuilder;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.DataInvoiceCouldBeGeneratedException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.TextInvoiceCouldBeGeneratedException;
import bo.com.luminia.sflbilling.msreport.web.rest.request.*;
import bo.com.luminia.sflbilling.msreport.web.rest.response.*;
import bo.com.luminia.sflbilling.msreport.web.rest.response.data.CompraVentaDetail;
import bo.com.luminia.sflbilling.msreport.web.rest.response.data.CompraVentaHeader;
import bo.com.luminia.sflbilling.msreport.web.rest.response.data.CompraVentaInvoice;
import bo.com.luminia.sflbilling.msreport.web.rest.response.data.SearchInvoiceData;
import bo.com.luminia.sflbilling.msreport.web.rest.util.ResponseCodes;
import bo.com.luminia.sflbilling.msreport.web.rest.util.ResponseMessages;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlCompraVentaDetail;
import bo.gob.impuestos.sfe.invoice.xml.eltr.XmlCompraVentaEltrInvoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceReporterService {

    private static final Integer FUERA_LINEA = 2;
    private static final String LEYENDA1 = "Esta factura contribuye al desarrollo del país, el uso ilícito será sancionado penalmente de acuerdo a Ley";
    private static final String TIPO = "2";

    private final ApplicationProperties properties;
    private final EntityManager em;
    private final InvoiceRepository repository;
    private final BatchRepository batchRepository;
    private final InvoiceBatchRepository invoiceBatchRepository;
    private final IdentityDocumentTypeRepository identityDocumentTypeRepository;
    private final PaymentMethodTypeRepository paymentMethodTypeRepository;
    private final CurrencyTypeRepository currencyTypeRepository;
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;

    public ReportResponse findInvoices(InvoiceRequest request) {
        ReportResponse response = new ReportResponse();
        List<InvoiceResponse> body = new ArrayList<>();

        Long companyId = request.getCompanyId();
        Integer pointOfSale = request.getPointSale();
        Integer branchOffice = request.getBranchOffice();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        String status = request.getStatus();
        String nit = request.getNit();
        String businessName = request.getBusinessName();

        if (companyId == null) {
            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage("El campo <companyId> es requerido.");
            return response;
        }
        Integer index = 1;
        String sql = "select i.* from invoice i  " +
            "inner join cufd c on c.id = i.cufd_id " +
            "inner join cuis cs on cs.id = c.cuis_id " +
            "inner join point_sale p on p.id = cs.point_sale_id " +
            "inner join branch_office bo on bo.id = p.branch_office_id " +
            "inner join company cp on cp.id = bo.company_id " +
            "where cp.id = ?" + index;

        index++;

        if (pointOfSale != null) {
            sql += " and p.point_sale_siat_id = ?" + index;
            index++;
        }

        if (branchOffice != null) {
            sql += " and bo.branch_office_siat_id= ?" + index;
            index++;
        }

        if (startDate != null && endDate != null) {
            sql += " and i.broadcast_date >= ?" + index;
            index++;
        }

        if (endDate != null) {
            sql += " and i.broadcast_date <=?" + index;
            index++;
        }

        if (status != null) {
            sql += " and i.status = ?" + index;
            index++;
        }

        if (nit != null) {
            sql += " and i.nit = ?" + index;
            index++;
        }

        if (businessName != null) {
            sql += " and i.business_name = ?" + index;
            index++;
        }

        Query query = em.createNativeQuery(sql, Invoice.class);

        index = 1;
        //query.setParameter("companyId", companyId);
        query.setParameter(index, companyId);

        if (pointOfSale != null) {
            //query.setParameter("pointSale", pointOfSale);\
            index++;
            query.setParameter(index, pointOfSale);
        }

        if (branchOffice != null) {
            index++;
            //query.setParameter("branchOffice", branchOffice);
            query.setParameter(index, branchOffice);
        }

        if (startDate != null && endDate != null) {
            //query.setParameter("startDate" , getTimestamp(startDate));
            index++;
            query.setParameter(index, getTimestampMin(startDate));
        }
        if (endDate != null) {
            index++;
            //query.setParameter("endDate", getTimestamp(endDate));
            query.setParameter(index, getTimestampMax(endDate));
        }

        if (status != null) {
            //query.setParameter("status", InvoiceStatusEnum.valueOf(status));
            index++;
            query.setParameter(index, (status));
        }

        if (nit != null) {
            //query.setParameter("nit", nit);
            index++;
            query.setParameter(index, nit);
        }

        if (businessName != null) {
            //query.setParameter("businessName", businessName);
            index++;
            query.setParameter(index, businessName);
        }

        List<Invoice> result = query.getResultList();

        body = populateReport(result);

        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(body);

        return response;
    }

    public ReportResponse findQueryInvoices(QueryRequest request) {
        ReportResponse response = new ReportResponse();

        Long companyId = request.getCompanyId();
        Integer pointOfSale = request.getPointSale();
        Integer branchOffice = request.getBranchOffice();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        String status = request.getStatus();
        Integer offset = request.getOffset();
        Integer limit = request.getLimit();
        Integer invoiceNumber = request.getInvoiceNumber();

        if (offset == null || limit == null) {
            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage("Los campos <offset>, <limit> son requeridos.");
            return response;
        }

        Integer index = 1;
        String sql = "select i.* from invoice i " +
            "inner join cufd c on c.id = i.cufd_id " +
            "inner join cuis cs on cs.id = c.cuis_id " +
            "inner join point_sale p on p.id = cs.point_sale_id " +
            "inner join branch_office bo on bo.id = p.branch_office_id " +
            "inner join company cp on cp.id = bo.company_id " +
            "where cp.id = ?" + index;
        index++;

        if (pointOfSale != null) {
            sql += " and p.point_sale_siat_id = ?" + index;
            index++;
        }

        if (branchOffice != null) {
            sql += " and bo.branch_office_siat_id= ?" + index;
            index++;
        }

        if (startDate != null && endDate != null) {
            sql += " and i.broadcast_date >= ?" + index;
            index++;
        }

        if (endDate != null) {
            sql += " and i.broadcast_date <=?" + index;
            index++;
        }

        if (status != null) {
            sql += " and i.status = ?" + index;
            index++;
        }

        if (invoiceNumber != null) {
            sql += " and i.invoice_number= ?" + index;
            index++;
        }

        sql += " order by i.broadcast_date DESC ";

        sql += " limit ?" + index;
        index++;

        sql += " offset ?" + index;
        index++;

        Query query = em.createNativeQuery(sql, Invoice.class);

        index = 1;
        query.setParameter(index, companyId);

        if (pointOfSale != null) {
            index++;
            query.setParameter(index, pointOfSale);
        }

        if (branchOffice != null) {
            index++;
            query.setParameter(index, branchOffice);
        }

        if (startDate != null && endDate != null) {
            index++;
            query.setParameter(index, getTimestampMin(startDate));
        }
        if (endDate != null) {
            index++;
            query.setParameter(index, getTimestampMax(endDate));
        }

        if (status != null) {
            index++;
            query.setParameter(index, (status));
        }

        if (invoiceNumber != null) {
            index++;
            query.setParameter(index, invoiceNumber);
        }

        index++;
        query.setParameter(index, limit);

        index++;
        query.setParameter(index, offset);

        List<Invoice> result = query.getResultList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss").withZone(ZoneId.systemDefault());
        // Mapea la lista de resultados.
        List<InvoiceResponse> listInvoices = result.stream().map(i -> {
            InvoiceResponse invoice = new InvoiceResponse();
            invoice.setInvoiceNumber(i.getInvoiceNumber());
            invoice.setCuf(i.getCuf());
            invoice.setBroadcastDate(formatter.format(i.getBroadcastDate()));
            invoice.setInvoiceHash(i.getInvoiceHash());
            invoice.setReceptionCode(i.getReceptionCode());
            invoice.setStatus(i.getStatus().name());
            invoice.setCufd(i.getCufd().getCufd());
            invoice.setSectorDocumentTypeId(i.getSectorDocumentType().getSiatId());
            invoice.setSectorDocumentTypeDescription(i.getSectorDocumentType().getDescription());
            invoice.setBroadcastType(i.getBroadcastType().getSiatId());
            invoice.setBroadcastTypeDescription(i.getBroadcastType().getDescription());
            invoice.setInvoiceType(i.getInvoiceType().getSiatId());
            invoice.setInvoiceTypeDescription(i.getInvoiceType().getDescription());
            return invoice;
        }).collect(Collectors.toList());

        QueryReportResponse queryReportResponse = new QueryReportResponse();
        queryReportResponse.setInvoices(listInvoices);
        queryReportResponse.setTotal(this.findTotalQueryInvoices(request));

        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(queryReportResponse);
        return response;
    }

    public ReportResponse findInvoice(String cuf) {
        ReportResponse response = new ReportResponse();
        Optional<Invoice> fromDb = repository.findInvoiceByCuf(cuf);
        if (fromDb.isPresent()) {
            InvoiceResponse invoice = new InvoiceResponse();
            BeanUtils.copyProperties(fromDb.get(), invoice);
            invoice = copy(fromDb.get(), invoice);
            response.setBody(invoice);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setCode(ResponseCodes.SUCCESS);
        } else {
            response.setCode(ResponseCodes.ERROR);
            response.setMessage(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        }
        return response;
    }

    public ReportResponse findInvoiceData(String cuf) {
        ReportResponse response = new ReportResponse();
        Optional<Invoice> data = repository.findInvoiceByCuf(cuf);
        try {
            if (data.isPresent()) {
                Invoice invoice = data.get();
                Integer companyId = invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId().intValue();
                if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
                    XmlCompraVentaEltrInvoice baseInvoice = (XmlCompraVentaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlCompraVentaEltrInvoice.class);
                    CompraVentaInvoice invoiceResponse = new CompraVentaInvoice();

                    CompraVentaHeader header = new CompraVentaHeader();
                    BeanUtils.copyProperties(baseInvoice.getCabecera(), header);
                    header.setCodigoTipoDocumentoIdentidadDescripcion(identityDocumentTypeRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId, baseInvoice.getCabecera().getCodigoTipoDocumentoIdentidad()).get().getDescription());
                    header.setCodigoMetodoPagoDescripcion(paymentMethodTypeRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId, baseInvoice.getCabecera().getCodigoMetodoPago()).get().getDescription());
                    header.setCodigoMonedaDescripcion(currencyTypeRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId, baseInvoice.getCabecera().getCodigoMoneda()).get().getDescription());
                    header.setLeyenda1(LEYENDA1);
                    header.setLeyenda2(baseInvoice.getCabecera().getLeyenda());
                    header.setLeyenda3(this.getLeyendaModalidad(invoice.getBroadcastType().getSiatId()));
                    header.setTipoEmision(invoice.getBroadcastType().getSiatId());
                    header.setTipoEmisionDescripcion(invoice.getBroadcastType().getDescription());
                    header.setCodigoDocumentoSectorDescripcion(sectorDocumentTypeRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId, baseInvoice.getCabecera().getCodigoDocumentoSector()).get().getDescription());
                    header.setTipoDocumentoFiscal(invoice.getInvoiceType().getSiatId());
                    header.setTipoDocumentoFiscalDescripcion(invoice.getInvoiceType().getDescription());
                    header.setQr(String.format(properties.getSiatUrl(), header.getNitEmisor(), header.getCuf(), header.getNumeroFactura(), TIPO));
                    invoiceResponse.setCabecera(header);

                    List<CompraVentaDetail> details = new ArrayList<>();
                    for (XmlCompraVentaDetail entity : baseInvoice.getDetalle()) {
                        CompraVentaDetail detail = new CompraVentaDetail();
                        BeanUtils.copyProperties(entity, detail);
                        detail.setUnidadMedidaDescripcion(measurementUnitRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId, entity.getUnidadMedida()).get().getDescription());
                        details.add(detail);
                    }
                    invoiceResponse.setDetalle(details);
                    response.setBody(invoiceResponse);
                }
                response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
                response.setCode(ResponseCodes.SUCCESS);
            } else {
                response.setCode(ResponseCodes.ERROR);
                response.setMessage(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
            }
        } catch (JsonProcessingException e) {
            log.error(String.format("Error al obtener los datos json %s", cuf), e.getMessage());
            e.printStackTrace();
            throw new DataInvoiceCouldBeGeneratedException(String.format("Error generando los datos %s. Contacte al administrador", cuf));
        }
        return response;
    }

    /**
     * Método que obtiene la lista de códigos de recepción.
     *
     * @param request Solicitud.
     * @return
     */
    public ReportResponse findBatch(BatchReportReq request) {
        ReportResponse response = new ReportResponse();
        List batchList = batchRepository.findByBusinessCode(request.getBusinessCode(), this.getTimestampMin(request.getStartDate()), this.getTimestampMax(request.getEndDate()));

        // Mapea la lista de resultados.
        List<BatchReceptionRes> listBatchs = ((ArrayList<Object[]>) batchList).stream().map(x -> {
            BatchReceptionRes batch = new BatchReceptionRes();
            batch.setReceptionCode(x[0].toString());
            batch.setDate(x[1].toString());
            batch.setStatus(x[2].toString());

            return batch;
        }).collect(Collectors.toList());

        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(listBatchs);
        return response;
    }

    /**
     * Método que obtiene la lista de documentos fiscales.
     *
     * @param request
     * @return
     */
    public ReportResponse findAllInvoiceBatch(InvoiceBatchReportReq request) {
        ReportResponse response = new ReportResponse();
        List batchList = invoiceBatchRepository.findAllByReceptionCode(request.getReceptionCode());

        // Mapea la lista de resultados.
        List<InvoiceBatchReceptionRes> listInvoices = ((ArrayList<Object[]>) batchList).stream().map(x -> {
            InvoiceBatchReceptionRes invoiceBatch = new InvoiceBatchReceptionRes();
            invoiceBatch.setInvoiceNumber(((BigInteger) x[0]).longValue());
            invoiceBatch.setCuf(x[1] != null ? x[1].toString() : null);
            invoiceBatch.setStatus(x[2] != null ? x[2].toString() : null);
            invoiceBatch.setValidationSource(x[3] != null ? x[3].toString() : null);
            invoiceBatch.setResponseMessage(x[4] != null ? x[4].toString() : null);

            return invoiceBatch;
        }).collect(Collectors.toList());

        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(listInvoices);
        return response;
    }

    private List<InvoiceResponse> populateReport(List<Invoice> fromDb) {
        List<InvoiceResponse> response = new ArrayList<>();
        for (Invoice i : fromDb) {
            InvoiceResponse r = new InvoiceResponse();
            BeanUtils.copyProperties(i, r);
            r = copy(i, r);
            r.setInvoiceXml(null);
            response.add(r);
        }
        return response;
    }

    /*private Timestamp getTimestamp(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(date));
        Timestamp ts = Timestamp.valueOf(localDateTime);
        return ts;
    }*/

    private Timestamp getTimestampMin(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy HH:mm:ss.SSSSSS]" + "[dd-MM-yyyy HH:mm:ss[.SSS]]");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        localDateTime = localDateTime.withNano(0);
        Timestamp ts = Timestamp.valueOf(localDateTime);
        return ts;
    }

    private Timestamp getTimestampMax(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy HH:mm:ss.SSSSSS]" + "[dd-MM-yyyy HH:mm:ss[.SSS]]");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        localDateTime = localDateTime.withNano(999999999);
        Timestamp ts = Timestamp.valueOf(localDateTime);
        return ts;
    }

    private InvoiceResponse copy(Invoice i, InvoiceResponse r) {
        r.setReceptionCode(i.getReceptionCode());
        r.setBroadcastType(i.getBroadcastType().getSiatId());
        r.setBroadcastTypeDescription(i.getBroadcastType().getDescription());
        r.setReceptionCode(i.getReceptionCode());
        r.setModalitySiat(i.getModalitySiat().getDescription());
        r.setCufd(i.getCufd().getCufd());
        r.setSectorDocumentTypeId(i.getSectorDocumentType().getSiatId());
        r.setSectorDocumentTypeDescription(i.getSectorDocumentType().getDescription());
        r.setInvoiceType(i.getInvoiceType().getSiatId());
        r.setInvoiceTypeDescription(i.getInvoiceType().getDescription());
        r.setStatus(i.getStatus().name());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(i.getInvoiceJson(), new TypeReference<Map<String, Object>>() {
            });
            r.setInvoice(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TextInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_TEXT_REPORT);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss").withZone(ZoneId.systemDefault());
        r.setBroadcastDate(formatter.format(i.getBroadcastDate()));

        return r;
    }

    private String getLeyendaModalidad(Integer broadcastType) {
        if (broadcastType == FUERA_LINEA) {
            return "Este documento es la Representación Gráfica de un Documento Fiscal Digital emitido fuera de línea, verifique su envío con su proveedor o en la página web www.impuestos.gob.bo";
        } else {
            return "Este documento es la Representación Gráfica de un Documento Fiscal Digital emitido en una modalidad de facturación en línea";
        }
    }

    private Integer findTotalQueryInvoices(QueryRequest request) {
        Long companyId = request.getCompanyId();
        Integer pointOfSale = request.getPointSale();
        Integer branchOffice = request.getBranchOffice();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        String status = request.getStatus();

        Integer index = 1;
        String sql = "select count(1) from invoice i " +
            "inner join cufd c on c.id = i.cufd_id " +
            "inner join cuis cs on cs.id = c.cuis_id " +
            "inner join point_sale p on p.id = cs.point_sale_id " +
            "inner join branch_office bo on bo.id = p.branch_office_id " +
            "inner join company cp on cp.id = bo.company_id " +
            "where cp.id = ?" + index;
        index++;

        if (pointOfSale != null) {
            sql += " and p.point_sale_siat_id = ?" + index;
            index++;
        }

        if (branchOffice != null) {
            sql += " and bo.branch_office_siat_id= ?" + index;
            index++;
        }

        if (startDate != null && endDate != null) {
            sql += " and i.broadcast_date >= ?" + index;
            index++;
        }

        if (endDate != null) {
            sql += " and i.broadcast_date <=?" + index;
            index++;
        }

        if (status != null) {
            sql += " and i.status = ?" + index;
            index++;
        }

        Query query = em.createNativeQuery(sql);

        index = 1;
        query.setParameter(index, companyId);

        if (pointOfSale != null) {
            index++;
            query.setParameter(index, pointOfSale);
        }

        if (branchOffice != null) {
            index++;
            query.setParameter(index, branchOffice);
        }

        if (startDate != null && endDate != null) {
            index++;
            query.setParameter(index, getTimestampMin(startDate));
        }
        if (endDate != null) {
            index++;
            query.setParameter(index, getTimestampMax(endDate));
        }

        if (status != null) {
            index++;
            query.setParameter(index, (status));
        }

        Object result = query.getSingleResult();
        return Integer.parseInt(result.toString());
    }

    public ReportResponse querySearchInvoice(SearchInvoiceRequest request) {
        //Obtengo el usuario principal
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.debug("usuario principal: {}", currentPrincipalName);

        //User user = userRepository.findCurrentUserCompany().orElseThrow(()-> new NotFoundAlertException("Usuario de compañía no econtrado", "Usuario de compañía no econtrado: cuf="+cuf));
        User userCompany = userRepository.findCurrentUserCompany().orElse(null);
        log.debug("usuario de compañía: {}", userCompany);

        //Si el usuario es el admin, lo dejaremos pasar
        if (userCompany == null && !"admin".equals(currentPrincipalName))
            throw new NotFoundAlertException("Usuario de compañía no econtrado", "Usuario de compañía no econtrado");

        //Validación básica para saber si tengo todos los inputs necesarios
        if (request == null ||
            (request.getInvoiceId() == null && Strings.isBlank(request.getDocument()) &&
                request.getCompanyId() == null && Strings.isBlank(request.getBusinessCode()))) {
            throw new RecordNotFoundException(ResponseMessages.ERROR_BAD_INPUT);
        }

        //Busco la compañía
        Optional<Company> company;
        if (request.getCompanyId() != null)
            company = companyRepository.findById(request.getCompanyId());
        else
            company = companyRepository.findByBusinessCode(request.getBusinessCode());
        if (!company.isPresent())
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND,
                    (request.getBusinessCode() == null ? "" : request.getBusinessCode()) + (request.getCompanyId() == null ? "" : request.getCompanyId())
                )
            );

        ReportResponse response = new ReportResponse();

        StringBuilder sql = new StringBuilder();
        sql.append("select i.* ");
        sql.append("from invoice i ");
        sql.append("     inner join cufd c on c.id = i.cufd_id ");
        sql.append("     inner join cuis cs on cs.id = c.cuis_id ");
        sql.append("     inner join point_sale p on p.id = cs.point_sale_id ");
        sql.append("     inner join branch_office bo on bo.id = p.branch_office_id ");
        sql.append("     inner join company cp on cp.id = bo.company_id ");
        sql.append("where i.broadcast_date >= ? and i.broadcast_date <= ? ");
        sql.append("      and cp.id = ? ");
        if (request.getInvoiceId() != null) {
            sql.append(" and i.id = ? ");
        }
        if (request.getInvoiceNumber() != null) {
            sql.append(" and i.invoice_number = ? ");
        }
        if (!Strings.isBlank(request.getDocument())) {
            sql.append(" and i.nit = ? ");
        }
        if (!Strings.isBlank(request.getStatus())) {
            sql.append(" and i.status = ? ");
        }

        sql.append("order by i.broadcast_date ");

        Timestamp startDate = getTimestampMin(request.getStartDate());
        Timestamp endDate = getTimestampMax(request.getEndDate());

        Query query = em.createNativeQuery(sql.toString(), Invoice.class);
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);
        query.setParameter(3, company.get().getId());
        int index = 4;
        if (request.getInvoiceId() != null) {
            query.setParameter(index++, request.getInvoiceId());
        }
        if (request.getInvoiceNumber() != null) {
            query.setParameter(index++, request.getInvoiceNumber());
        }
        if (!Strings.isBlank(request.getDocument())) {
            query.setParameter(index++, request.getDocument());
        }
        if (!Strings.isBlank(request.getStatus())) {
            query.setParameter(index++, request.getStatus());
        }

        query.setFirstResult(request.getOffset());
        query.setMaxResults(request.getLimit());

        List<Invoice> result = query.getResultList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss.SSS").withZone(ZoneId.systemDefault());
        // Mapea la lista de resultados.
        List<SearchInvoiceData> listInvoices = result.stream().map(i -> {
            SearchInvoiceData invoice = new SearchInvoiceData();
            invoice.setId(i.getId());
            invoice.setInvoiceNumber(i.getInvoiceNumber());
            invoice.setCuf(i.getCuf());
            invoice.setBroadcastDate(formatter.format(i.getBroadcastDate()));
            invoice.setModalitySiat(i.getModalitySiat().getDescription());
            invoice.setStatus(i.getStatus().name());
            invoice.setSectorDocumentTypeId(i.getSectorDocumentType().getSiatId());
            invoice.setSectorDocumentTypeDescription(i.getSectorDocumentType().getDescription());
            invoice.setBroadcastType(i.getBroadcastType().getSiatId());
            invoice.setBroadcastTypeDescription(i.getBroadcastType().getDescription());
            invoice.setInvoiceType(i.getInvoiceType().getSiatId());
            invoice.setInvoiceTypeDescription(i.getInvoiceType().getDescription());
            //invoice.setAmount(invoiceRepository.findAmount(i.getId()));

            try {
                Map<String, Object> jsonBody = new ObjectMapper().readValue(i.getInvoiceJson(), HashMap.class);
                LinkedHashMap<String, Object> headerData = (LinkedHashMap<String, Object>) jsonBody.get("cabecera");

                invoice.setAmount(getValueFromDataRequestDouble(headerData, "montoTotal"));
                invoice.setAmountIva(getValueFromDataRequestDouble(headerData, "montoTotalSujetoIva"));
                invoice.setSocialReason(getValueFromDataRequest(headerData, "nombreRazonSocial"));
                invoice.setDocument(getValueFromDataRequest(headerData, "numeroDocumento"));
                invoice.setComplement(getValueFromDataRequest(headerData, "complemento"));
                if ("null".equals(invoice.getComplement()) || invoice.getComplement() == null) {
                    invoice.setComplement("");
                }
            } catch (Exception e) {
            }

            return invoice;
        }).collect(Collectors.toList());

        SearchInvoiceResponse wrapper = new SearchInvoiceResponse();
        wrapper.setInvoices(listInvoices);
        wrapper.setTotal(querySearchInvoiceCount(request, company, startDate, endDate));

        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(wrapper);
        return response;
    }


    private String getValueFromDataRequest(Map<String, Object> data, String property) {
        try {
            return String.valueOf((Integer) data.get(property));
        } catch (Exception e) {
        }
        return (String) data.get(property);
    }

    private Double getValueFromDataRequestDouble(Map<String, Object> data, String property) {
        try {
            return (Double) data.get(property);
        } catch (Exception e) {
        }
        return Double.valueOf((String) data.get(property));
    }


    private Integer querySearchInvoiceCount(SearchInvoiceRequest request, Optional<Company> company, Timestamp startDate, Timestamp endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) ");
        sql.append("from invoice i ");
        sql.append("     inner join cufd c on c.id = i.cufd_id ");
        sql.append("     inner join cuis cs on cs.id = c.cuis_id ");
        sql.append("     inner join point_sale p on p.id = cs.point_sale_id ");
        sql.append("     inner join branch_office bo on bo.id = p.branch_office_id ");
        sql.append("     inner join company cp on cp.id = bo.company_id ");
        sql.append("where i.broadcast_date >= ? and i.broadcast_date <= ? ");
        sql.append("      and cp.id = ? ");
        if (request.getInvoiceId() != null) {
            sql.append(" and i.id = ? ");
        }
        if (request.getInvoiceNumber() != null) {
            sql.append(" and i.invoice_number = ? ");
        }
        if (!Strings.isBlank(request.getDocument())) {
            sql.append(" and i.nit = ? ");
        }

        Query query = em.createNativeQuery(sql.toString());
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);
        query.setParameter(3, company.get().getId());
        int index = 4;
        if (request.getInvoiceId() != null) {
            query.setParameter(index++, request.getInvoiceId());
        }
        if (request.getInvoiceNumber() != null) {
            query.setParameter(index++, request.getInvoiceNumber());
        }
        if (!Strings.isBlank(request.getDocument())) {
            query.setParameter(index++, request.getDocument());
        }

        Object result = query.getSingleResult();
        return Integer.parseInt(result.toString());
    }


    public ReportResponse resumeInvoices(ResumeInvoiceRequest request) {
        //Obtengo el usuario principal
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.debug("usuario principal: {}", currentPrincipalName);

        //User user = userRepository.findCurrentUserCompany().orElseThrow(()-> new NotFoundAlertException("Usuario de compañía no econtrado", "Usuario de compañía no econtrado: cuf="+cuf));
        User userCompany = userRepository.findCurrentUserCompany().orElse(null);
        log.debug("usuario de compañía: {}", userCompany);

        //Si el usuario es el admin, lo dejaremos pasar
        if (userCompany == null && !"admin".equals(currentPrincipalName))
            throw new NotFoundAlertException("Usuario de compañía no econtrado", "Usuario de compañía no econtrado");

        //Validación básica para saber si tengo todos los inputs necesarios
        if (request == null ||
            (request.getCompanyId() == null && Strings.isBlank(request.getBusinessCode()))) {
            throw new RecordNotFoundException(ResponseMessages.ERROR_BAD_INPUT);
        }

        //Busco la compañía
        Optional<Company> company;
        if (request.getCompanyId() != null)
            company = companyRepository.findById(request.getCompanyId());
        else
            company = companyRepository.findByBusinessCode(request.getBusinessCode());
        if (!company.isPresent())
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND,
                    (request.getBusinessCode() == null ? "" : request.getBusinessCode()) + (request.getCompanyId() == null ? "" : request.getCompanyId())
                )
            );

        Timestamp startDate = getTimestampMin(request.getStartDate());
        Timestamp endDate = getTimestampMax(request.getEndDate());

        StringBuilder sql = new StringBuilder();
        sql.append("select  ");
        sql.append("   quantity_emitted,  amount_emitted, ");
        sql.append("   quantity_canceled, amount_canceled, ");
        sql.append("   quantity_reversed, amount_reversed, ");
        sql.append("   quantity_pending,  amount_pending, ");
        sql.append("   quantity_observed, amount_observed  ");
        sql.append("from view_resume_transaction (  ");
        sql.append("'").append(startDate).append("', ");
        sql.append("'").append(endDate).append("', ");
        sql.append(company.get().getId());
        sql.append(");");

        Query query = em.createNativeQuery(sql.toString());
        List<Object[]> resume = query.getResultList();

        ResumeInvoiceResponse data = new ResumeInvoiceResponse();
        data.setQuantityEmitted(((BigInteger) resume.get(0)[0]).longValue());
        data.setAmountEmitted((Double) resume.get(0)[1]);
        data.setQuantityCanceled(((BigInteger) resume.get(0)[2]).longValue());
        data.setAmountCanceled((Double) resume.get(0)[3]);
        data.setQuantityReversed(((BigInteger) resume.get(0)[4]).longValue());
        data.setAmountReversed((Double) resume.get(0)[5]);
        data.setQuantityPending(((BigInteger) resume.get(0)[6]).longValue());
        data.setAmountPending((Double) resume.get(0)[7]);
        data.setQuantityObserved(((BigInteger) resume.get(0)[8]).longValue());
        data.setAmountObserved((Double) resume.get(0)[9]);

        ReportResponse response = new ReportResponse();
        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(data);
        return response;
    }


}
