package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "product_service")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "productServiceSeq")
    @SequenceGenerator(name = "productServiceSeq")
    private Long id;

    @NotNull
    @Column(name = "siat_id", nullable = false)
    private Integer siatId;

    @NotNull
    @Column(name = "activity_code", nullable = false)
    private Integer activityCode;

    @NotNull
    @Size(max = 1000)
    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Integer companyId;
}
