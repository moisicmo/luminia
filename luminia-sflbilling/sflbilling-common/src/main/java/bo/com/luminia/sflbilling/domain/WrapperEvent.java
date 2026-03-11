package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.WrapperEventStatusEnum;
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
@Table(name = "wrapper_event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WrapperEvent extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wrapperEventSeq")
    @SequenceGenerator(name = "wrapperEventSeq")
    private Long id;

    @Column(name = "reception_code", nullable = false)
    private String receptionCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WrapperEventStatusEnum status;

    @ManyToOne(optional = false)
    @NotNull
    private Event event;

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
