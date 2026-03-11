package bo.com.luminia.sflbilling.msaccount.web.rest.admin;

import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.repository.MeasurementUnitRepository;
import bo.com.luminia.sflbilling.msaccount.repository.ProductServiceRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.ApprovedProductsService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.ApprovedProductReq;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.domain.ProductService;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.PaginationUtil;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
public class AdminApprovedProductsResource {

    private final ApprovedProductsService service;
    private final CompanyRepository companyRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final ProductServiceRepository productServiceRepository;

    @PostMapping("/approved-products")
    public ResponseEntity<ApprovedProductReq> create(@Valid @RequestBody ApprovedProductReq request) throws URISyntaxException {
        log.debug("REST request to create Approved Product : {}", request);
        Company company = companyRepository.findByIdAndActiveTrue(request.getCompanyId())
            .orElseThrow(() -> new DefaultTransactionException(String.format(ResponseMessages.ERROR_EMPRESA_NO_ENCONTRADA,
                request.getCompanyId()), "approvedProducts", ErrorKeys.ERR_RECORD_NOT_FOUND));
        ProductService productService = productServiceRepository.findById(request.getProductServiceId())
            .orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductServiceId()),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        MeasurementUnit measurementUnit = measurementUnitRepository.findById(request.getMeasurementUnitId())
            .orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnitId()),
                ErrorEntities.MEASUREMENT_UNIT,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        ApprovedProductReq result = service.save(request, company, productService, measurementUnit);
        return ResponseEntity
            .created(new URI("/api/admin/approved-products/" + result.getId()))
            .body(result);
    }

    @PutMapping("/approved-products")
    public ResponseEntity<ApprovedProductReq> update(@Valid @RequestBody ApprovedProductReq request) {
        log.debug("REST request to update Approved Product : {}", request);
        Company company = companyRepository.findByIdAndActiveTrue(request.getCompanyId())
            .orElseThrow(() -> new DefaultTransactionException(String.format(ResponseMessages.ERROR_EMPRESA_NO_ENCONTRADA,
                request.getCompanyId()), "approvedProducts", ErrorKeys.ERR_RECORD_NOT_FOUND));
        ProductService productService = productServiceRepository.findById(request.getProductServiceId())
            .orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductServiceId()),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        MeasurementUnit measurementUnit = measurementUnitRepository.findById(request.getMeasurementUnitId())
            .orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnitId()),
                ErrorEntities.MEASUREMENT_UNIT,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        return ResponseUtil.wrapOrNotFound(service.partialUpdate(request, company, productService, measurementUnit));
    }

    @GetMapping("/approved-products")
    public ResponseEntity<List<ApprovedProductReq>> getAllCompanies(Pageable pageable) {
        log.debug("REST request to get a page of Approved Product");
        Page<ApprovedProductReq> page = service.findAll(pageable);
        HttpHeaders headers = PaginationUtil.addTotalCountHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/approved-products/{id}")
    public ResponseEntity<ApprovedProductReq> getCompany(@PathVariable Long id) {
        log.debug("REST request to get Approved Product : {}", id);
        Optional<ApprovedProductReq> approvedProductReq = service.findOne(id);
        return ResponseUtil.wrapOrNotFound(approvedProductReq);
    }

    @DeleteMapping("/approved-products/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Approved Product : {}", id);
        if (!productServiceRepository.existsById(id)) throw new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        service.deleteApprovedProduct(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
