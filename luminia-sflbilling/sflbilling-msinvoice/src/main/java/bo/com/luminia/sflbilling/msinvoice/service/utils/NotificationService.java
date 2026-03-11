package bo.com.luminia.sflbilling.msinvoice.service.utils;

import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {


    public void notifyErrorInvoiceValidation(InvoiceIssueReq request, String invalid_xml) {
    }

    public void notifyErrorInvoiceEmition(InvoiceIssueReq request, String message) {
    }
}
