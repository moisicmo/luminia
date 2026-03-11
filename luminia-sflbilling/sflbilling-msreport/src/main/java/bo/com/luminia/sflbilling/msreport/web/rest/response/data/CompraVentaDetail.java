package bo.com.luminia.sflbilling.msreport.web.rest.response.data;

import java.math.BigDecimal;

public class CompraVentaDetail {
    private Integer codigoProductoSin;
    private String codigoProducto;
    private String descripcion;
    private BigDecimal cantidad;
    private Integer unidadMedida;
    private String unidadMedidaDescripcion;
    private BigDecimal precioUnitario;
    private BigDecimal montoDescuento;
    private BigDecimal subTotal;
    private String numeroSerie;
    private String numeroImei;

    public Integer getCodigoProductoSin() {
        return codigoProductoSin;
    }

    public void setCodigoProductoSin(Integer codigoProductoSin) {
        this.codigoProductoSin = codigoProductoSin;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(Integer unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getUnidadMedidaDescripcion() {
        return unidadMedidaDescripcion;
    }

    public void setUnidadMedidaDescripcion(String unidadMedidaDescripcion) {
        this.unidadMedidaDescripcion = unidadMedidaDescripcion;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(BigDecimal montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getNumeroImei() {
        return numeroImei;
    }

    public void setNumeroImei(String numeroImei) {
        this.numeroImei = numeroImei;
    }
}
