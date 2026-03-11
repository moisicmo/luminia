package bo.gob.impuestos.sfe.invoice.xml.header;

import bo.gob.impuestos.sfe.invoice.xml.XmlDateHandler;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.XmlOptionalHeader;
import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.*;

@Setter
public abstract class XmlBaseHeaderInvoice {

    protected Long nitEmisor;
    @NotBlank
    @Size(min = 1, max = 400)
    protected String razonSocialEmisor;
    @NotBlank
    @Size(min = 1, max = 25)
    protected String municipio;
    @Size(min = 0, max = 25)
    protected String telefono;
    @NotBlank
    protected Integer numeroFactura;
    @NotBlank
    @Size(min = 0, max = 100)
    protected String cuf;
    @NotBlank
    @Size(min = 1, max = 100)
    protected String cufd;
    @NotBlank
    protected Integer codigoSucursal;
    @NotBlank
    @Size(min = 1, max = 500)
    protected String direccion;

    protected Integer codigoPuntoVenta;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonDeserialize(using = XmlDateHandler.class)
    @NotBlank
    protected Date fechaEmision;
    @NotBlank
    @Size(min = 1, max = 500)
    protected String nombreRazonSocial;
    @NotBlank
    protected Integer codigoTipoDocumentoIdentidad;
    @NotBlank
    @Size(min = 1, max = 20)
    protected String numeroDocumento;
    @Size(min = 0, max = 5)
    protected String complemento;
    @NotBlank
    @Size(min = 1, max = 100)
    protected String codigoCliente;
    @NotBlank
    protected Integer codigoMetodoPago;

    protected Long numeroTarjeta;
    @NotNull
    protected BigDecimal montoTotal;
    @NotNull
    protected BigDecimal montoTotalSujetoIva;
    @NotBlank
    protected Integer codigoMoneda;
    @NotNull
    protected BigDecimal tipoCambio;
    @NotBlank
    protected BigDecimal montoTotalMoneda;
    @NotBlank
    @Size(min = 1, max = 200)
    protected String leyenda;
    @NotBlank
    @Size(min = 1, max = 100)
    protected String usuario;
    @NotNull
    protected Integer codigoDocumentoSector;

    protected BigDecimal montoGiftCard;
    protected BigDecimal descuentoAdicional;
    protected Integer codigoExcepcion;
    protected String cafc;
    protected String businessCode;

    protected List<HashMap<String, String>> optional = new ArrayList<>();
    protected List<XmlOptionalHeader> optionalHeader = new ArrayList<>();
    protected String barcode;

    @XmlTransient
    public String getBusinessCode() {
        return this.businessCode;
    }

