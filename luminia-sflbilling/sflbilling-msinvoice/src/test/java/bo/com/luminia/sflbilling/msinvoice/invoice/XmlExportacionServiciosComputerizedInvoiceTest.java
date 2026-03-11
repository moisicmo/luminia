package bo.com.luminia.sflbilling.msinvoice.invoice;

import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlExportacionServicioCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlExportacionServiciosDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlExportacionServiciosHeader;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlExportacionServiciosComputerizedInvoiceTest {

    private XmlExportacionServicioCmpInvoice crearFactura() {
        XmlExportacionServiciosHeader cabecera = new XmlExportacionServiciosHeader();
        cabecera.setNitEmisor(456489012L);
        cabecera.setRazonSocialEmisor("PRUEBA");
        cabecera.setMunicipio("La Paz");
        cabecera.setTelefono("2457896");
        cabecera.setNumeroFactura(200);
        cabecera.setCuf("B2AFA11610013351564D658EE50D2D2A4AA6B685");
        cabecera.setCufd("F00F840D939A5512913A06FC88ADEA84");
        cabecera.setCodigoSucursal(0);
        cabecera.setDireccion("Calle Juan Pablo II #54");
        cabecera.setCodigoPuntoVenta(0);
        cabecera.setFechaEmision(XmlDateUtil.parseDateToYyyymmddThhmmssSSS("2019-07-26T11:00:12.208"));
        cabecera.setNombreRazonSocial("Pablo Mamani");
        cabecera.setCodigoTipoDocumentoIdentidad(1);
        cabecera.setNumeroDocumento("1548971");
        cabecera.setComplemento("ABC");
        cabecera.setCodigoCliente("PMamani");
        cabecera.setCodigoMetodoPago(2);
        cabecera.setNumeroTarjeta(4846000000006814L);
        cabecera.setMontoTotal(new BigDecimal(450));
        cabecera.setCodigoMoneda(2);
        cabecera.setTipoCambio(new BigDecimal(1));
        cabecera.setMontoTotalMoneda(new BigDecimal(25));
        cabecera.setLeyenda("Ley N° 453: Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        cabecera.setUsuario("pperez");
        cabecera.setCodigoDocumentoSector(28);
        cabecera.setCodigoExcepcion(0);

        cabecera.setDireccionComprador("Av La salle");
        cabecera.setLugarDestino("Santa Cruz");
        cabecera.setCodigoPais(100);
        cabecera.setInformacionAdicional("cuenta del cliente 2432432");

        XmlExportacionServiciosDetail d1 = new XmlExportacionServiciosDetail();
        d1.setActividadEconomica("620100");
        d1.setCodigoProductoSin(83141);
        d1.setCodigoProducto("JN-131231");
        d1.setDescripcion("Pension de Junio");
        d1.setCantidad(new BigDecimal(1));
        d1.setUnidadMedida(58);
        d1.setPrecioUnitario(new BigDecimal(350));
        d1.setMontoDescuento(new BigDecimal(1));
        d1.setSubTotal(new BigDecimal(350));

        List<XmlExportacionServiciosDetail> detalle = new ArrayList<>();
        detalle.add(d1);

        XmlExportacionServicioCmpInvoice invoice = null;
        try {
            invoice = new XmlExportacionServicioCmpInvoice();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }

        invoice.setCabecera(cabecera);
        invoice.setDetalle(detalle);
        return invoice;
    }

    @Test
    public  void testValidarFactura(){
        XmlExportacionServicioCmpInvoice factura = this.crearFactura();
        try {
            assertThat(factura.isValidXml(factura.generate(true))) .isTrue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerarFactura() {
        try {
            XmlExportacionServicioCmpInvoice invoice = this.crearFactura();
            String output = invoice.generate(true);
            System.out.println(output);
            assertThat(output).isNotNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
