package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.InvoiceSiatStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "automatic_invoice_fix")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AutomaticInvoiceFix extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", nullable = false)
    private InvoiceStatusEnum oldStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private InvoiceStatusEnum newStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "siat_status", nullable = false)
    private InvoiceSiatStatusEnum siatStatus;

    @NotNull
    @Column(name = "siat_observation", nullable = false)
    private String siatObservation;

}