    @XmlTransient
    public Integer getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(Integer numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public void convertToHeaderBase(final Map<String, Object> header) {
        //this.setNitEmisor(Long.parseLong(header.get(XmlTags.HEADER_NIT_EMISOR)));
        //this.setRazonSocialEmisor((String) header.get(XmlTags.HEADER_RAZON_SOCIAL_EMISOR));
        //this.setMunicipio((String) header.get(XmlTags.HEADER_MUNICIPIO));
        //this.setTelefono((String) header.get(XmlTags.HEADER_TELEFONO));
        this.setNumeroFactura(Integer.parseInt((String) header.get(XmlTags.HEADER_NUMERO_FACTURA)));
        //this.setCuf((String) header.get(XmlTags.HEADER_CUF));
        //this.setCufd((String) header.get(XmlTags.HEADER_CUFD));
        //this.setCodigoSucursal(Integer.parseInt(header.get(XmlTags.HEADER_CODIGO_SUCURSAL)));
        //this.setDireccion((String) header.get(XmlTags.HEADER_DIRECCION));
        //if (header.containsKey(XmlTags.HEADER_CODIGO_PUNTO_VENTA)) {
        //    this.setCodigoPuntoVenta(Integer.parseInt(header.get(XmlTags.HEADER_CODIGO_PUNTO_VENTA)));
        //}
        //this.setFechaEmision(XmlDateUtil.parseDateToYyyymmddThhmmssSSS((String) header.get(XmlTags.HEADER_FECHA_EMISION)));
        this.setNombreRazonSocial((String) header.get(XmlTags.HEADER_NOMBRE_RAZON_SOCIAL));
        this.setCodigoTipoDocumentoIdentidad(Integer.parseInt((String) header.get(XmlTags.HEADER_CODIGO_TIPO_DOCUMENTO_IDENTIDAD)));
        this.setNumeroDocumento((String) header.get(XmlTags.HEADER_NUMERO_DOCUMENTO));
        if (header.containsKey(XmlTags.HEADER_COMPLEMENTO)) {
            this.setComplemento((String) header.get(XmlTags.HEADER_COMPLEMENTO));
        }
        this.setCodigoCliente((String) header.get(XmlTags.HEADER_CODIGO_CLIENTE));
        if (header.containsKey(XmlTags.HEADER_CODIGO_METODO_PAGO) && header.get(XmlTags.HEADER_CODIGO_METODO_PAGO) != null) {
            this.setCodigoMetodoPago(Integer.parseInt((String) header.get(XmlTags.HEADER_CODIGO_METODO_PAGO)));
        }
        if (header.containsKey(XmlTags.HEADER_NUMERO_TARJETA) && header.get(XmlTags.HEADER_NUMERO_TARJETA) != null) {
            this.setNumeroTarjeta(Long.parseLong((String) header.get(XmlTags.HEADER_NUMERO_TARJETA)));
        }

        if (header.containsKey(XmlTags.HEADER_MONTO_TOTAL)) {
            this.setMontoTotal(new BigDecimal((String) header.get(XmlTags.HEADER_MONTO_TOTAL)));
        }

        if (header.containsKey(XmlTags.HEADER_MONTO_TOTAL_SUJETO_IVA)) {
            this.setMontoTotalSujetoIva(new BigDecimal((String) header.get(XmlTags.HEADER_MONTO_TOTAL_SUJETO_IVA)));
        }
        if (header.containsKey(XmlTags.HEADER_CODIGO_MONEDA) && header.get(XmlTags.HEADER_CODIGO_MONEDA) != null) {
            this.setCodigoMoneda(Integer.parseInt((String) header.get(XmlTags.HEADER_CODIGO_MONEDA)));
        }
        if (header.containsKey(XmlTags.HEADER_TIPO_CAMBIO)) {
            this.setTipoCambio(new BigDecimal((String) header.get(XmlTags.HEADER_TIPO_CAMBIO)));
        }//TODO else

        if (header.containsKey(XmlTags.HEADER_MONTO_TOTAL_MONEDA)) {
            this.setMontoTotalMoneda(new BigDecimal((String) header.get(XmlTags.HEADER_MONTO_TOTAL_MONEDA)));
        } //TODO else

        if (header.containsKey(XmlTags.HEADER_MONTO_GIFT_CARD)) {
            this.setMontoGiftCard(new BigDecimal((String) header.get(XmlTags.HEADER_MONTO_GIFT_CARD)));
        }

        if (header.containsKey(XmlTags.HEADER_DESCUENTO_ADICIONAL)) {
            this.setDescuentoAdicional(new BigDecimal((String) header.get(XmlTags.HEADER_DESCUENTO_ADICIONAL)));
        }

        if (header.containsKey(XmlTags.HEADER_CODIGO_EXCEPCION)) {
            if (header.get(XmlTags.HEADER_CODIGO_EXCEPCION) != null) {
                this.setCodigoExcepcion(Integer.parseInt((String) header.get(XmlTags.HEADER_CODIGO_EXCEPCION)));
            }
        }

        if (header.containsKey(XmlTags.HEADER_CAFC)) {
            this.setCafc((String) header.get(XmlTags.HEADER_CAFC));
        }

        this.setLeyenda((String) header.get(XmlTags.HEADER_LEYENDA));
        this.setUsuario((String) header.get(XmlTags.HEADER_USUARIO));

        if (header.containsKey(XmlTags.HEADER_OPTIONAL)) {
            this.setOptional((List) header.get(XmlTags.HEADER_OPTIONAL));
        }
        if (header.containsKey(XmlTags.HEADER_OPTIONAL_HEADER)) {
            this.setOptionalHeader((List) header.get(XmlTags.HEADER_OPTIONAL_HEADER));
        }
        if (header.containsKey(XmlTags.HEADER_BARCODE)) {
            this.setBarcode((String) header.get(XmlTags.HEADER_BARCODE));
        }
        if (header.containsKey(XmlTags.HEADER_COMPLEMENTO)) {
            this.setComplemento((String) header.get(XmlTags.HEADER_COMPLEMENTO));
        }
    }

    public abstract void convert(Map<String, Object> header);

    @XmlTransient
    public Long getNitEmisor() {
        return nitEmisor;
    }

    @XmlTransient
    public String getRazonSocialEmisor() {
        return razonSocialEmisor;
    }

    @XmlTransient
    public String getMunicipio() {
        return municipio;
    }

    @XmlTransient
    public String getTelefono() {
        return telefono;
    }

    @XmlTransient
    public String getCuf() {
        return cuf;
    }

    @XmlTransient
    public String getCufd() {
        return cufd;
    }

    @XmlTransient
    public Integer getCodigoSucursal() {
        return codigoSucursal;
    }

    @XmlTransient
    public String getDireccion() {
        return direccion;
    }

    @XmlTransient
    public Integer getCodigoPuntoVenta() {
        return codigoPuntoVenta;
    }

    @XmlTransient
    public Date getFechaEmision() {
        return fechaEmision;
    }

    @XmlTransient
    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    @XmlTransient
    public Integer getCodigoTipoDocumentoIdentidad() {
        return codigoTipoDocumentoIdentidad;
    }

    @XmlTransient
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    @XmlTransient
    public String getComplemento() {
        return complemento;
    }

    @XmlTransient
    public String getCodigoCliente() {
        return codigoCliente;
    }

    @XmlTransient
    public Integer getCodigoMetodoPago() {
        return codigoMetodoPago;
    }

    @XmlTransient
    public Long getNumeroTarjeta() {
        return numeroTarjeta;
    }

    @XmlTransient
    public BigDecimal getMontoTotal() {
        return montoTotal != null ? montoTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlTransient
    public BigDecimal getMontoTotalSujetoIva() {
        return montoTotalSujetoIva != null ? montoTotalSujetoIva.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlTransient
    public Integer getCodigoMoneda() {
        return codigoMoneda;
    }

    @XmlTransient
    public BigDecimal getTipoCambio() {
        return tipoCambio != null ? tipoCambio.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlTransient
    public BigDecimal getMontoTotalMoneda() {
        return montoTotalMoneda != null ? montoTotalMoneda.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlTransient
    public String getLeyenda() {
        return leyenda;
    }

    @XmlTransient
    public String getUsuario() {
        return usuario;
    }

    @XmlTransient
    public Integer getCodigoDocumentoSector() {
        return codigoDocumentoSector;
    }

    @XmlTransient
    public BigDecimal getMontoGiftCard() {
        return montoGiftCard != null ? montoGiftCard.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlTransient
    public BigDecimal getDescuentoAdicional() {
        return descuentoAdicional != null ? descuentoAdicional.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlTransient
    public Integer getCodigoExcepcion() {
        return codigoExcepcion;
    }

    @XmlTransient
    public String getCafc() {
        return cafc;
    }

    @XmlTransient
    public List getOptional() {
        return optional;
    }

    @XmlTransient
    public List getOptionalHeader() {
        return optionalHeader;
    }

    @XmlTransient
    public String getBarcode() {
        return this.barcode;
    }
}
