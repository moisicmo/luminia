package bo.com.luminia.sflbilling.msreport.web.rest;

import bo.com.luminia.sflbilling.msreport.web.rest.request.*;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.msreport.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msreport.siat.pdf.service.InvoiceReporterService;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msreport.web.rest.request.*;
import bo.com.luminia.sflbilling.msreport.web.rest.response.ReportResponse;
import bo.com.luminia.sflbilling.msreport.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
public class InvoiceReporterController {

    private final CompanyRepository companyRepository;
    private final InvoiceReporterService service;

    @PostMapping("/invoices")
    public ResponseEntity<ReportResponse> findInvoices(@RequestBody InvoiceRequest request) {

        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(request.getBusinessCode());
        if (!companyFromDb.isPresent()) {
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getBusinessCode()));
        } else {
            Company company = companyFromDb.get();
            request.setCompanyId(company.getId());
            ReportResponse body = service.findInvoices(request);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
    }

    @PostMapping("/query-invoices")
    public ResponseEntity<ReportResponse> findQueryInvoices(@RequestBody QueryRequest request) {
        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(request.getBusinessCode());
        if (!companyFromDb.isPresent()) {
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getBusinessCode()));
        } else {
            Company company = companyFromDb.get();
            request.setCompanyId(company.getId());
            ReportResponse body = service.findQueryInvoices(request);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
    }

    @GetMapping("/{cuf}/invoices")
    public ResponseEntity<ReportResponse> findInvoice(@PathVariable String cuf) {
        ReportResponse body = service.findInvoice(cuf);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{cuf}/data")
    public ResponseEntity<ReportResponse> findInvoiceData(@PathVariable String cuf) {
        ReportResponse body = service.findInvoiceData(cuf);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/massive/reception-codes")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<ReportResponse> findBatch(@Valid @RequestBody BatchReportReq request) {
        ReportResponse body = service.findBatch(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/massive/validations")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<ReportResponse> findAllInvoiceBatch(@Valid @RequestBody InvoiceBatchReportReq request) {
        ReportResponse body = service.findAllInvoiceBatch(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/query-search-invoices")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<ReportResponse> querySearchInvoice(@RequestBody SearchInvoiceRequest request) {
        ReportResponse body = service.querySearchInvoice(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/resume-invoices")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<ReportResponse> resumeInvoices(@RequestBody ResumeInvoiceRequest request) {
        ReportResponse body = service.resumeInvoices(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
