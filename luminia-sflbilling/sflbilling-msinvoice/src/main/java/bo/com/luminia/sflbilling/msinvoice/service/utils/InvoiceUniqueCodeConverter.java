package bo.com.luminia.sflbilling.msinvoice.service.utils;

import bo.com.luminia.sflbilling.msinvoice.service.dto.siat.InvoiceUniqueCodeGeneratorCufDto;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.InvoiceUniqueCodeGeneratorCufReq;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 */
public class InvoiceUniqueCodeConverter {

    public InvoiceUniqueCodeGeneratorCufDto convert(InvoiceUniqueCodeGeneratorCufReq request) {
        InvoiceUniqueCodeGeneratorCufDto response = new InvoiceUniqueCodeGeneratorCufDto();

        response.setBranchOffice(fillWithZero(String.valueOf(request.getBranchOffice()), 4));
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        response.setDatetime(dateFormat.format(request.getDatetime()));
        response.setInvoiceNumber(fillWithZero(String.valueOf(request.getInvoiceNumber()), 10));
        response.setInvoiceType(String.valueOf(request.getInvoiceType()));
        response.setModality(String.valueOf(request.getModality()));
        response.setNit(fillWithZero(String.valueOf(request.getNit()), 13));
        response.setEmisionType(String.valueOf(request.getEmisionType()));
        response.setDocumentSectorType(fillWithZero(String.valueOf(request.getDocumentSectorType()), 2));
        response.setPointOfSale(fillWithZero(String.valueOf(request.getPointOfSale()), 4));

        return response;
    }

    public String fillWithZero(InvoiceUniqueCodeGeneratorCufDto dto) {
        InvoiceUniqueCodeGeneratorCufDto result = (InvoiceUniqueCodeGeneratorCufDto) dto.clone();
        result.setInvoiceType(fillWithZero(dto.getInvoiceType(), dto.getInvoiceType().length()));

        return concat(result);
    }

    public String concat(InvoiceUniqueCodeGeneratorCufDto request) {
        return request.getNit()
            + request.getDatetime() + request.getBranchOffice()
            + request.getModality() + request.getEmisionType()
            + request.getInvoiceType() + request.getDocumentSectorType()
            + request.getInvoiceNumber() + request.getPointOfSale();
    }

    public String applyModule11(String value) {
        return value + applyModule11(value, 1, 9, false);
    }

    private String applyModule11(String value, int digits, int limit, boolean x10) {
        int mult, suma, i, n, dig;
        if (!x10) digits = 1;
        for (n = 1; n <= digits; n++) {
            suma = 0;
            mult = 2;
            for (i = value.length() - 1; i >= 0; i--) {
                suma += (mult * Integer.parseInt(value.substring(i, i + 1)));
                if (++mult > limit) mult = 2;
            }
            if (x10) {
                dig = ((suma * 10) % 11) % 10;
            } else {
                dig = suma % 11;
            }

            if (dig == 10) {
                value += "1";
            }

            if (dig == 11) {
                value += "0";
            }

            if (dig < 10) {
                value += String.valueOf(dig);
            }
        }
        return value.substring(value.length() - digits);
    }

    public String applyBase16(String value) {
        BigInteger al = new BigInteger(value);
        return al.toString(16).toUpperCase();
    }

    /**
     * @param value
     * @param maxLength
     * @return
     */
    private String fillWithZero(String value, Integer maxLength) {
        String output = String.format("%" + maxLength + "s", value).replaceAll(" ", "0");
        return output;
    }

}
