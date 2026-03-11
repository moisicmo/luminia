package bo.gob.impuestos.sfe.invoice.xml.detail;

import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import lombok.Setter;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Map;

@Setter
@XmlType(propOrder = {"actividadEconomica", "codigoProductoSin", "codigoProducto", "descripcion", "cantidad", "unidadMedida",
    "precioUnitario", "montoDescuento", "subTotal", "numeroSerie", "numeroImei"})
public class XmlTelecomunicacionesDetail extends XmlBaseDetailInvoice {

    @Size(min = 0, max = 1500)
    private String numeroSerie;

    @Size(min = 0, max = 1500)
    private String numeroImei;

    @XmlElement(name = "numeroSerie", nillable = true)
    public String getNumeroSerie() {
        return numeroSerie;
    }

    @XmlElement(name = "numeroImei", nillable = true)
    public String getNumeroImei() {
        return numeroImei;
    }

    //
    @XmlElement(name = "actividadEconomica")
    public String getActividadEconomica() {
        return actividadEconomica;
    }

    @XmlElement(name = "codigoProductoSin")
    public Integer getCodigoProductoSin() {
        return codigoProductoSin;
    }

    @XmlElement(name = "codigoProducto")
    public String getCodigoProducto() {
        return codigoProducto;
    }

    @XmlElement(name = "descripcion")
    public String getDescripcion() {
        return descripcion;
    }

    @XmlElement(name = "cantidad")
    public BigDecimal getCantidad() {
        return cantidad != null ? cantidad.setScale(0, BigDecimal.ROUND_HALF_EVEN) : null;
    }

    @XmlElement(name = "unidadMedida", required = true)
    public Integer getUnidadMedida() {
        return unidadMedida;
    }

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

    @Override
    public void convert(Map<String, String> details) {
        this.convertToDetailBase(details);
        this.setNumeroImei(details.get(XmlTags.DETAIL_NUMERO_IMEI));
        this.setNumeroSerie(details.get(XmlTags.DETAIL_NUMERO_SERIE));
    }
}
