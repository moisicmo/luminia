package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlExportacionServiciosDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlExportacionServiciosHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "facturaComputarizadaComercialExportacionServicio")
public class XmlExportacionServicioCmpInvoice
    extends XmlBaseInvoice<XmlExportacionServiciosHeader, XmlExportacionServiciosDetail> {

    public XmlExportacionServicioCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_EXPORTACION_SERVICIOS );
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlExportacionServiciosHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlExportacionServiciosDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlExportacionServiciosHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlExportacionServiciosDetail detail = new XmlExportacionServiciosDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
