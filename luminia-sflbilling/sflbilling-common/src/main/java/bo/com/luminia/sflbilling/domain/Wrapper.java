package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.WrapperStatusEnum;
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
@Table(name = "wrapper")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Wrapper extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wrapperSeq")
    @SequenceGenerator(name = "wrapperSeq")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "cudf_event", length = 100, nullable = false)
    private String cudfEvent;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private ZonedDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @Column(name = "reception_code", nullable = false)
    private String receptionCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WrapperStatusEnum status;

    @ManyToOne(optional = false)
    private ScheduleSetting scheduleSetting;

    @ManyToOne(optional = false)
    @NotNull
    private BranchOffice branchOffice;

    @ManyToOne(optional = false)
    @NotNull
    private PointSale pointSale;

    @ManyToOne(optional = false)
    @NotNull
    private SectorDocumentType sectorDocumentType;
}
