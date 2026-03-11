package bo.com.luminia.sflbilling.domain;

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
@Table(name = "invoice_batch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InvoiceBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceBatchSeq")
    @SequenceGenerator(name = "invoiceBatchSeq")
    private Long id;

    @NotNull
    @Column(name = "invoice_number", nullable = false)
    private Long invoiceNumber;

    @NotNull
    @Column(name = "broadcast_date", nullable = false)
    private ZonedDateTime broadcastDate;

    @NotNull
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "invoice_json")
    private String invoiceJson;

    @Size(max = 255)
    @Column(name = "response_message", length = 255)
    private String responseMessage;

    @ManyToOne(optional = false)
    private Invoice invoice;

    @ManyToOne(optional = false)
    @NotNull
    private Batch batch;

    @ManyToOne(optional = false)
    @NotNull
    private SectorDocumentType sectorDocumentType;
}
