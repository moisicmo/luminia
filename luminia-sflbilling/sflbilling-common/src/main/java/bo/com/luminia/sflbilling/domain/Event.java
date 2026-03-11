package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Event extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventSeq")
    @SequenceGenerator(name = "eventSeq")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "cufd_event", length = 100, nullable = false)
    private String cufdEvent;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private ZonedDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @Column(name = "reception_code")
    private Integer receptionCode;

    @NotNull
    @Size(max = 500)
    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Size(max = 255)
    @Column(name = "cafc", length = 255)
    private String cafc;

    @ManyToOne(optional = false)
    @NotNull
    private BranchOffice branchOffice;

    @ManyToOne(optional = false)
    @NotNull
    private SignificantEvent significantEvent;

    @ManyToOne(optional = false)
    @NotNull
    private PointSale pointSale;
}
