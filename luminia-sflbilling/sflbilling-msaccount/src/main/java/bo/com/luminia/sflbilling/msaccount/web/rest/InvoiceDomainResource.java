package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.*;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DomainRes;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.msaccount.service.sfe.*;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/companies")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
public class InvoiceDomainResource {

    private final ActivityService activityService;
    private final SectorDocumentTypeService sectorDocumentTypeService;
    private final IdentityDocumentTypeService identityDocumentTypeService;
    private final PaymentMethodTypeService paymentMethodTypeService;
    private final CurrencyTypeService currencyTypeService;
    private final RoomTypeService roomTypeService;
    private final OriginCountryService originCountryService;
    private final SignificantEventService significantEventService;
    private final CancellationReasonService cancellationReasonService;
    private final PointSaleTypeService pointSaleTypeService;
    private final MeasurementUnitService measurementUnitService;
    private final ProductServiceService productServiceService;
    private final InvoiceLegendService invoiceLegendService;

    private final CompanyRepository companyRepository;

    @GetMapping("/{businessCode}/activities")
    public ResponseEntity<DomainRes> getActivity(@PathVariable String businessCode) {
        log.debug("REST request to get Activity : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(activityService.getActivity(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/sector-document-types")
    public ResponseEntity<DomainRes> getSectorDocumentType(@PathVariable String businessCode) {
        log.debug("REST request to get Sector Document Type : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(sectorDocumentTypeService.getSectorDocumentType(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/identity-document-types")
    public ResponseEntity<DomainRes> getIdentityDocumentType(@PathVariable String businessCode) {
        log.debug("REST request to get Identity Document Type: {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(identityDocumentTypeService.getIdentityDocumentType(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/payment-method-types")
    public ResponseEntity<DomainRes> getPaymentMethodType(@PathVariable String businessCode) {
        log.debug("REST request to get Payment Method Type : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(paymentMethodTypeService.getPaymentMethodType(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/currency-types")
    public ResponseEntity<DomainRes> getCurrencyType(@PathVariable String businessCode) {
        log.debug("REST request to get Currency Type : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(currencyTypeService.getCurrencyType(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/room-types")
    public ResponseEntity<DomainRes> getRoomType(@PathVariable String businessCode) {
        log.debug("REST request to get Room Type : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(roomTypeService.getRoomType(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/origin-countries")
    public ResponseEntity<DomainRes> getOriginCountry(@PathVariable String businessCode) {
        log.debug("REST request to get Origin Country : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(originCountryService.getOriginCountry(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/significant-events")
    public ResponseEntity<DomainRes> getSignificantEvent(@PathVariable String businessCode) {
        log.debug("REST request to get Signigicant Event : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(significantEventService.getSignificantEvent(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/cancellation-reasons")
    public ResponseEntity<DomainRes> getCancellationReason(@PathVariable String businessCode) {
        log.debug("REST request to get Cancellation Reason : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(cancellationReasonService.getCancellationReason(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/point-sale-types")
    public ResponseEntity<DomainRes> getPointSaleType(@PathVariable String businessCode) {
        log.debug("REST request to get Point Sale Type : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(pointSaleTypeService.getPointSaleType(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/measurement-units")
    public ResponseEntity<DomainRes> getMeasurementUnit(@PathVariable String businessCode) {
        log.debug("REST request to get Measurement Unit : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(measurementUnitService.getMeasurementUnit(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/product-services")
    public ResponseEntity<DomainRes> getProductService(@PathVariable String businessCode) {
        log.debug("REST request to get Product Service : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(productServiceService.getProductService(company.get().getId().intValue()), HttpStatus.OK);
    }

    @GetMapping("/{businessCode}/invoice-legends")
    public ResponseEntity<DomainRes> getInvoiceLegend(@PathVariable String businessCode, @RequestParam("activityCode") Integer activityCode) {
        log.debug("REST request to get Invoice Legend : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(invoiceLegendService.getInvoiceLegend(company.get().getId().intValue(), activityCode), HttpStatus.OK);
    }
}
