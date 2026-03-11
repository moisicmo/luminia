package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.BatchStatusEnum;
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
@Table(name = "batch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Batch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batchSeq")
    @SequenceGenerator(name = "batchSeq")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "reception_code", length = 50, nullable = false)
    private String receptionCode;

    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchStatusEnum status;

    @ManyToOne(optional = false)
    @NotNull
    private Company company;
}
