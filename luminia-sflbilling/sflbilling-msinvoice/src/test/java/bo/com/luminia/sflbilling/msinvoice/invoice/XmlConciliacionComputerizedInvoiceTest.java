package bo.com.luminia.sflbilling.msinvoice.invoice;

import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlConciliacionCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlConciliacionDetail;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlConciliacionOriginalDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlConciliacionHeader;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlConciliacionComputerizedInvoiceTest {

    private XmlConciliacionCmpInvoice crearFactura() {
        XmlConciliacionHeader cabecera = new XmlConciliacionHeader();
        cabecera.setNitEmisor(456489012L);
        cabecera.setRazonSocialEmisor("PRUEBA");
        cabecera.setMunicipio("La Paz");
        cabecera.setTelefono("2457896");
        cabecera.setNumeroNotaConciliacion(4343L);
        cabecera.setNumeroFactura(200);
        cabecera.setCuf("B2AFA11610013351564D658EE50D2D2A4AA6B685");
        cabecera.setCufd("F00F840D939A5512913A06FC88ADEA84");
        cabecera.setCodigoSucursal(0);
        cabecera.setDireccion("Calle Juan Pablo II #54");
        cabecera.setNumeroAutorizacionCuf("324234dsfsdf");
        cabecera.setFechaEmision(XmlDateUtil.parseDateToYyyymmddThhmmssSSS("2019-07-26T11:00:12.208"));
        cabecera.setFechaEmisionFactura(XmlDateUtil.parseDateToYyyymmddThhmmssSSS("2019-07-26T11:00:12.208"));
        cabecera.setNombreRazonSocial("Pablo Mamani");
        cabecera.setCodigoTipoDocumentoIdentidad(1);
        cabecera.setNumeroDocumento("1548971");
        cabecera.setComplemento("ABC");
        cabecera.setCodigoCliente("PMamani");
        cabecera.setMontoTotalOriginal(new BigDecimal(3500));
        cabecera.setMontoTotalConciliado(new BigDecimal(100));
        cabecera.setCreditoFiscalIva(new BigDecimal(23));
        cabecera.setDebitoFiscalIva(new BigDecimal(22));

        cabecera.setCodigoExcepcion(0);
        cabecera.setLeyenda("Ley N° 453: Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        cabecera.setUsuario("pperez");
        cabecera.setCodigoDocumentoSector(29);

        XmlConciliacionOriginalDetail d1 = new XmlConciliacionOriginalDetail();
        d1.setActividadEconomica("620100");
        d1.setCodigoProductoSin(83141);
        d1.setCodigoProducto("JN-131231");
        d1.setDescripcion("Pension de Junio");
        d1.setCantidad(new BigDecimal(10));
        d1.setUnidadMedida(58);
        d1.setPrecioUnitario(new BigDecimal(2.5));
        //d1.setMontoDescuento();
        d1.setSubTotal(new BigDecimal(25));

        List<XmlConciliacionOriginalDetail> detalle = new ArrayList<>();
        detalle.add(d1);

        XmlConciliacionDetail d2 = new XmlConciliacionDetail();
        d2.setActividadEconomica("620100");
        d2.setCodigoProductoSin(83141);
        d2.setCodigoProducto("JN-131231");
        d2.setDescripcion("Pension de Junio");
        d2.setMontoOriginal(new BigDecimal(20.5));
        d2.setMontoFinal(new BigDecimal(2.5));
        d2.setMontoConciliado(new BigDecimal(25));

        List<XmlConciliacionDetail> detalleC = new ArrayList<>();
        detalleC.add(d2);


        XmlConciliacionCmpInvoice invoice = null;
        try {
            invoice = new XmlConciliacionCmpInvoice();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        invoice.setCabecera(cabecera);
        invoice.setDetalle(detalle);
        invoice.setDetalleConciliacion(detalleC);
        return invoice;
    }

    @Test
    public void testValidarFactura() {
        XmlConciliacionCmpInvoice factura = this.crearFactura();
        try {
            assertThat(factura.isValidXml(factura.generate(true))).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerarFactura() {
        try {
            XmlConciliacionCmpInvoice invoice = this.crearFactura();
            String output = invoice.generate(true);
            System.out.println(output);
            assertThat(output).isNotNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
