package bo.com.luminia.sflbilling.msinvoice.invoice;

import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlEntidadesFinancierasCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlEntidadesFinancierasDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlEntidadesFinancierasHeader;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlEntidadesFinancierasComputerizedInvoiceTest {

    private XmlEntidadesFinancierasCmpInvoice crearFactura() {
        XmlEntidadesFinancierasHeader cabecera = new XmlEntidadesFinancierasHeader();
        cabecera.setNitEmisor(456489012L);
        cabecera.setRazonSocialEmisor("PRUEBA");
        cabecera.setMunicipio("La Paz");
        cabecera.setTelefono("2457896");
        cabecera.setNumeroFactura(200);
        cabecera.setCuf("B2AFA11610013351564D658EE50D2D2A4AA6B685");
        cabecera.setCufd("F00F840D939A5512913A06FC88ADEA84");
        cabecera.setCodigoSucursal(0);
        cabecera.setDireccion("Calle Juan Pablo II #54");
        //cabecera.setCodigoPuntoVenta(0);
        cabecera.setFechaEmision(XmlDateUtil.parseDateToYyyymmddThhmmssSSS("2019-07-26T11:00:12.208"));
        cabecera.setNombreRazonSocial("Pablo Mamani");
        cabecera.setCodigoTipoDocumentoIdentidad(1);
        cabecera.setNumeroDocumento("1548971");
        cabecera.setComplemento("ABC");
        cabecera.setCodigoCliente("PMamani");
        cabecera.setCodigoMetodoPago(2);
        //cabecera.setNumeroTarjeta();
        cabecera.setMontoTotal(new BigDecimal(450));
        cabecera.setMontoTotalArrendamientoFinanciero(new BigDecimal(100));
        cabecera.setMontoTotalSujetoIva(new BigDecimal(25));
        cabecera.setCodigoMoneda(1);
        cabecera.setTipoCambio(new BigDecimal(1));
        cabecera.setMontoTotalMoneda(new BigDecimal(25));
        cabecera.setLeyenda("Ley N° 453: Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        cabecera.setUsuario("pperez");
        cabecera.setCodigoDocumentoSector(15);
        cabecera.setCodigoExcepcion(0);
        cabecera.setDescuentoAdicional(new BigDecimal(1));

        XmlEntidadesFinancierasDetail d1 = new XmlEntidadesFinancierasDetail();
        d1.setActividadEconomica("620100");
        d1.setCodigoProductoSin(83141);
        d1.setCodigoProducto("JN-131231");
        d1.setDescripcion("Pension de Junio");
        d1.setCantidad(new BigDecimal(1));
        d1.setUnidadMedida(58);
        d1.setPrecioUnitario(new BigDecimal(350));
        //d1.setMontoDescuento();
        d1.setSubTotal(new BigDecimal(350));
        //d1setNumeroSerie()
        //d1.setNumeroImei();

        List<XmlEntidadesFinancierasDetail> detalle = new ArrayList<>();
        detalle.add(d1);

        XmlEntidadesFinancierasCmpInvoice invoice = null;
        try {
            invoice = new XmlEntidadesFinancierasCmpInvoice();
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
        XmlEntidadesFinancierasCmpInvoice factura = this.crearFactura();
        try {
            assertThat(factura.isValidXml(factura.generate(true))) .isTrue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerarFactura() {
        try {
            XmlEntidadesFinancierasCmpInvoice invoice = this.crearFactura();
            String output = invoice.generate(true);
            System.out.println(output);
            assertThat(output).isNotNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
