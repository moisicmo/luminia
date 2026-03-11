package bo.gob.impuestos.sfe.invoice.xml.header;

import bo.gob.impuestos.sfe.invoice.xml.XmlDateTimeAdapter;
import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import lombok.Setter;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


@Setter
@XmlType(propOrder = {"nitEmisor", "razonSocialEmisor", "municipio","telefono","numeroFactura","cuf","cufd",
    "codigoSucursal","direccion","codigoPuntoVenta","fechaEmision","nombreRazonSocial","codigoTipoDocumentoIdentidad",
    "numeroDocumento","complemento","codigoCliente",
    "periodoFacturado",
    "codigoMetodoPago","numeroTarjeta","montoTotal","montoTotalSujetoIva",
    "codigoMoneda","tipoCambio","montoTotalMoneda",
    "descuentoAdicional", "codigoExcepcion", "cafc",
    "leyenda","usuario","codigoDocumentoSector"})
public class XmlAlquilerBienesInmueblesHeader extends XmlBaseHeaderInvoice{

    @Size(min = 0 , max = 100)
    private String periodoFacturado;

    @XmlElement(name = "periodoFacturado", nillable = true)
    public String getPeriodoFacturado() {
        return periodoFacturado;
    }
    //

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
    @XmlElement(name = "codigoPuntoVenta", required = true)
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
    @XmlElement(name = "codigoMetodoPago", required = true)
    public Integer getCodigoMetodoPago() {
        return codigoMetodoPago;
    }
    @XmlElement(name = "numeroTarjeta", nillable = true)
    public Long getNumeroTarjeta() {
        return numeroTarjeta;
    }
    @XmlElement(name = "montoTotal", required = true)
    public BigDecimal getMontoTotal() {
        return montoTotal!=null? montoTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN): null;
    }
    @XmlElement(name = "montoTotalSujetoIva", required = false)
    public BigDecimal getMontoTotalSujetoIva() {
        return montoTotalSujetoIva!= null? montoTotalSujetoIva.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }
    @XmlElement(name = "codigoMoneda", required = true)
    public Integer getCodigoMoneda() {
        return codigoMoneda;
    }
    @XmlElement(name = "tipoCambio", required = true)
    public BigDecimal getTipoCambio() {
        return tipoCambio!= null ? tipoCambio.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }
    @XmlElement(name = "montoTotalMoneda", required = true)
    public BigDecimal getMontoTotalMoneda() {
        return montoTotalMoneda!= null ? montoTotalMoneda.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "descuentoAdicional", required = false, nillable = true)
    public BigDecimal getDescuentoAdicional() {
        return descuentoAdicional!= null? descuentoAdicional.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "codigoExcepcion", required = false, nillable = true)
    public Integer getCodigoExcepcion() {
        return codigoExcepcion;
    }

    @XmlElement(name = "cafc", required = false,nillable = true)
    public String getCafc() {
        return cafc;
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
        this.setPeriodoFacturado((String)header.get(XmlTags.HEADER_PERIODO_FACTURADO));
    }
}
