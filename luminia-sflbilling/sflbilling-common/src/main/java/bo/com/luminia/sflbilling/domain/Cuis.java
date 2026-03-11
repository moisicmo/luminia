package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@ToString(exclude = {"pointSale"})
@Entity
@Table(name = "cuis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Cuis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuisSeq")
    @SequenceGenerator(name = "cuisSeq")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "cuis", length = 20, nullable = false)
    private String cuis;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private ZonedDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    @NotNull
    private PointSale pointSale;
}
