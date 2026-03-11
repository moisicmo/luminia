package bo.gob.impuestos.sfe.invoice.xml.header;

import bo.gob.impuestos.sfe.invoice.xml.XmlDateTimeAdapter;
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
@XmlType(propOrder = {"nitEmisor", "razonSocialEmisor", "municipio", "telefono", "numeroFactura", "cuf", "cufd",
        "codigoSucursal", "direccion", "codigoPuntoVenta", "fechaEmision", "nombreRazonSocial", "codigoTipoDocumentoIdentidad",
        "numeroDocumento", "complemento", "direccionComprador", "codigoCliente",
        "incoterm", "incotermDetalle", "puertoDestino",
        "lugarDestino", "codigoPais", "codigoMetodoPago", "numeroTarjeta", "montoTotal",
        "costosGastosNacionales", "totalGastosNacionalesFob", "costosGastosInternacionales", "totalGastosInternacionales", "montoDetalle",
        "montoTotalSujetoIva", "codigoMoneda", "tipoCambio", "montoTotalMoneda", "numeroDescripcionPaquetesBultos",
        "informacionAdicional", "descuentoAdicional", "codigoExcepcion", "cafc",
        "leyenda", "usuario", "codigoDocumentoSector"})
public class XmlComercialExportacionHeader extends XmlBaseHeaderInvoice {
    @NotBlank
    @Size(min = 1, max = 500)
    private String direccionComprador;
    @NotBlank
    @Size(min = 1, max = 500)
    private String lugarDestino;
    @NotNull
    private Integer codigoPais;
    @Size(min = 0, max = 10000)
    private String informacionAdicional;
    @NotBlank
    @Size(min = 1, max = 100)
    private String incoterm;
    @NotBlank
    @Size(min = 1, max = 100)
    private String incotermDetalle;
    @NotBlank
    @Size(min = 1, max = 500)
    private String puertoDestino;

    @Size(min = 0, max = 10000)
    private String costosGastosNacionales;
    @NotNull
    private BigDecimal totalGastosNacionalesFob;
    @Size(min = 0, max = 10000)
    private String costosGastosInternacionales;
    private BigDecimal totalGastosInternacionales;
    @NotNull
    private BigDecimal montoDetalle;
    @Size(min = 0, max = 10000)
    private String numeroDescripcionPaquetesBultos;

    @XmlElement(name = "direccionComprador")
    public String getDireccionComprador() {
        return direccionComprador;
    }

    @XmlElement(name = "lugarDestino")
    public String getLugarDestino() {
        return lugarDestino;
    }

    @XmlElement(name = "codigoPais")
    public Integer getCodigoPais() {
        return codigoPais;
    }

    @XmlElement(name = "informacionAdicional", nillable = true)
    public String getInformacionAdicional() {
        return informacionAdicional;
    }

    @XmlElement(name = "incoterm")
    public String getIncoterm() {
        return incoterm;
    }

    @XmlElement(name = "incotermDetalle")
    public String getIncotermDetalle() {
        return incotermDetalle;
    }

    @XmlElement(name = "puertoDestino")
    public String getPuertoDestino() {
        return puertoDestino;
    }


    @XmlElement(name = "costosGastosNacionales")
    public String getCostosGastosNacionales() {
        return costosGastosNacionales;
    }

