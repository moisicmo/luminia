package bo.com.luminia.sflbilling.msreport.web.rest;

import bo.com.luminia.sflbilling.msreport.siat.pdf.service.WrapperReporterService;
import bo.com.luminia.sflbilling.msreport.web.rest.request.WrapperReportReq;
import bo.com.luminia.sflbilling.msreport.web.rest.response.ReportResponse;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_ADMIN +"')")
public class WrapperReporterController {

    private final WrapperReporterService wrapperReporterService;

    @PostMapping("/wrappers")
    public ResponseEntity<ReportResponse> findWrappers(@Valid @RequestBody WrapperReportReq request) {
        ReportResponse body = wrapperReporterService.findWrappers(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/wrappers-events")
    public ResponseEntity<ReportResponse> findWrapperEvents(@Valid @RequestBody WrapperReportReq request) {
        ReportResponse body = wrapperReporterService.findWrapperEvents(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
