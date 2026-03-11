package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ToString(exclude = {"branchOffice"})
@Entity
@Table(name = "point_sale")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PointSale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pointSaleSeq")
    @SequenceGenerator(name = "pointSaleSeq")
    private Long id;

    @NotNull
    @Column(name = "point_sale_siat_id", nullable = false)
    private Integer pointSaleSiatId;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @ManyToOne(optional = true)
    private PointSaleType pointSaleType;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    @NotNull
    private BranchOffice branchOffice;
}
