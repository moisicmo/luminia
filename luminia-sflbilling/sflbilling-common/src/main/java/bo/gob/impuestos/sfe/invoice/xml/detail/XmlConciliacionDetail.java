package bo.gob.impuestos.sfe.invoice.xml.detail;

import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Map;

@Setter
@XmlType(propOrder = {"actividadEconomica", "codigoProductoSin", "codigoProducto", "descripcion",
       "cantidad", "montoOriginal", "montoFinal", "montoConciliado"})
public class XmlConciliacionDetail extends XmlBaseDetailInvoice {

    @NotNull
    protected BigDecimal montoOriginal;
    @NotNull
    protected BigDecimal montoFinal;
    @NotNull
    protected BigDecimal montoConciliado;
    @NotNull
    protected Integer codigoDetalleTransaccion;

    @XmlElement(name = "actividadEconomica", required = true)
    public String getActividadEconomica() {
        return actividadEconomica;
    }

    @XmlElement(name = "codigoProductoSin", required = true)
    public Integer getCodigoProductoSin() {
        return codigoProductoSin;
    }

    @XmlElement(name = "codigoProducto", required = true)
    public String getCodigoProducto() {
        return codigoProducto;
    }

    @XmlElement(name = "descripcion", required = true)
    public String getDescripcion() {
        return descripcion;
    }

    @XmlElement(name = "cantidad")
    public BigDecimal getCantidad() {
        return cantidad != null ? cantidad.setScale(0, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    /*
    @XmlElement(name = "precioUnitario", required = true)
    public BigDecimal getPrecioUnitario() {
        return precioUnitario != null ? precioUnitario.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "montoDescuento", nillable = true)
    public BigDecimal getMontoDescuento() {
        return montoDescuento != null ? montoDescuento.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "subTotal")
    public BigDecimal getSubTotal() {
        return subTotal != null ? subTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }
*/
    @XmlElement(name = "montoOriginal", required = true)
    public BigDecimal getMontoOriginal() {
        return montoOriginal != null ? montoOriginal.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "montoFinal", required = true)
    public BigDecimal getMontoFinal() {
        return montoFinal != null ? montoFinal.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "montoConciliado", required = true)
    public BigDecimal getMontoConciliado() {
        return montoConciliado != null ? montoConciliado.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }


    @Override
    public void convert(Map<String, String> details) {
        this.convertToDetailBase(details);
        if (details.containsKey(XmlTags.DETAIL_MONTO_ORIGINAL)) {
            this.setMontoOriginal(new BigDecimal(details.get(XmlTags.DETAIL_MONTO_ORIGINAL)));
        }
        if (details.containsKey(XmlTags.DETAIL_MONTO_FINAL)) {
            this.setMontoFinal(new BigDecimal(details.get(XmlTags.DETAIL_MONTO_FINAL)));
        }
        if (details.containsKey(XmlTags.DETAIL_MONTO_CONCILIADO)) {
            this.setMontoConciliado(new BigDecimal(details.get(XmlTags.DETAIL_MONTO_CONCILIADO)));
        }
    }
}
