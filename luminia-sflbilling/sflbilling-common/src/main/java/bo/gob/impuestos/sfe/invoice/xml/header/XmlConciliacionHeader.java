package bo.gob.impuestos.sfe.invoice.xml.header;

import bo.gob.impuestos.sfe.invoice.xml.XmlDateTimeAdapter;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Setter
@XmlType(propOrder = {"nitEmisor", "razonSocialEmisor", "municipio", "telefono",
        "numeroNotaConciliacion", "cuf", "cufd",
        "codigoSucursal", "direccion", "codigoPuntoVenta", "fechaEmision", "nombreRazonSocial", "codigoTipoDocumentoIdentidad",
        "numeroDocumento", "complemento", "codigoCliente", "numeroFactura", "numeroAutorizacionCuf", "codigoControl",
        "fechaEmisionFactura", "montoTotalOriginal", "montoTotalConciliado", "creditoFiscalIva", "debitoFiscalIva",
        "codigoExcepcion", "leyenda", "usuario", "codigoDocumentoSector"})
public class XmlConciliacionHeader extends XmlBaseHeaderInvoice {
    @NotNull
    private Long numeroNotaConciliacion;
    @Size(min = 0, max = 20)
    private String codigoControl;
    @NotBlank
    @Size(min = 1, max = 100)
    private String numeroAutorizacionCuf;
    @NotNull
    private Date fechaEmisionFactura;
    @NotNull
    private BigDecimal montoTotalOriginal;
    @NotNull
    private BigDecimal montoTotalConciliado;
    @NotNull
    private BigDecimal creditoFiscalIva;
    @NotNull
    private BigDecimal debitoFiscalIva;

    @XmlElement(name = "numeroNotaConciliacion")
    public Long getNumeroNotaConciliacion() {
        return numeroNotaConciliacion;
    }

    @XmlElement(name = "numeroAutorizacionCuf")
    public String getNumeroAutorizacionCuf() {
        return numeroAutorizacionCuf;
    }

    @XmlElement(name = "codigoControl")
    public String getCodigoControl() {
        return codigoControl;
    }

    @XmlElement(name = "fechaEmisionFactura", required = true)
    @XmlJavaTypeAdapter(XmlDateTimeAdapter.class)
    public Date getFechaEmisionFactura() {
        return fechaEmisionFactura;
    }

    @XmlElement(name = "montoTotalOriginal")
    public BigDecimal getMontoTotalOriginal() {
        return montoTotalOriginal != null ? montoTotalOriginal.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "montoTotalConciliado", required = true)
    public BigDecimal getMontoTotalConciliado() {
        return montoTotalConciliado != null ? montoTotalConciliado.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "creditoFiscalIva", nillable = true)
    public BigDecimal getCreditoFiscalIva() {
        return creditoFiscalIva != null ? creditoFiscalIva.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "debitoFiscalIva")
    public BigDecimal getDebitoFiscalIva() {
        return debitoFiscalIva != null ? debitoFiscalIva.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "nitEmisor", required = true)
    public Long getNitEmisor() {
        return nitEmisor;
    }

    @XmlElement(name = "razonSocialEmisor", required = true)
    public String getRazonSocialEmisor() {
        return razonSocialEmisor;
    }

    @XmlElement(name = "municipio", required = true)
    public String getMunicipio() {
        return municipio;
    }

    @XmlElement(name = "telefono", nillable = true)
    public String getTelefono() {
        return telefono;
    }

    @XmlElement(name = "numeroFactura", required = true)
    public Integer getNumeroFactura() {
        return numeroFactura;
    }

    @XmlElement(name = "cuf", required = true)
    public String getCuf() {
        return cuf;
    }

    @XmlElement(name = "cufd", required = true)
    public String getCufd() {
        return cufd;
    }

    @XmlElement(name = "codigoSucursal", required = true)
    public Integer getCodigoSucursal() {
        return codigoSucursal;
    }

    @XmlElement(name = "direccion", required = true)
    public String getDireccion() {
        return direccion;
    }

    @XmlElement(name = "codigoPuntoVenta", nillable = true)
    public Integer getCodigoPuntoVenta() {
        return codigoPuntoVenta;
    }

    @XmlJavaTypeAdapter(XmlDateTimeAdapter.class)
    @XmlElement(name = "fechaEmision", required = true)
    public Date getFechaEmision() {
        return fechaEmision;
    }

    @XmlElement(name = "nombreRazonSocial", required = true)
    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    @XmlElement(name = "codigoTipoDocumentoIdentidad", required = true)
    public Integer getCodigoTipoDocumentoIdentidad() {
        return codigoTipoDocumentoIdentidad;
    }

    @XmlElement(name = "numeroDocumento", required = true)
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    @XmlElement(name = "complemento", nillable = true)
    public String getComplemento() {
        return complemento;
    }

    @XmlElement(name = "codigoCliente", required = true)
    public String getCodigoCliente() {
        return codigoCliente;
    }

    @XmlElement(name = "codigoExcepcion", required = true, nillable = true)
    public Integer getCodigoExcepcion() {
        return super.getCodigoExcepcion();
    }

    @XmlElement(name = "leyenda", required = true)
    public String getLeyenda() {
        return leyenda;
    }

    @XmlElement(name = "usuario", required = true)
    public String getUsuario() {
        return usuario;
    }

    @XmlElement(name = "codigoDocumentoSector", required = true)
    public Integer getCodigoDocumentoSector() {
        return codigoDocumentoSector;
    }

    @Override
    public void convert(Map<String, Object> header) {
        this.convertToHeaderBase(header);
        if (header.containsKey(XmlTags.HEADER_NUMERO_NOTA_CONCILIACION)) {
            this.setNumeroNotaConciliacion(Long.parseLong( (String) header.get(XmlTags.HEADER_NUMERO_NOTA_CONCILIACION)));
        }
        this.setNumeroAutorizacionCuf( (String) header.get(XmlTags.HEADER_NUMERO_AUTORIZACION_CUF));
        this.setCodigoControl( (String) header.get(XmlTags.HEADER_CODIGO_CONTROL));
        if (header.containsKey(XmlTags.HEADER_FECHA_EMISION_FACTURA)) {
            this.setFechaEmisionFactura(XmlDateUtil.parseDateToYyyymmddThhmmssSSS(
                    (String) header.get(XmlTags.HEADER_FECHA_EMISION_FACTURA)));
        }
        if (header.containsKey(XmlTags.HEADER_MONTO_TOTAL_ORIGINAL)) {
            this.setMontoTotalOriginal(new BigDecimal( (String) header.get(XmlTags.HEADER_MONTO_TOTAL_ORIGINAL)));
        }
        if (header.containsKey(XmlTags.HEADER_MONTO_TOTAL_CONCILIADO)) {
            this.setMontoTotalConciliado(new BigDecimal((String) header.get(XmlTags.HEADER_MONTO_TOTAL_CONCILIADO)));
        }
        if (header.containsKey(XmlTags.HEADER_CREDITO_FISCAL_IVA)) {
            this.setCreditoFiscalIva(new BigDecimal( (String) header.get(XmlTags.HEADER_CREDITO_FISCAL_IVA)));
        }
        if (header.containsKey(XmlTags.HEADER_DEBITO_FISCAL_IVA)) {
            this.setDebitoFiscalIva(new BigDecimal( (String) header.get(XmlTags.HEADER_DEBITO_FISCAL_IVA)));
        }
    }
}
