package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlCompraVentaDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlCompraVentaHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "facturaComputarizadaCompraVenta")
public class XmlCompraVentaCmpInvoice
    extends XmlBaseInvoice<XmlCompraVentaHeader, XmlCompraVentaDetail> {

    public XmlCompraVentaCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_COMPRA_VENTA);
        this.noNamespaceSchemaLocation = XmlNoNamespace.SCHEMA_LOCATION_CMP_COMPRA_VENTA ;
    }

    @XmlElement (name = "detalle")
    public List<XmlCompraVentaDetail> getDetalle() {
        return this.detalle ;
    }

    @XmlElement (name = "cabecera")
    public XmlCompraVentaHeader getCabecera(){
        return this.cabecera ;
    }

    public void convert(Map<String, Object> fromHeader, List<Map<String,String>>fromDetail){
        this.cabecera = new XmlCompraVentaHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlCompraVentaDetail detail = new XmlCompraVentaDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }

}
