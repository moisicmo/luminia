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
    "codigoSucursal","direccion","codigoPuntoVenta",
    "mes","gestion","ciudad","zona","numeroMedidor",
    "fechaEmision","nombreRazonSocial",
    "domicilioCliente",
    "codigoTipoDocumentoIdentidad",
    "numeroDocumento","complemento","codigoCliente","codigoMetodoPago","numeroTarjeta","montoTotal","montoTotalSujetoIva",
    "consumoPeriodo","beneficiarioLey1886","montoDescuentoLey1886","montoDescuentoTarifaDignidad","tasaAseo","tasaAlumbrado",
        "ajusteNoSujetoIva","detalleAjusteNoSujetoIva", "ajusteSujetoIva","detalleAjusteSujetoIva",
        "otrosPagosNoSujetoIva", "detalleOtrosPagosNoSujetoIva", "otrasTasas",
        "codigoMoneda","tipoCambio","montoTotalMoneda","descuentoAdicional","codigoExcepcion","cafc",
        "leyenda","usuario","codigoDocumentoSector"})
public class XmlServiciosBasicosHeader extends XmlBaseHeaderInvoice {

    @Size(min = 1, max = 10)
    private String mes;
    private Integer gestion;
    @Size(min = 1, max = 100)
    private String ciudad;
    @Size(min = 1, max = 100)
    private String zona;
    @Size(min = 1, max = 100)
    private String numeroMedidor;
    @Size(min = 1, max = 500)
    private String domicilioCliente;
    private BigDecimal consumoPeriodo;
    private Integer beneficiarioLey1886;
    private BigDecimal montoDescuentoLey1886;
    private BigDecimal montoDescuentoTarifaDignidad;
    private BigDecimal tasaAseo;
    private BigDecimal tasaAlumbrado;
    private BigDecimal ajusteNoSujetoIva;
    private String detalleAjusteNoSujetoIva;
    private BigDecimal otrosPagosNoSujetoIva;
    private String detalleOtrosPagosNoSujetoIva;
    private BigDecimal ajusteSujetoIva;
    private String detalleAjusteSujetoIva;
    private BigDecimal otrasTasas;

    @XmlElement(name = "mes", nillable = true)
    public String getMes() {
        return mes;
    }

    @XmlElement(name = "gestion", nillable = true)
    public Integer getGestion() {
        return gestion;
    }

    @XmlElement(name = "ciudad", nillable = true)
    public String getCiudad() {
        return ciudad;
    }

    @XmlElement(name = "zona", nillable = true)
    public String getZona() {
        return zona;
    }

    @XmlElement(name = "numeroMedidor", nillable = true)
    public String getNumeroMedidor() {
        return numeroMedidor;
    }

    @XmlElement(name = "domicilioCliente", nillable = true)
    public String getDomicilioCliente() {
        return domicilioCliente;
    }

