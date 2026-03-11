package bo.com.luminia.sflbilling.msreport.web.rest.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@EqualsAndHashCode()
public class ResumeInvoiceResponse implements Serializable {

    @Column(name = "quantity_emitted", nullable = false)
    private Long quantityEmitted = 0L;
    @Column(name = "amount_emitted", nullable = false)
    private Double amountEmitted = 0.0;

    @Column(name = "quantity_canceled", nullable = false)
    private Long quantityCanceled = 0L;
    @Column(name = "amount_canceled", nullable = false)
    private Double amountCanceled = 0.0;

    @Column(name = "quantity_reversed", nullable = false)
    private Long quantityReversed = 0L;
    @Column(name = "amountReversed", nullable = false)
    private Double amountReversed = 0.0;

    @Column(name = "quantity_pending", nullable = false)
    private Long quantityPending = 0L;
    @Column(name = "amount_pending", nullable = false)
    private Double amountPending = 0.0;

    @Column(name = "quantity_observed", nullable = false)
    private Long quantityObserved = 0L;
    @Column(name = "amount_observed", nullable = false)
    private Double amountObserved = 0.0;

}
