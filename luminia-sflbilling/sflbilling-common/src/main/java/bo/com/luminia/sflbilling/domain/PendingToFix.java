package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.PendingToFixActionEnum;
import bo.com.luminia.sflbilling.domain.enumeration.PendingToFixStatusEnum;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ToString(exclude = {"company", "invoice"})
@Entity
@Table(name = "pending_to_fix")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PendingToFix extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @ManyToOne(optional = false)
    @NotNull
    private Company company;


    @ManyToOne(optional = true)
    private Event event;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private PendingToFixActionEnum action;

    @Column(name = "counter", nullable = false)
    private Integer counter = 0;

    @Size(max = 2000)
    @Column(name = "fail_reason", length = 2000)
    private String failReason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PendingToFixStatusEnum status;
}