    @XmlElement(name = "totalGastosNacionalesFob", required = true)
    public BigDecimal getTotalGastosNacionalesFob() {
        return totalGastosNacionalesFob != null ? totalGastosNacionalesFob.setScale(2, BigDecimal.ROUND_HALF_EVEN) : new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @XmlElement(name = "costosGastosInternacionales")
    public String getCostosGastosInternacionales() {
        return costosGastosInternacionales;
    }

    @XmlElement(name = "totalGastosInternacionales")
    public BigDecimal getTotalGastosInternacionales() {
        return totalGastosInternacionales != null ? totalGastosInternacionales.setScale(2, BigDecimal.ROUND_HALF_EVEN) : new BigDecimal(0).setScale(2);
    }

    @XmlElement(name = "montoDetalle", required = true)
    public BigDecimal getMontoDetalle() {
        return montoDetalle != null ? montoDetalle.setScale(2, BigDecimal.ROUND_HALF_EVEN) :  new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @XmlElement(name = "numeroDescripcionPaquetesBultos")
    public String getNumeroDescripcionPaquetesBultos() {
        return numeroDescripcionPaquetesBultos;
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
        return montoTotal != null ? montoTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN) :  new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @XmlElement(name = "montoTotalSujetoIva", required = false)
    public BigDecimal getMontoTotalSujetoIva() {
        return montoTotalSujetoIva != null ? montoTotalSujetoIva.setScale(0, BigDecimal.ROUND_HALF_EVEN) :  new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @XmlElement(name = "codigoMoneda", required = true)
    public Integer getCodigoMoneda() {
        return codigoMoneda;
    }

    @XmlElement(name = "tipoCambio", required = true)
    public BigDecimal getTipoCambio() {
        return tipoCambio != null ? tipoCambio.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "montoTotalMoneda", required = true)
    public BigDecimal getMontoTotalMoneda() {
        return montoTotalMoneda != null ? montoTotalMoneda.setScale(2, BigDecimal.ROUND_HALF_EVEN) :  new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @XmlElement(name = "descuentoAdicional", required = false, nillable = true)
    public BigDecimal getDescuentoAdicional() {
        return descuentoAdicional != null ? descuentoAdicional.setScale(2, BigDecimal.ROUND_HALF_EVEN) :  new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @XmlElement(name = "codigoExcepcion", required = false, nillable = true)
    public Integer getCodigoExcepcion() {
        return codigoExcepcion;
    }

    @XmlElement(name = "cafc", required = false, nillable = true)
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
        this.setDireccionComprador((String) header.get(XmlTags.HEADER_DIRECCION_COMPRADOR));
        this.setLugarDestino((String) header.get(XmlTags.HEADER_LUGAR_DESTINO));
        if (header.containsKey(XmlTags.HEADER_CODIGO_PAIS)) {
            this.setCodigoPais(Integer.parseInt((String) header.get(XmlTags.HEADER_CODIGO_PAIS)));
        }
        this.setInformacionAdicional((String) header.get(XmlTags.HEADER_INFORMACION_ADICIONAL));

        this.setIncoterm((String) header.get(XmlTags.HEADER_INCOTERM));
        this.setIncotermDetalle((String) header.get(XmlTags.HEADER_INCOTERM_DETALLE));
        this.setPuertoDestino((String) header.get(XmlTags.HEADER_PUERTO_DESTINO));

        this.setCostosGastosNacionales((String) header.get(XmlTags.HEADER_COSTOS_GASTOS_NACIONALES));
        if (header.containsKey(XmlTags.HEADER_TOTAL_GASTOS_NACIONALES_FOB)) {
            this.setTotalGastosNacionalesFob(new BigDecimal((String) header.get(XmlTags.HEADER_TOTAL_GASTOS_NACIONALES_FOB)));
        }
        this.setCostosGastosInternacionales((String) header.get(XmlTags.HEADER_COSTOS_GASTOS_INTERNACIONALES));
        if (header.containsKey(XmlTags.HEADER_TOTAL_GASTOS_INTERNACIONALES)) {
            this.setTotalGastosInternacionales(new BigDecimal((String) header.get(XmlTags.HEADER_TOTAL_GASTOS_INTERNACIONALES)));
        }
        if (header.containsKey(XmlTags.HEADER_MONTO_DETALLE)) {
            this.setMontoDetalle(new BigDecimal((String) header.get(XmlTags.HEADER_MONTO_DETALLE)));
        }
        this.setNumeroDescripcionPaquetesBultos((String) header.get(XmlTags.HEADER_NUMERO_PAQUETES_BULTOS));
    }
}
