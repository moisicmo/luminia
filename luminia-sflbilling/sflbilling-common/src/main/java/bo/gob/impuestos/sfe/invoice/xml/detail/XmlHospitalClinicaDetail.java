package bo.gob.impuestos.sfe.invoice.xml.detail;

import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Map;

@Setter
@XmlType(propOrder = {"actividadEconomica", "codigoProductoSin", "codigoProducto", "descripcion",
    "especialidad", "especialidadDetalle","nroQuirofanoSalaOperaciones","especialidadMedico","nombreApellidoMedico",
    "nitDocumentoMedico","nroMatriculaMedico","nroFacturaMedico",
    "cantidad", "unidadMedida",
    "precioUnitario", "montoDescuento", "subTotal"})
public class XmlHospitalClinicaDetail extends XmlBaseDetailInvoice{

    @Size(max = 500)
    private String especialidad ;
    @Size(max = 500)
    private String especialidadDetalle ;
    @NotNull
    private Integer nroQuirofanoSalaOperaciones;
    @Size(max = 500)
    private String especialidadMedico;
    @NotNull
    @Size(min = 1, max = 500)
    private String nombreApellidoMedico;
    @NotNull
    private Long nitDocumentoMedico;
    @Size(max = 50)
    private String nroMatriculaMedico;
    private Long nroFacturaMedico ;

    @XmlElement( name = "especialidad")
    public String getEspecialidad() {
        return especialidad;
    }
    @XmlElement( name = "especialidadDetalle")
    public String getEspecialidadDetalle() {
        return especialidadDetalle;
    }
    @XmlElement(name = "nroQuirofanoSalaOperaciones")
    public Integer getNroQuirofanoSalaOperaciones() {
        return nroQuirofanoSalaOperaciones;
    }
    @XmlElement(name = "especialidadMedico", nillable = true)
    public String getEspecialidadMedico() {
        return especialidadMedico;
    }
    @XmlElement(name = "nombreApellidoMedico")
    public String getNombreApellidoMedico() {
        return nombreApellidoMedico;
    }
    @XmlElement(name = "nitDocumentoMedico")
    public Long getNitDocumentoMedico() {
        return nitDocumentoMedico;
    }
    @XmlElement(name = "nroMatriculaMedico", nillable = true)
    public String getNroMatriculaMedico() {
        return nroMatriculaMedico;
    }
    @XmlElement(name = "nroFacturaMedico", nillable = true)
    public Long getNroFacturaMedico() {
        return nroFacturaMedico;
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
        this.setEspecialidad(details.get(XmlTags.DETAIL_ESPECIALIDAD));
        this.setEspecialidadDetalle(details.get(XmlTags.DETAIL_ESPECIALIDAD_DETALLE));
        if(details.containsKey(XmlTags.DETAIL_NRO_QUIROFANO_SALA_OPERACIONES)) {
            this.setNroQuirofanoSalaOperaciones(Integer.parseInt(details.get(XmlTags.DETAIL_NRO_QUIROFANO_SALA_OPERACIONES)));
        }
        this.setEspecialidadMedico(details.get(XmlTags.DETAIL_ESPECIALIDAD_MEDICO));
        this.setNombreApellidoMedico(details.get(XmlTags.DETAIL_NOMBRE_APELLIDO_MEDICO));
        if(details.containsKey(XmlTags.DETAIL_NIT_DOCUMENTO_MEDICO)){
            this.setNitDocumentoMedico(Long.parseLong(details.get(XmlTags.DETAIL_NIT_DOCUMENTO_MEDICO)));
        }
        this.setNroMatriculaMedico(details.get(XmlTags.DETAIL_NRO_MATRICULA_MEDICO));
        if(details.containsKey(XmlTags.DETAIL_NRO_FACTURA_MEDICO)){
            this.setNroFacturaMedico(Long.parseLong(details.get(XmlTags.DETAIL_NRO_FACTURA_MEDICO)));
        }
    }
}
