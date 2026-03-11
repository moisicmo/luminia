package bo.com.luminia.sflbilling.msreport.web.rest;

import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.service.PdfGeneratorService;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.PdfInvoiceCouldBeGeneratedException;
import bo.com.luminia.sflbilling.msreport.web.rest.response.PdfInvoiceResponse;
import bo.com.luminia.sflbilling.msreport.web.rest.util.ResponseMessages;
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

import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_INTEGRATION +"')")
public class PdfController {

    private final PdfGeneratorService generatorService;

    @GetMapping("/{cuf}/pdf/legal")
    public ResponseEntity<byte[]> generatePdfa4(@PathVariable(name = "cuf") String cuf) {
        byte[] bytes = new byte[0];
        try {
            bytes = generatorService.exportReportPdf(cuf, PdfInvoice.PAPER_A4);
        } catch (PdfInvoiceCouldBeGeneratedException e) {
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_FACTURA_GENERARSE);
            //return  ResponseEntity.badRequest().body( null) ;
        }

        if (bytes == null) {
            // TODO hacer un mejor response en caso de exception
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf; charset=UTF-8")
            .header("Content-Disposition", "inline; filename=\"filename.pdf\"")
            .body(bytes);
    }

    @GetMapping("/{cuf}/pdf/roll")
    public ResponseEntity<byte[]> generatePdfForTest(@PathVariable(name = "cuf") String cuf) {
        byte[] bytes = new byte[0];
        try {
            bytes = generatorService.exportReportPdf(cuf, PdfInvoice.PAPER_ROLL);
        } catch (PdfInvoiceCouldBeGeneratedException e) {
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_FACTURA_GENERARSE);
            //return  ResponseEntity.badRequest().body( null) ;
        }

        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf; charset=UTF-8")
            .header("Content-Disposition", "inline; filename=\"filename.pdf\"")
            .body(bytes);
    }

    @GetMapping("/{cuf}/base64/legal")
    public ResponseEntity<PdfInvoiceResponse> generateA4(@PathVariable(name = "cuf") String cuf) {
        byte[] bytes = new byte[0];
        PdfInvoiceResponse response = new PdfInvoiceResponse();

        try {
            bytes = generatorService.exportReportPdf(cuf, PdfInvoice.PAPER_A4);
        } catch (PdfInvoiceCouldBeGeneratedException e) {
            e.printStackTrace();
            //response.setCode(500); //TODO errores
            //response.setMessage("La factura no ha podido generarse");
            //response.setBody(e.getMessage());
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_FACTURA_GENERARSE);
            //return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (bytes == null) {
            response.setCode(500); //TODO errores
            response.setMessage("La factura no ha podido generarse");
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_FACTURA_GENERARSE);
        } else {
            response.setCode(200);
            response.setMessage("Factura Generada Exitosamente");
            response.setBody(Base64.getEncoder().encodeToString(bytes));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{cuf}/base64/roll")
    public ResponseEntity<PdfInvoiceResponse> generateRoll(@PathVariable(name = "cuf") String cuf) {
        byte[] bytes = new byte[0];
        PdfInvoiceResponse response = new PdfInvoiceResponse();

        try {
            bytes = generatorService.exportReportPdf(cuf, PdfInvoice.PAPER_ROLL);
        } catch (PdfInvoiceCouldBeGeneratedException e) {
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_FACTURA_GENERARSE);
            //response.setCode(500); //TODO errores
            //response.setMessage("La factura no ha podido generarse");
            //response.setBody(e.getMessage());
            //return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (bytes == null) {
            //response.setCode(500); //TODO errores
            //response.setMessage("La factura no ha podido generarse");
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_FACTURA_GENERARSE);
        } else {
            response.setCode(200);
            response.setMessage("Factura Generada Exitosamente");
            response.setBody(Base64.getEncoder().encodeToString(bytes));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
