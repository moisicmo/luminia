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
@XmlType(propOrder = {"nitEmisor", "numeroFactura", "cufd", "cuf",
        "codigoSucursal", "codigoPuntoVenta", "direccion", "fechaEmision", "razonSocialEmisor",
        "nombreRazonSocial", "numeroDocumento", "codigoTipoDocumentoIdentidad",
        "nombrePasajero", "numeroDocumentoPasajero", "codigoIataLineaAerea", "codigoIataAgenteViajes",
        "nitAgenteViajes", "codigoOrigenServicio", "codigoMoneda", "tipoCambio", "montoTarifa",
        "montoTotal", "montoTotalMoneda", "montoTotalSujetoIva", "codigoMetodoPago", "codigoTipoTransaccion",
        "usuario", "leyenda", "codigoDocumentoSector"})
public class XmlBoletoAereoHeader extends XmlBaseHeaderInvoice {

    @NotBlank
    @Size(min = 1, max = 300)
    private String nombrePasajero;

    @Size(min = 1, max = 20)
    private String numeroDocumentoPasajero;

    @NotBlank
    private Integer codigoIataLineaAerea;

    private Long codigoIataAgenteViajes;

    private Long nitAgenteViajes;

    @NotBlank
    @Size(min = 1, max = 50)
    private String codigoOrigenServicio;

    @NotNull
    private BigDecimal montoTarifa;

    @NotBlank
    @Size(min = 1, max = 10)
    private String codigoTipoTransaccion;

    @XmlElement(name = "nitEmisor", required = true)
    public Long getNitEmisor() {
        return nitEmisor;
    }

    @XmlElement(name = "razonSocialEmisor", required = true)
    public String getRazonSocialEmisor() {
        return razonSocialEmisor;
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

    @XmlElement(name = "nombrePasajero", required = true)
    public String getNombrePasajero() {
        return nombrePasajero;
    }

    @XmlElement(name = "numeroDocumentoPasajero")
    public String getNumeroDocumentoPasajero() {
        return numeroDocumentoPasajero;
    }

    @XmlElement(name = "codigoIataLineaAerea", required = true)
    public Integer getCodigoIataLineaAerea() {
        return codigoIataLineaAerea;
    }

    @XmlElement(name = "codigoIataAgenteViajes")
    public Long getCodigoIataAgenteViajes() {
        return codigoIataAgenteViajes;
    }

    @XmlElement(name = "nitAgenteViajes")
    public Long getNitAgenteViajes() {
        return nitAgenteViajes;
    }

    @XmlElement(name = "codigoOrigenServicio", required = true)
    public String getCodigoOrigenServicio() {
        return codigoOrigenServicio;
    }

    @XmlElement(name = "montoTarifa", required = true)
    public BigDecimal getMontoTarifa() {
        return montoTarifa != null ? montoTarifa.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "codigoTipoTransaccion", required = true)
    public String getCodigoTipoTransaccion() {
        return codigoTipoTransaccion;
    }

    @XmlElement(name = "numeroDocumento", required = true)
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    @XmlElement(name = "codigoMetodoPago", required = true)
    public Integer getCodigoMetodoPago() {
        return codigoMetodoPago;
    }

    @XmlElement(name = "montoTotal", required = true)
    public BigDecimal getMontoTotal() {
        return montoTotal != null ? montoTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "montoTotalSujetoIva", required = false)
    public BigDecimal getMontoTotalSujetoIva() {
        return montoTotalSujetoIva != null ? montoTotalSujetoIva.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
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
        return montoTotalMoneda != null ? montoTotalMoneda.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
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

    public void convert(Map<String, Object> header) {
        this.convertToHeaderBase(header);

        this.setNombrePasajero(( String) header.get(XmlTags.HEADER_NOMBRE_PASAJERO));
        if (header.containsKey(XmlTags.HEADER_NUMERO_DOCUMENTO_PASAJERO)) {
            this.setNumeroDocumentoPasajero( (String) header.get(XmlTags.HEADER_NUMERO_DOCUMENTO_PASAJERO));
        }
        this.setCodigoIataLineaAerea(Integer.parseInt((String) header.get(XmlTags.HEADER_CODIGO_IATA_LINEA_AEREA)));
        if (header.containsKey(XmlTags.HEADER_CODIGO_IATA_AGENTE_VIAJES) && header.get(XmlTags.HEADER_CODIGO_IATA_AGENTE_VIAJES) != null) {
            this.setCodigoIataAgenteViajes(Long.parseLong( (String) header.get(XmlTags.HEADER_CODIGO_IATA_AGENTE_VIAJES)));
        }
        if (header.containsKey(XmlTags.HEADER_NIT_AGENTE_VIAJES) && header.get(XmlTags.HEADER_NIT_AGENTE_VIAJES) != null) {
            this.setNitAgenteViajes(Long.parseLong( (String) header.get(XmlTags.HEADER_NIT_AGENTE_VIAJES)));
        }
        this.setCodigoOrigenServicio( (String) header.get(XmlTags.HEADER_CODIGO_ORIGEN_SERVICIO));
        if (header.containsKey(XmlTags.HEADER_MONTO_TARIFA)) {
            this.setMontoTarifa(new BigDecimal( (String) header.get(XmlTags.HEADER_MONTO_TARIFA)));
        }
        this.setCodigoTipoTransaccion( (String) header.get(XmlTags.HEADER_CODIGO_TIPO_TRANSACCION));
    }
}
