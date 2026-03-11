package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlAlquilerBienesInmueblesDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlAlquilerBienesInmueblesHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "facturaElectronicaAlquilerBienInmueble")
public class XmlAlquilerBienesInmueblesEltrInvoice
    extends XmlBaseInvoice<XmlAlquilerBienesInmueblesHeader, XmlAlquilerBienesInmueblesDetail> {

    public XmlAlquilerBienesInmueblesEltrInvoice()throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_ALQUILER_BIENES_INMUEBLES);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlAlquilerBienesInmueblesHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlAlquilerBienesInmueblesDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlAlquilerBienesInmueblesHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlAlquilerBienesInmueblesDetail detail = new XmlAlquilerBienesInmueblesDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
