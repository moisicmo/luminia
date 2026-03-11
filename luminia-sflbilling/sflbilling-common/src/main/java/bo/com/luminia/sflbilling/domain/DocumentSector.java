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
@Table(name = "document_sector")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DocumentSector implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documentSectorSeq")
    @SequenceGenerator(name = "documentSectorSeq")
    private Long id;

    @NotNull
    @Column(name = "siat_id", nullable = false)
    private Integer siatId;

    @NotNull
    @Column(name = "activity_code", nullable = false)
    private Integer activityCode;

    @NotNull
    @Size(max = 10)
    @Column(name = "document_sector_type", length = 10, nullable = false)
    private String documentSectorType;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Integer companyId;
}
