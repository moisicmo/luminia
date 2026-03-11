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
@Table(name = "room_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roomTypeSeq")
    @SequenceGenerator(name = "roomTypeSeq")
    private Long id;

    @NotNull
    @Column(name = "siat_id", nullable = false)
    private Integer siatId;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Integer companyId;
}