    @XmlElement(name = "consumoPeriodo", nillable = true)
    public BigDecimal getConsumoPeriodo() {
        return consumoPeriodo.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @XmlElement(name = "beneficiarioLey1886", nillable = true)
    public Integer getBeneficiarioLey1886() {
        return beneficiarioLey1886;
    }

    @XmlElement(name = "montoDescuentoLey1886", nillable = true)
    public BigDecimal getMontoDescuentoLey1886() {
        return montoDescuentoLey1886!= null ? montoDescuentoLey1886.setScale(2, BigDecimal.ROUND_HALF_EVEN):null;
    }

    @XmlElement(name = "montoDescuentoTarifaDignidad", nillable = true)
    public BigDecimal getMontoDescuentoTarifaDignidad() {
        return montoDescuentoTarifaDignidad != null ?montoDescuentoTarifaDignidad.setScale(2, BigDecimal.ROUND_HALF_EVEN):null;
    }

    @XmlElement(name = "tasaAseo", nillable = true)
    public BigDecimal getTasaAseo() {
        return tasaAseo != null ? tasaAseo.setScale(2, BigDecimal.ROUND_HALF_EVEN):null;
    }

    @XmlElement(name = "tasaAlumbrado", nillable = true)
    public BigDecimal getTasaAlumbrado() {
        return tasaAlumbrado!=null? tasaAlumbrado.setScale(2, BigDecimal.ROUND_HALF_EVEN):null;
    }

    @XmlElement(name = "ajusteNoSujetoIva", nillable = true)
    public BigDecimal getAjusteNoSujetoIva() {
        return ajusteNoSujetoIva != null?ajusteNoSujetoIva.setScale(2, BigDecimal.ROUND_HALF_EVEN): null;
    }

    @XmlElement(name = "detalleAjusteNoSujetoIva", nillable = true)
    public String getDetalleAjusteNoSujetoIva() {
        return detalleAjusteNoSujetoIva;
    }

    @XmlElement(name = "ajusteSujetoIva", nillable = true)
    public BigDecimal getAjusteSujetoIva() {
        return ajusteSujetoIva != null? ajusteSujetoIva.setScale(2, BigDecimal.ROUND_HALF_EVEN): null;
    }

    @XmlElement(name = "detalleAjusteSujetoIva", nillable = true)
    public String getDetalleAjusteSujetoIva() {
        return detalleAjusteSujetoIva;
    }

    @XmlElement(name = "otrosPagosNoSujetoIva", nillable = true)
    public BigDecimal getOtrosPagosNoSujetoIva() {
        return otrosPagosNoSujetoIva != null? otrosPagosNoSujetoIva.setScale(2, BigDecimal.ROUND_HALF_EVEN): null;
    }

    @XmlElement(name = "otrasTasas", nillable = true)
    public BigDecimal getOtrasTasas() {
        return otrasTasas != null? otrasTasas.setScale(2, BigDecimal.ROUND_HALF_EVEN): null;
    }

    @XmlElement(name = "detalleOtrosPagosNoSujetoIva", nillable = true)
    public String getDetalleOtrosPagosNoSujetoIva() {
        return detalleOtrosPagosNoSujetoIva;
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
        this.setMes( (String) header.get(XmlTags.HEADER_MES));
        if (header.containsKey(XmlTags.HEADER_GESTION)){
            this.setGestion(Integer.parseInt( (String) header.get(XmlTags.HEADER_GESTION)));
        }
        this.setCiudad( (String) header.get(XmlTags.HEADER_CIUDAD));
        this.setZona( (String) header.get(XmlTags.HEADER_ZONA));
        this.setNumeroMedidor((String) header.get(XmlTags.HEADER_NUMERO_MEDIDOR));
        this.setDomicilioCliente( (String) header.get(XmlTags.HEADER_DOMICILIO_CLIENTE));

        if(header.containsKey(XmlTags.HEADER_CONSUMO_PERIODO)){
            this.setConsumoPeriodo(new BigDecimal( (String) header.get(XmlTags.HEADER_CONSUMO_PERIODO)));
        }
        if (header.containsKey(XmlTags.HEADER_BENEFICIARIO_LEY_1886)){
            this.setBeneficiarioLey1886(Integer.parseInt( (String) header.get(XmlTags.HEADER_BENEFICIARIO_LEY_1886)));
        }
        if(header.containsKey(XmlTags.HEADER_MONTO_DESCUENTO_LEY_1886)){
            this.setMontoDescuentoLey1886(new BigDecimal( (String) header.get(XmlTags.HEADER_MONTO_DESCUENTO_LEY_1886)));
        }
        if(header.containsKey(XmlTags.HEADER_MONTO_DESCUENTO_TARIFA_DIGNIDAD)){
            this.setMontoDescuentoTarifaDignidad(new BigDecimal( (String) header.get(XmlTags.HEADER_MONTO_DESCUENTO_TARIFA_DIGNIDAD)));
        }
        if(header.containsKey(XmlTags.HEADER_TASA_ASEO)){
            this.setTasaAseo(new BigDecimal( (String) header.get(XmlTags.HEADER_TASA_ASEO)));
        }
        if(header.containsKey(XmlTags.HEADER_TASA_ALUMBRADO)){
            this.setTasaAlumbrado(new BigDecimal( (String) header.get(XmlTags.HEADER_TASA_ALUMBRADO)));
        }
        if(header.containsKey(XmlTags.HEADER_AJUSTE_NO_SUJETO_A_IVA)){
            this.setAjusteNoSujetoIva(new BigDecimal( (String) header.get(XmlTags.HEADER_AJUSTE_NO_SUJETO_A_IVA)));
        }
        if(header.containsKey(XmlTags.HEADER_DETALLE_AJUSTE_NO_SUJETO_IVA)){
            this.setDetalleAjusteNoSujetoIva((String) header.get(XmlTags.HEADER_DETALLE_AJUSTE_NO_SUJETO_IVA));
        }
        if(header.containsKey(XmlTags.HEADER_AJUSTE_SUJETO_IVA)){
            this.setAjusteSujetoIva(new BigDecimal( (String) header.get(XmlTags.HEADER_AJUSTE_SUJETO_IVA)));
        }
        if(header.containsKey(XmlTags.HEADER_DETALLE_AJUSTE_SUJETO_IVA)){
            this.setDetalleAjusteSujetoIva((String) header.get(XmlTags.HEADER_DETALLE_AJUSTE_SUJETO_IVA));
        }
        if(header.containsKey(XmlTags.HEADER_OTROS_PAGOS_NO_SUJETO_IVA)){
            this.setOtrosPagosNoSujetoIva(new BigDecimal( (String) header.get(XmlTags.HEADER_OTROS_PAGOS_NO_SUJETO_IVA)));
        }
        if(header.containsKey(XmlTags.HEADER_DETALLE_OTROS_PAGOS_NO_SUJETO_IVA)){
            this.setDetalleOtrosPagosNoSujetoIva((String) header.get(XmlTags.HEADER_DETALLE_OTROS_PAGOS_NO_SUJETO_IVA));
        }
        if (header.containsKey(XmlTags.HEADER_OTRAS_TASAS)) {
            this.setOtrasTasas(new BigDecimal((String) header.get(XmlTags.HEADER_OTRAS_TASAS)));
        }
    }
}
