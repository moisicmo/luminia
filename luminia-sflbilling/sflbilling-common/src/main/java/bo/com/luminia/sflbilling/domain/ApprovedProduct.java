package bo.com.luminia.sflbilling.domain;

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
@Table(name = "approved_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApprovedProduct extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approvedProductSeq")
    @SequenceGenerator(name = "approvedProductSeq")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "product_code", length = 20, nullable = false)
    private String productCode;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @ManyToOne(optional = false)
    @NotNull
    private Company company;

    @ManyToOne(optional = false)
    @NotNull
    private ProductService productService;

    @ManyToOne(optional = false)
    @NotNull
    private MeasurementUnit measurementUnit;
}
