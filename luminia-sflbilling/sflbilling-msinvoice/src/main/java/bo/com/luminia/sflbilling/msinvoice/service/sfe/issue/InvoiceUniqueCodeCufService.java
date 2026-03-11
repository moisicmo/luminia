package bo.com.luminia.sflbilling.msinvoice.service.sfe.issue;

import bo.com.luminia.sflbilling.msinvoice.service.dto.siat.InvoiceUniqueCodeGeneratorCufDto;
import bo.com.luminia.sflbilling.msinvoice.service.utils.InvoiceUniqueCodeConverter;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.CufdNotFoundException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.InvoiceUniqueCodeGeneratorCufReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.InvoiceUniqueCodeGeneratorCufRes;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class InvoiceUniqueCodeCufService {

    private final InvoiceUniqueCodeConverter invoiceUniqueCodeConverter = new InvoiceUniqueCodeConverter();

    public Optional<InvoiceUniqueCodeGeneratorCufRes> generateCuf(InvoiceUniqueCodeGeneratorCufReq request) {
        //paso 1
        InvoiceUniqueCodeGeneratorCufDto invoiceData = invoiceUniqueCodeConverter.convert(request);
        //Campos concatenados
        try {
            //paso 2
            String invoiceConcat = invoiceUniqueCodeConverter.concat(invoiceData);
            //paso 3
            String invoiceModule11 = invoiceUniqueCodeConverter.applyModule11(invoiceConcat);
            //paso 4
            String invoiceBase16 = invoiceUniqueCodeConverter.applyBase16(invoiceModule11);
            //paso 5
            String invoiceBase16Cuf = invoiceBase16 + request.getControlCode();

            InvoiceUniqueCodeGeneratorCufRes result = new InvoiceUniqueCodeGeneratorCufRes();
            result.setCuf(invoiceBase16Cuf);

            return Optional.of(result);
        } catch (Exception e) {
            throw new CufdNotFoundException();
            //log.error("Error en generateCuf", e);
            //return Optional.of(null);
        }
    }

}
