package bo.gob.impuestos.sfe.invoice.xml.detail;

import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import lombok.Setter;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Map;

@Setter
@XmlType(propOrder = {"actividadEconomica", "codigoProductoSin", "codigoProducto","descripcion",
    "codigoTipoHabitacion",
    "cantidad", "unidadMedida",
    "precioUnitario", "montoDescuento", "subTotal",
    "detalleHuespedes"
})
public class XmlTuristicoHospedajeDetail extends XmlBaseDetailInvoice{

    private Integer codigoTipoHabitacion;
    @Size(min = 0, max = 5000)
    private String detalleHuespedes;

    @XmlElement(name = "codigoTipoHabitacion", nillable = true)
    public Integer getCodigoTipoHabitacion() {
        return codigoTipoHabitacion;
    }

    @XmlElement(name = "detalleHuespedes" , nillable = true)
    public String getDetalleHuespedes() {
        return detalleHuespedes;
    }

    //

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

    @XmlElement(name = "cantidad", required = true)
    public BigDecimal getCantidad() {
        return cantidad!= null? cantidad.setScale(0, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "unidadMedida", required = true)
    public Integer getUnidadMedida() {
        return unidadMedida;
    }

    @XmlElement(name = "precioUnitario", required = true)
    public BigDecimal getPrecioUnitario() {
        return precioUnitario!= null ? precioUnitario.setScale(2,BigDecimal.ROUND_HALF_EVEN) : null;
    }
    @XmlElement(name = "montoDescuento", nillable = true)
    public BigDecimal getMontoDescuento() {
        return montoDescuento!= null? montoDescuento.setScale(2, BigDecimal.ROUND_HALF_EVEN) : null;
    }
    @XmlElement(name = "subTotal")
    public BigDecimal getSubTotal() {
        return subTotal != null? subTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN): null;
    }

    @Override
    public void convert(Map<String, String> details) {
        this.convertToDetailBase(details);
        this.setDetalleHuespedes(details.get(XmlTags.DETAIL_DETALLE_HUESPEDES));
        if(details.containsKey(XmlTags.DETAIL_CODIGO_TIPO_HABITACION)){
            this.setCodigoTipoHabitacion(Integer.parseInt(details.get(XmlTags.DETAIL_CODIGO_TIPO_HABITACION)));
        }
    }
}
