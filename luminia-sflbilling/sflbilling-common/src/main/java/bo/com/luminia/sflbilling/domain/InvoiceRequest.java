package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.InvoiceRequestTypeEnum;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "invoice_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InvoiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceRequestSeq")
    @SequenceGenerator(name = "invoiceRequestSeq")
    private Long id;

    @NotNull
    @Column(name = "request_date", nullable = false)
    private ZonedDateTime requestDate;

    @Column(name = "response_date", nullable = true)
    private ZonedDateTime responseDate;

    @ManyToOne(optional = true)
    private Company company;

    @OneToOne(optional = true)
    private Invoice invoice;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "request", nullable = false)
    private String request;

    @Size(max = 70)
    @Column(name = "cuf", length = 70, nullable = true)
    private String cuf;

    @Size(max = 500)
    @Column(name = "observation", length = 500, nullable = true)
    private String observation;

    @Column(name = "response")
    private Boolean response;

    @Column(name = "elapsed_time")
    private Long elapsedTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InvoiceRequestTypeEnum type;

    @Column(name = "error_checked")
    private Boolean errorChecked = false;


}
