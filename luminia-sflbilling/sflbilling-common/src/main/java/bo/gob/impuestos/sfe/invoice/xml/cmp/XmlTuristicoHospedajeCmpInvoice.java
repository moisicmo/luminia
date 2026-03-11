package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlTuristicoHospedajeDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlTuristicoHospedajeHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * No debe settear el valor de MontoTotalSujetoIva
 */
@XmlType
@XmlRootElement(name = "facturaComputarizadaServicioTuristicoHospedaje")
public class XmlTuristicoHospedajeCmpInvoice
    extends XmlBaseInvoice<XmlTuristicoHospedajeHeader, XmlTuristicoHospedajeDetail> {

    public XmlTuristicoHospedajeCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_TURISTICO_HSOPEDAJE);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlTuristicoHospedajeHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlTuristicoHospedajeDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlTuristicoHospedajeHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlTuristicoHospedajeDetail detail = new XmlTuristicoHospedajeDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
