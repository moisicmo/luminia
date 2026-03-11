package bo.com.luminia.sflbilling.msreport.web.rest.response.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CompraVentaHeader {
    private Long nitEmisor;
    private String razonSocialEmisor;
    private String municipio;
    private String telefono;
    private Integer numeroFactura;
    private String cuf;
    private Integer codigoSucursal;
    private String direccion;
    private Integer codigoPuntoVenta;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date fechaEmision;
    private String nombreRazonSocial;
    private Integer codigoTipoDocumentoIdentidad;
    private String codigoTipoDocumentoIdentidadDescripcion;
    private String numeroDocumento;
    private String complemento;
    private String codigoCliente;
    private Integer codigoMetodoPago;
    private String codigoMetodoPagoDescripcion;
    private Long numeroTarjeta;
    private BigDecimal montoTotal;
    private BigDecimal montoTotalSujetoIva;
    private Integer codigoMoneda;
    private String codigoMonedaDescripcion;
    private BigDecimal tipoCambio;
    private BigDecimal montoTotalMoneda;
    private String leyenda1;
    private String leyenda2;
    private String leyenda3;
    private String usuario;
    private Integer tipoEmision;
    private String tipoEmisionDescripcion;
    private Integer codigoDocumentoSector;
    private String codigoDocumentoSectorDescripcion;
    private Integer tipoDocumentoFiscal;
    private String tipoDocumentoFiscalDescripcion;
    private BigDecimal montoGiftCard;
    private BigDecimal descuentoAdicional;
    private String qr;
    private List<HashMap<String, String>> optional = new ArrayList<>();

    public Long getNitEmisor() {
        return nitEmisor;
    }

    public void setNitEmisor(Long nitEmisor) {
        this.nitEmisor = nitEmisor;
    }

    public String getRazonSocialEmisor() {
        return razonSocialEmisor;
    }

    public void setRazonSocialEmisor(String razonSocialEmisor) {
        this.razonSocialEmisor = razonSocialEmisor;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(Integer numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getCuf() {
        return cuf;
    }

    public void setCuf(String cuf) {
        this.cuf = cuf;
    }

    public Integer getCodigoSucursal() {
        return codigoSucursal;
    }

    public void setCodigoSucursal(Integer codigoSucursal) {
        this.codigoSucursal = codigoSucursal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getCodigoPuntoVenta() {
        return codigoPuntoVenta;
    }

    public void setCodigoPuntoVenta(Integer codigoPuntoVenta) {
        this.codigoPuntoVenta = codigoPuntoVenta;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public Integer getCodigoTipoDocumentoIdentidad() {
        return codigoTipoDocumentoIdentidad;
    }

    public void setCodigoTipoDocumentoIdentidad(Integer codigoTipoDocumentoIdentidad) {
        this.codigoTipoDocumentoIdentidad = codigoTipoDocumentoIdentidad;
    }

    public String getCodigoTipoDocumentoIdentidadDescripcion() {
        return codigoTipoDocumentoIdentidadDescripcion;
    }

    public void setCodigoTipoDocumentoIdentidadDescripcion(String codigoTipoDocumentoIdentidadDescripcion) {
        this.codigoTipoDocumentoIdentidadDescripcion = codigoTipoDocumentoIdentidadDescripcion;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Integer getCodigoMetodoPago() {
        return codigoMetodoPago;
    }

    public void setCodigoMetodoPago(Integer codigoMetodoPago) {
        this.codigoMetodoPago = codigoMetodoPago;
    }

    public String getCodigoMetodoPagoDescripcion() {
        return codigoMetodoPagoDescripcion;
    }

    public void setCodigoMetodoPagoDescripcion(String codigoMetodoPagoDescripcion) {
        this.codigoMetodoPagoDescripcion = codigoMetodoPagoDescripcion;
    }

    public Long getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(Long numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoTotalSujetoIva() {
        return montoTotalSujetoIva;
    }

    public void setMontoTotalSujetoIva(BigDecimal montoTotalSujetoIva) {
        this.montoTotalSujetoIva = montoTotalSujetoIva;
    }

    public Integer getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(Integer codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    public String getCodigoMonedaDescripcion() {
        return codigoMonedaDescripcion;
    }

    public void setCodigoMonedaDescripcion(String codigoMonedaDescripcion) {
        this.codigoMonedaDescripcion = codigoMonedaDescripcion;
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public BigDecimal getMontoTotalMoneda() {
        return montoTotalMoneda;
    }

    public void setMontoTotalMoneda(BigDecimal montoTotalMoneda) {
        this.montoTotalMoneda = montoTotalMoneda;
    }

    public String getLeyenda1() {
        return leyenda1;
    }

    public void setLeyenda1(String leyenda1) {
        this.leyenda1 = leyenda1;
    }

    public String getLeyenda2() {
        return leyenda2;
    }

    public void setLeyenda2(String leyenda2) {
        this.leyenda2 = leyenda2;
    }

    public String getLeyenda3() {
        return leyenda3;
    }

    public void setLeyenda3(String leyenda3) {
        this.leyenda3 = leyenda3;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getTipoEmision() {
        return tipoEmision;
    }

    public void setTipoEmision(Integer tipoEmision) {
        this.tipoEmision = tipoEmision;
    }

    public String getTipoEmisionDescripcion() {
        return tipoEmisionDescripcion;
    }

    public void setTipoEmisionDescripcion(String tipoEmisionDescripcion) {
        this.tipoEmisionDescripcion = tipoEmisionDescripcion;
    }

    public Integer getCodigoDocumentoSector() {
        return codigoDocumentoSector;
    }

    public void setCodigoDocumentoSector(Integer codigoDocumentoSector) {
        this.codigoDocumentoSector = codigoDocumentoSector;
    }

    public String getCodigoDocumentoSectorDescripcion() {
        return codigoDocumentoSectorDescripcion;
    }

    public void setCodigoDocumentoSectorDescripcion(String codigoDocumentoSectorDescripcion) {
        this.codigoDocumentoSectorDescripcion = codigoDocumentoSectorDescripcion;
    }

    public Integer getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(Integer tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }

    public String getTipoDocumentoFiscalDescripcion() {
        return tipoDocumentoFiscalDescripcion;
    }

    public void setTipoDocumentoFiscalDescripcion(String tipoDocumentoFiscalDescripcion) {
        this.tipoDocumentoFiscalDescripcion = tipoDocumentoFiscalDescripcion;
    }

    public BigDecimal getMontoGiftCard() {
        return montoGiftCard;
    }

    public void setMontoGiftCard(BigDecimal montoGiftCard) {
        this.montoGiftCard = montoGiftCard;
    }

    public BigDecimal getDescuentoAdicional() {
        return descuentoAdicional;
    }

    public void setDescuentoAdicional(BigDecimal descuentoAdicional) {
        this.descuentoAdicional = descuentoAdicional;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public List<HashMap<String, String>> getOptional() {
        return optional;
    }

    public void setOptional(List<HashMap<String, String>> optional) {
        this.optional = optional;
    }
}
