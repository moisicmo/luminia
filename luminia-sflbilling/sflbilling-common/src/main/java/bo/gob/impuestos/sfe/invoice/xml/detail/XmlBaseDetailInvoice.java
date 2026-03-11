package bo.gob.impuestos.sfe.invoice.xml.detail;

import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Map;

@Setter
public abstract class XmlBaseDetailInvoice {
    @NotBlank
    @Size(min = 1, max = 10)
    protected String actividadEconomica;
    @NotNull
    protected Integer codigoProductoSin;
    @NotBlank
    @Size(min = 1, max = 50)
    protected String codigoProducto;
    @NotBlank
    @Size(min = 1, max = 500)
    protected String descripcion;
    @NotNull
    protected BigDecimal cantidad;
    @NotNull
    protected Integer unidadMedida;
    @NotBlank
    protected BigDecimal precioUnitario;

    protected BigDecimal montoDescuento;
    @NotNull
    protected BigDecimal subTotal;

    protected  String unidadMedidaLiteral;

    @XmlTransient
    public String getUnidadMedidaLiteral(){
        return this.unidadMedidaLiteral ;
    }

    @XmlTransient
    public String getDescripcion(){
        return this.descripcion;
    }

    @XmlTransient
    public Integer getUnidadMedida() {
        return unidadMedida;
    }

    @XmlTransient
    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public void convertToDetailBase(final Map<String, String> detail) {
        //this.setActividadEconomica(detail.get(XmlTags.DETAIL_ACTIVIDAD_ECONOMICA));
        //this.setCodigoProductoSin(Integer.parseInt(detail.get(XmlTags.DETAIL_CODIGO_PRODUCTO_SIN)));
        this.setCodigoProducto(detail.get(XmlTags.DETAIL_CODIGO_PRODUCTO));

        if (detail.containsKey(XmlTags.DETAIL_DESCRIPCION)){
            this.setDescripcion(detail.get(XmlTags.DETAIL_DESCRIPCION));
        }

        if (detail.containsKey(XmlTags.DETAIL_CANTIDAD)) {
            this.setCantidad(new BigDecimal(detail.get(XmlTags.DETAIL_CANTIDAD)));
        }

        //if (detail.containsKey(XmlTags.DETAIL_UNIDAD_MEDIDA)){
        //    this.setUnidadMedida(Integer.parseInt(detail.get(XmlTags.DETAIL_UNIDAD_MEDIDA)));
        //}

        if (detail.containsKey(XmlTags.DETAIL_PRECIO_UNITARIO)) {
            this.setPrecioUnitario(new BigDecimal(detail.get(XmlTags.DETAIL_PRECIO_UNITARIO)));
        }

        if (detail.containsKey(XmlTags.DETAIL_MONTO_DESCUENTO)) {
            this.setMontoDescuento(new BigDecimal(detail.get(XmlTags.DETAIL_MONTO_DESCUENTO)));
        }

        if (detail.containsKey(XmlTags.DETAIL_SUBTOTAL)) {
            this.setSubTotal(new BigDecimal(detail.get(XmlTags.DETAIL_SUBTOTAL)));
        }
    }

    public abstract void convert(Map<String, String> details);

    @XmlTransient
    public BigDecimal getCantidad() {
        return cantidad;
    }

    @XmlTransient
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    @XmlTransient
    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    @XmlTransient
    public BigDecimal getSubTotal() {
        return subTotal;
    }
}
