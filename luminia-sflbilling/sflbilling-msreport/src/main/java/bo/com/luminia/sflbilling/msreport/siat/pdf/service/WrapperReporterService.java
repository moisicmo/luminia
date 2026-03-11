package bo.com.luminia.sflbilling.msreport.siat.pdf.service;

import bo.com.luminia.sflbilling.msreport.repository.WrapperEventRepository;
import bo.com.luminia.sflbilling.msreport.repository.WrapperRepository;
import bo.com.luminia.sflbilling.msreport.web.rest.request.WrapperReportReq;
import bo.com.luminia.sflbilling.msreport.web.rest.response.ReportResponse;
import bo.com.luminia.sflbilling.msreport.web.rest.response.WrapperEventRes;
import bo.com.luminia.sflbilling.msreport.web.rest.response.WrapperRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class WrapperReporterService {

    private final WrapperRepository wrapperRepository;
    private final WrapperEventRepository wrapperEventRepository;

    public ReportResponse findWrappers(WrapperReportReq request) {
        ReportResponse response = new ReportResponse();

        List wrapperList = wrapperRepository.findFullByCompanyId(request.getCompanyId(), this.getTimestamp(request.getStartDate()), this.getTimestamp(request.getEndDate()));

        // Mapea la lista de resultados.
        List<WrapperRes> listInvoices = ((ArrayList<Object[]>) wrapperList).stream().map(x -> {
            WrapperRes invoice = new WrapperRes();

            invoice.setId(((BigInteger) x[0]).longValue());
            invoice.setCufDEvent(x[1].toString());
            invoice.setStartDate(x[2].toString());
            invoice.setEndDate(x[3].toString());
            invoice.setReceptionCode(x[4].toString());
            invoice.setStatus(x[5].toString());
            invoice.setCompanyName(x[6].toString());
            invoice.setCompanyId(((BigInteger) x[7]).longValue());
            invoice.setBranchOfficeName(x[8].toString());
            invoice.setBranchOfficeId(((BigInteger) x[9]).longValue());
            invoice.setPointSaleName(x[10].toString());
            invoice.setPointSaleId(((BigInteger) x[11]).longValue());
            invoice.setSectorDocumentTypeName(x[12].toString());
            invoice.setSectorDocumentTypeId(((BigInteger) x[13]).longValue());

            return invoice;
        }).collect(Collectors.toList());

        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(listInvoices);

        return response;
    }

    public ReportResponse findWrapperEvents(WrapperReportReq request) {
        ReportResponse response = new ReportResponse();

        List wrapperList = wrapperEventRepository.findFullByCompanyId(request.getCompanyId(), this.getTimestamp(request.getStartDate()), this.getTimestamp(request.getEndDate()));

        // Mapea la lista de resultados.
        List<WrapperEventRes> listInvoices = ((ArrayList<Object[]>) wrapperList).stream().map(x -> {
            WrapperEventRes invoice = new WrapperEventRes();

            invoice.setId(((BigInteger) x[0]).longValue());
            invoice.setCufDEvent(x[1].toString());
            invoice.setStartDate(x[2].toString());
            invoice.setEndDate(x[3].toString());
            invoice.setReceptionCode(x[4].toString());
            invoice.setStatus(x[5].toString());
            invoice.setCompanyName(x[6].toString());
            invoice.setCompanyId(((BigInteger) x[7]).longValue());
            invoice.setBranchOfficeName(x[8].toString());
            invoice.setBranchOfficeId(((BigInteger) x[9]).longValue());
            invoice.setPointSaleName(x[10].toString());
            invoice.setPointSaleId(((BigInteger) x[11]).longValue());
            invoice.setSignificantEventName(x[12].toString());
            invoice.setSignificantEventDescription(x[13].toString());
            invoice.setSignificantEventId(((BigInteger) x[14]).longValue());
            invoice.setSectorDocumentTypeName(x[15].toString());
            invoice.setSectorDocumentTypeId(((BigInteger) x[16]).longValue());

            return invoice;
        }).collect(Collectors.toList());

        response.setCode(200);
        response.setMessage("Operacion realizada con exito.");
        response.setBody(listInvoices);

        return response;
    }

    private Timestamp getTimestamp(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(date));
        Timestamp ts = Timestamp.valueOf(localDateTime);
        return ts;
    }
}
