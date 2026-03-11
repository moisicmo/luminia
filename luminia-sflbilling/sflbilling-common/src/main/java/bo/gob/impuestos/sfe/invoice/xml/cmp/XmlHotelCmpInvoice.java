package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlHotelesDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlHotelesHeader;

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
@XmlRootElement(name = "facturaComputarizadaHotel")
public class XmlHotelCmpInvoice
    extends XmlBaseInvoice<XmlHotelesHeader, XmlHotelesDetail> {

    public XmlHotelCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_HOTELES);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlHotelesHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlHotelesDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlHotelesHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlHotelesDetail detail = new XmlHotelesDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
