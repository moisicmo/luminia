package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlComercialExportacionDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlComercialExportacionHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "facturaElectronicaComercialExportacion")
public class XmlComercialExportacionEltrInvoice
        extends XmlBaseInvoice<XmlComercialExportacionHeader, XmlComercialExportacionDetail> {

    public XmlComercialExportacionEltrInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_COMERCIAL_EXPORTACION);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlComercialExportacionHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlComercialExportacionDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlComercialExportacionHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlComercialExportacionDetail detail = new XmlComercialExportacionDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
