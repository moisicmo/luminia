package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "sync_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SyncLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syncLogSeq")
    @SequenceGenerator(name = "syncLogSeq")
    private Long id;

    @NotNull
    @Column(name = "sync_type", nullable = false)
    private Integer syncType;

    @Column(name = "start_date")
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    private ZonedDateTime endDate;

    @Column(name = "response")
    private Boolean response;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;
}
