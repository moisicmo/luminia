package bo.com.luminia.sflbilling.msreport.web.rest;

import bo.com.luminia.sflbilling.msreport.siat.text.TextInvoice;
import bo.com.luminia.sflbilling.msreport.siat.text.service.TextGeneratorService;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.TextInvoiceCouldBeGeneratedException;
import bo.com.luminia.sflbilling.msreport.web.rest.response.TextInvoiceResponse;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_INTEGRATION +"')")
public class TextController {

    private final TextGeneratorService service;
    private Object ResponseEntity;

    @GetMapping("/{cuf}/text/legal")
    public ResponseEntity<TextInvoiceResponse> generateTextA4(@PathVariable(name = "cuf") String cuf) {

        try {
            return new ResponseEntity<>(service.exportReportText(cuf, TextInvoice.PAPER_A4), HttpStatus.OK);
        } catch (TextInvoiceCouldBeGeneratedException e) {
            e.printStackTrace();
            throw new TextInvoiceCouldBeGeneratedException(e.getMessage());
//            TextInvoiceResponse response = new TextInvoiceResponse();
//            response.setCode(ResponseCodes.ERROR);
//            response.setMessage(e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{cuf}/text/roll")
    public ResponseEntity<TextInvoiceResponse> generateTextRoll(@PathVariable(name = "cuf") String cuf) {

        try {
            return new ResponseEntity<>(service.exportReportText(cuf, TextInvoice.PAPER_ROLL), HttpStatus.OK);
        } catch (TextInvoiceCouldBeGeneratedException e) {
            e.printStackTrace();
            throw new TextInvoiceCouldBeGeneratedException(e.getMessage());

            //TextInvoiceResponse response = new TextInvoiceResponse();
            //response.setCode(ResponseCodes.ERROR);
            //response.setMessage(e.getMessage());
            //return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
