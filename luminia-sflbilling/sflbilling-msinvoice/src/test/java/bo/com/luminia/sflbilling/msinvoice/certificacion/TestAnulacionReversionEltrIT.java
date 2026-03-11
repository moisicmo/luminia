package bo.com.luminia.sflbilling.msinvoice.certificacion;

import bo.com.luminia.sflbilling.domain.Invoice;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msinvoice.repository.InvoiceRepository;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceCancellationReq;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest
public class TestAnulacionReversionEltrIT {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private MockMvc mockMvc;

    private InvoiceCancellationReq createInvoiceCancelation(String cufd, int cancelation) {

        InvoiceCancellationReq request = new InvoiceCancellationReq();
        request.setCuf(cufd);
        request.setCancellationReasonSiat(cancelation);

        return request;
    }

    @Test
    public void testPasoReversionEltr() {

        int cancelation_reason = 1;
        List<Invoice> listOfInvoices = invoiceRepository.findAll();

        for (Invoice invoice : listOfInvoices) {
            if (invoice.getStatus() == InvoiceStatusEnum.EMITTED
                && invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                InvoiceCancellationReq request = createInvoiceCancelation(invoice.getCuf(), cancelation_reason);
                try {
                    mockMvc
                        .perform(post("/api/invoice/cancellation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.convertObjectToJsonBytes(request)))
                        .andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
