package bo.com.luminia.sflbilling.domain;

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
@Table(name = "check_invoice")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CheckInvoice extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checkInvoiceSeq")
    @SequenceGenerator(name = "checkInvoiceSeq")
    private Long id;

    @NotNull
    @Column(name = "position", nullable = false)
    private Long position;
}
