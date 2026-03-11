package bo.com.luminia.sflbilling.msinvoice.invoice.signature;

import bo.com.luminia.sflbilling.msinvoice.service.sfe.signature.SignatureService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SignTest {

    private SignatureService signatureService;

    @Test
    void testSign() throws Exception {
        try {
            Long companyId = 1L;
            String xml = "<note>\n" +
                "<to>Tove</to>\n" +
                "<from>Jani</from>\n" +
                "<heading>Reminder</heading>\n" +
                "<body>Don't forget me this weekend!</body>\n" +
                "</note>";
            signatureService = new SignatureService();
            String xmlSign = signatureService.singXml(xml, companyId);
            assertThat(xmlSign).isNotNull();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
