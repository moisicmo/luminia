package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlAlimentariaAbastecimientoDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlAlimentariaAbastecimientoHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "facturaComputarizadaSeguridadAlimentaria")
public class XmlAlimentariaAbastecimientoCmpInvoice
    extends XmlBaseInvoice<XmlAlimentariaAbastecimientoHeader, XmlAlimentariaAbastecimientoDetail> {

    public XmlAlimentariaAbastecimientoCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_SEGURIDAD_ALIMENTARIA);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlAlimentariaAbastecimientoHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlAlimentariaAbastecimientoDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlAlimentariaAbastecimientoHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlAlimentariaAbastecimientoDetail detail = new XmlAlimentariaAbastecimientoDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
