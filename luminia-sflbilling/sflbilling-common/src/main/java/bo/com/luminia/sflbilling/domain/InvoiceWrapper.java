package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.InvoiceWrapperStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "invoice_wrapper")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InvoiceWrapper extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceWrapperSeq")
    @SequenceGenerator(name = "invoiceWrapperSeq")
    private Long id;

    @NotNull
    @Column(name = "file_number", nullable = false)
    private Integer fileNumber;

    @Size(max = 255)
    @Column(name = "response_message", length = 255)
    private String responseMessage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceWrapperStatusEnum status;

    @ManyToOne(optional = false)
    @NotNull
    private InvoiceBatch invoiceBatch;

    @ManyToOne(optional = false)
    @NotNull
    private Wrapper wrapper;
}
