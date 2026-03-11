package bo.com.luminia.sflbilling.msreport.siat.text.invoices;

import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.invoices.PdfComercialExportacionGastos;
import bo.com.luminia.sflbilling.msreport.siat.text.TextInvoice;
import bo.com.luminia.sflbilling.msreport.siat.text.TextInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates.TextComercialExportacionTemplate;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlComercialExportacionDetail;
import bo.gob.impuestos.sfe.invoice.xml.eltr.XmlComercialExportacionEltrInvoice;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextEltrComercialExportacionInvoice extends TextInvoice<XmlComercialExportacionEltrInvoice> {

    private String footer_1, footer_2, footer_3, detail_footer;
    private ArrayList<PdfComercialExportacionGastos> costosNacionales, costosInternacionales;

    public TextEltrComercialExportacionInvoice(XmlComercialExportacionEltrInvoice invoice, String paperType) {
        super(invoice, paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
        if (paperType == TextInvoice.PAPER_A4) {
            if (invoice.getCabecera().getDireccion().length() > MAX_DIRECCION_LENGTH) {
                this.header = TextComercialExportacionTemplate.HEADER_A4_2;
            }else {
                this.header = TextComercialExportacionTemplate.HEADER_A4;
            }
            this.details = TextComercialExportacionTemplate.DETAILS_A4;
            this.footer_1 = TextComercialExportacionTemplate.FOOTER_A4_1;
            this.footer_2 = TextComercialExportacionTemplate.FOOTER_A4_2;
            this.footer_3 = TextComercialExportacionTemplate.FOOTER_A4_3;
            this.detail_footer = TextComercialExportacionTemplate.DETAIL_FOOTER_A4;
        } else {
            this.header = TextComercialExportacionTemplate.HEADER_POS;
            this.details = TextComercialExportacionTemplate.DETAILS_POS;
            this.footer_1 = TextComercialExportacionTemplate.FOOTER_POS_1;
            this.footer_2 = TextComercialExportacionTemplate.FOOTER_POS_2;
            this.footer_3 = TextComercialExportacionTemplate.FOOTER_POS_3;
            this.detail_footer = TextComercialExportacionTemplate.DETAIL_FOOTER_POS;
        }
    }

    @Override
    public String[] generate() {
        if (paperType == TextInvoice.PAPER_A4) {
            return
                concatWithStream(
                    concatWithStream(
                        concatWithStream(
                            concatWithStream(
                                concatWithStream(
                                    concatWithStream(
                                        generateHeaderA4(), generateDetailsA4(0)),
                                    generateFooterA4()),
                                generateDetailA4_2(convertoJsonToGastos(invoice.getCabecera().getCostosGastosNacionales()))
                            ), generateFooterA4_2()),
                        generateDetailA4_2(convertoJsonToGastos(invoice.getCabecera().getCostosGastosInternacionales()))
                    ),
                    generateFooterA4_3()
                )
                ;
        } else {
            return concatWithStream(
                concatWithStream(
                    concatWithStream(
                        concatWithStream(
                            concatWithStream(
                                concatWithStream(
                                    generateHeaderPOS(), generateDetailsPOS(0)),
                                generateFooterPOS()),
                            generateDetailPOS_2(convertoJsonToGastos(invoice.getCabecera().getCostosGastosNacionales()))
                        ), generateFooterPOS_2()),
                    generateDetailPOS_2(convertoJsonToGastos(invoice.getCabecera().getCostosGastosInternacionales()))
                ),
                generateFooterPOS_3()
            );
        }
    }

    @Override
    protected String[] generateHeaderA4() {

        parameters.put(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR, invoice.getCabecera().getRazonSocialEmisor());
        parameters.put(TextInvoiceParameters.HDR_DIRECCION, invoice.getCabecera().getDireccion());
        parameters.put(TextInvoiceParameters.HDR_TELEFONO, invoice.getCabecera().getTelefono());
        parameters.put(TextInvoiceParameters.HDR_MUNICIPIO, invoice.getCabecera().getMunicipio());
        parameters.put(TextInvoiceParameters.HDR_NIT, invoice.getCabecera().getNitEmisor().toString());
        parameters.put(TextInvoiceParameters.HDR_FACTURA, invoice.getCabecera().getNumeroFactura().toString());
        parameters.put(TextInvoiceParameters.HDR_CUF, invoice.getCabecera().getCuf());
        parameters.put(TextInvoiceParameters.HDR_ACTIVIDAD, this.getActividad());
        parameters.put(TextInvoiceParameters.HDR_FECHA, XmlDateUtil.convertDateToYyyymmddhhmmssSSS(invoice.getCabecera().getFechaEmision()));
        parameters.put(TextInvoiceParameters.HDR_NIT_CI_EX, invoice.getCabecera().getNumeroDocumento());
        parameters.put(TextInvoiceParameters.HDR_CMPL, invoice.getCabecera().getComplemento()!= null ? invoice.getCabecera().getComplemento() : "");
        parameters.put(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL, invoice.getCabecera().getNombreRazonSocial());
        parameters.put(TextInvoiceParameters.HDR_INCOTERM, invoice.getCabecera().getIncoterm());
        parameters.put(TextInvoiceParameters.HDR_LUGAR_DESTINO, invoice.getCabecera().getLugarDestino());
        parameters.put(TextInvoiceParameters.HDR_TIPO_CAMBIO, invoice.getCabecera().getTipoCambio().toString());
        parameters.put(TextInvoiceParameters.HDR_MONEDA_TRANSACCION, invoice.getCabecera().getCodigoMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.HDR_INCOTERM, invoice.getCabecera().getIncoterm());
        parameters.put(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR, invoice.getCabecera().getDireccionComprador());

        checkForOptional(invoice.getCabecera().getOptional());

        String header = generateHeaderA4Default();

        return split(header, "\n", LENGTH_A4, false);
    }

    @Override
    protected String[] generateDetailsA4() {
        return new String[0];
    }

    protected String[] generateDetailsA4(Integer transaccion) {
            for (XmlComercialExportacionDetail item : invoice.getDetalle()) {

                HashMap<String, String> row = new HashMap<>();
                row.put(TextInvoiceParameters.COD_PRODUCTO, item.getCodigoProducto());
                row.put(TextInvoiceParameters.CANTIDAD, item.getCantidad().toString());
                row.put(TextInvoiceParameters.PREC_UNITA, item.getPrecioUnitario().toString());
                row.put(TextInvoiceParameters.DESCRIPCION, item.getDescripcion());
                row.put(TextInvoiceParameters.DESCUENTO, item.getMontoDescuento().toString());
                row.put(TextInvoiceParameters.SUB_TOTAL, item.getSubTotal().toString());
                row.put(TextInvoiceParameters.UNIDAD_MEDIDA, this.getUnidadMedidadLiteral(item.getUnidadMedida()));

                data.add(row);
        }

        String details = printDetailsA4(transaccion);

        return split(details, "\n", LENGTH_A4, false);
    }

    protected String printDetailsA4(Integer transaccion) {
        String response = "";
        for (HashMap<String, String> item : data) {
            StringBuilder row = new StringBuilder(this.details);

            row = replace(TextInvoiceParameters.COD_PRODUCTO, item.get(TextInvoiceParameters.COD_PRODUCTO), row);
            row = replaceNumberValues(TextInvoiceParameters.CANTIDAD, item.get(TextInvoiceParameters.CANTIDAD), row, item.get(TextInvoiceParameters.CANTIDAD).length());
            row = replace(TextInvoiceParameters.DESCRIPCION, item.get(TextInvoiceParameters.DESCRIPCION), row);
            row = replaceNumberValues(TextInvoiceParameters.PREC_UNITA, item.get(TextInvoiceParameters.PREC_UNITA), row, item.get(TextInvoiceParameters.PREC_UNITA).length());
            row = replaceNumberValues(TextInvoiceParameters.DESCUENTO, item.get(TextInvoiceParameters.DESCUENTO), row, item.get(TextInvoiceParameters.DESCUENTO).length());
            row = replaceNumberValues(TextInvoiceParameters.SUB_TOTAL, item.get(TextInvoiceParameters.SUB_TOTAL), row, item.get(TextInvoiceParameters.SUB_TOTAL).length());
            row = replace(TextInvoiceParameters.UNIDAD_MEDIDA, item.get(TextInvoiceParameters.UNIDAD_MEDIDA), row);

            response += row + "\n";
        }
        return response;
    }

    @Override
    protected String[] generateFooterA4() {

        parameters.put(TextInvoiceParameters.FTR_TOTAL_DETAIL, invoice.getCabecera().getMontoDetalle().toString());
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, TextInvoice.BOLIVIANOS);
        parameters.put(TextInvoiceParameters.FTR_INCOTERM, invoice.getCabecera().getIncotermDetalle());

        String footer = printFooterA4_1();

        return split(footer, "\n", LENGTH_A4, false);
    }

    protected String[] generateFooterA4_2() {
        StringBuilder footer = new StringBuilder(this.footer_2);

        footer = replaceNumberValues(TextInvoiceParameters.FTR_SUBTOTAL_FOB,
            this.invoice.getCabecera().getTotalGastosNacionalesFob() != null ?
            this.invoice.getCabecera().getTotalGastosNacionalesFob().toString() : "0.00"
            , footer, this.invoice.getCabecera().getTotalGastosNacionalesFob() != null ?
                this.invoice.getCabecera().getTotalGastosNacionalesFob().toString().length() : "0.00".length());

        return split(footer.toString(), "\n", LENGTH_A4, false) ;
    }

    protected String[] generateFooterA4_3() {
        parameters.put(TextInvoiceParameters.FTR_MONTO_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_MONTO_IVA, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL_LITERAL, integerToLiteral(invoice.getCabecera().getMontoTotal().intValue()));
        parameters.put(TextInvoiceParameters.FTR_TOTAL_DECIMAL, obtainDecimalPart(invoice.getCabecera().getMontoTotal()));
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, invoice.getCabecera().getCodigoMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.FTR_TOTAL_MONEDA, invoice.getCabecera().getMontoTotalMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.FTR_SUBTOTAL_MONEDA, invoice.getCabecera().getCodigoMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.FTR_DESCUENTO_MONEDA, invoice.getCabecera().getMontoTotalMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.HDR_CODIGO_SECTOR, invoice.getCabecera().getCodigoDocumentoSector().toString());
        parameters.put(TextInvoiceParameters.FTR_LEYENDA, invoice.getCabecera().getLeyenda());
        parameters.put(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, this.modalidadLeyenda);
        parameters.put(TextInvoiceParameters.FTR_SUBTOTAL_CIF, invoice.getCabecera().getTotalGastosInternacionales().toString());
        parameters.put(TextInvoiceParameters.FTR_MNT_SUBTOTAL, (PdfInvoice.detailsToSubtotal( invoice.getDetalle() )).toString());
        parameters.put(TextInvoiceParameters.FTR_SUBTOTAL, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_DESCUENTO, invoice.getCabecera().getDescuentoAdicional().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_BOXES_DESCRIPTION, invoice.getCabecera().getNumeroDescripcionPaquetesBultos().toString());
        parameters.put(TextInvoiceParameters.FTR_ADITIONAL_INFORMATION, invoice.getCabecera().getInformacionAdicional().toString());

        String qr = String.format(this.siatUrl,
            parameters.get(TextInvoiceParameters.HDR_NIT), parameters.get(TextInvoiceParameters.HDR_CUF), parameters.get(TextInvoiceParameters.HDR_FACTURA),
            parameters.get(TextInvoiceParameters.HDR_CODIGO_SECTOR));

        parameters.put(TextInvoiceParameters.FTR_QR, qr);

        String footer = printFooterA4_3();
        return split(footer, "\n", LENGTH_A4, false);
    }

    protected String printFooterA4_1() {
        StringBuilder footer = new StringBuilder(this.footer_1);

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MONEDA_LITERAL, parameters.get(TextInvoiceParameters.FTR_MONEDA_LITERAL)));

        footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTAL_DETAIL, parameters.get(TextInvoiceParameters.FTR_TOTAL_DETAIL)
            , footer, parameters.get(TextInvoiceParameters.FTR_TOTAL_DETAIL).length());

        footer = replace(TextInvoiceParameters.FTR_INCOTERM,parameters.get(TextInvoiceParameters.FTR_INCOTERM),footer);

        String response = footer.toString();

        return response;
    }

    protected String[] generateDetailA4_2(ArrayList<PdfComercialExportacionGastos> costos){
        String detail = "";
        if (!costos.isEmpty()){
            for( PdfComercialExportacionGastos item:  costos){
                StringBuilder row = new StringBuilder(this.detail_footer);

                row = replace(TextInvoiceParameters.JSON_DETAIL,item.getDescripcion(), row);
                row = replaceNumberValues(TextInvoiceParameters.JSON_MONTO,item.getSubTotal().toString(), row, item.getSubTotal().toString().length());

                detail += row.toString()  ;
            }
        }

        return split(detail, "\n", LENGTH_A4, false);
    }

    protected String[] generateDetailPOS_2(ArrayList<PdfComercialExportacionGastos> costos){
        String detail = "";
        if (!costos.isEmpty()){
            for( PdfComercialExportacionGastos item:  costos){
                StringBuilder row = new StringBuilder(this.detail_footer);

                row = new StringBuilder (row.toString().replace(TextInvoiceParameters.JSON_DETAIL, item.getDescripcion()));

                row = replaceNumberValues(TextInvoiceParameters.JSON_MONTO,item.getSubTotal().toString(), row, item.getSubTotal().toString().length());

                detail += row.toString()  ;
            }
        }

        return split(detail, ";", LENGTH_POS, false);
    }

    protected String printFooterA4_3() {
        StringBuilder footer = new StringBuilder(this.footer_3);

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_IVA, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA).length());
        }

        footer = replaceNumberValues(TextInvoiceParameters.FTR_SUBTOTAL_CIF, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL_CIF), footer, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL_CIF).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_DESCUENTO, parameters.get(TextInvoiceParameters.FTR_DESCUENTO), footer, parameters.get(TextInvoiceParameters.FTR_DESCUENTO).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_SUBTOTAL, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL), footer, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTAL, parameters.get(TextInvoiceParameters.FTR_TOTAL), footer, parameters.get(TextInvoiceParameters.FTR_TOTAL).length());

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_LITERAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_LITERAL)));

        footer = replace(TextInvoiceParameters.FTR_BOXES_DESCRIPTION,parameters.get(TextInvoiceParameters.FTR_BOXES_DESCRIPTION), footer);
        footer = replace(TextInvoiceParameters.FTR_ADITIONAL_INFORMATION,parameters.get(TextInvoiceParameters.FTR_ADITIONAL_INFORMATION), footer);
        footer = replace(TextInvoiceParameters.FTR_TOTAL_MONEDA,parameters.get(TextInvoiceParameters.FTR_TOTAL_MONEDA), footer);
        footer = replace(TextInvoiceParameters.FTR_SUBTOTAL_MONEDA,parameters.get(TextInvoiceParameters.FTR_SUBTOTAL_MONEDA), footer);
        footer = replace(TextInvoiceParameters.FTR_DESCUENTO_MONEDA,parameters.get(TextInvoiceParameters.FTR_DESCUENTO_MONEDA), footer);


        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_DECIMAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_DECIMAL)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MONEDA_LITERAL, parameters.get(TextInvoiceParameters.FTR_MONEDA_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_LEYENDA, parameters.get(TextInvoiceParameters.FTR_LEYENDA)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, parameters.get(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_QR, parameters.get(TextInvoiceParameters.FTR_QR)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MENSAJE, MENSAJE));

        return footer.toString();
    }

    @Override
    protected String[] generateHeaderPOS() {
        parameters.put(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR, invoice.getCabecera().getRazonSocialEmisor());
        parameters.put(TextInvoiceParameters.HDR_PUNTO_VENTA, invoice.getCabecera().getCodigoPuntoVenta().toString());
        parameters.put(TextInvoiceParameters.HDR_COD_CLIENTE, invoice.getCabecera().getCodigoCliente());
        parameters.put(TextInvoiceParameters.HDR_DIRECCION, invoice.getCabecera().getDireccion());
        parameters.put(TextInvoiceParameters.HDR_TELEFONO, invoice.getCabecera().getTelefono());
        parameters.put(TextInvoiceParameters.HDR_MUNICIPIO, invoice.getCabecera().getMunicipio());
        parameters.put(TextInvoiceParameters.HDR_NIT, invoice.getCabecera().getNitEmisor().toString());
        parameters.put(TextInvoiceParameters.HDR_FACTURA, invoice.getCabecera().getNumeroFactura().toString());
        parameters.put(TextInvoiceParameters.HDR_CUF, invoice.getCabecera().getCuf());
        parameters.put(TextInvoiceParameters.HDR_ACTIVIDAD, this.getActividad());
        parameters.put(TextInvoiceParameters.HDR_FECHA, XmlDateUtil.convertDateToYyyymmddhhmmssSSS(invoice.getCabecera().getFechaEmision()));
        parameters.put(TextInvoiceParameters.HDR_NIT_CI_EX, invoice.getCabecera().getNumeroDocumento());
        parameters.put(TextInvoiceParameters.HDR_CMPL, invoice.getCabecera().getComplemento()!= null ? invoice.getCabecera().getComplemento() : "");
        parameters.put(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL, invoice.getCabecera().getNombreRazonSocial());
        parameters.put(TextInvoiceParameters.HDR_LUGAR_DESTINO, invoice.getCabecera().getLugarDestino());
        parameters.put(TextInvoiceParameters.HDR_TIPO_CAMBIO, invoice.getCabecera().getTipoCambio().toString());
        parameters.put(TextInvoiceParameters.HDR_MONEDA_TRANSACCION, invoice.getCabecera().getCodigoMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.HDR_INCOTERM, invoice.getCabecera().getIncoterm());
        parameters.put(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR, invoice.getCabecera().getDireccionComprador());

        checkForOptional(invoice.getCabecera().getOptional());

        String response = generateHeaderPOSDefault();

        return split(response, ";", LENGTH_POS, true);

    }

    protected String[] generateDetailsPOS() {
        return null;
    }

    protected String[] generateDetailsPOS(Integer transaction) {
        String details = "";

            for (XmlComercialExportacionDetail item : invoice.getDetalle()) {

                HashMap<String, String> row = new HashMap<>();
                row.put(TextInvoiceParameters.CODIGO_PRODUCTO, item.getCodigoProducto());
                row.put(TextInvoiceParameters.CANTI, item.getCantidad().toString());
                row.put(TextInvoiceParameters.PREC_UNITA, item.getPrecioUnitario().toString());
                row.put(TextInvoiceParameters.DESCRIPCION, item.getDescripcion());
                row.put(TextInvoiceParameters.DESCUENTO, item.getMontoDescuento().toString());
                row.put(TextInvoiceParameters.SUB_TOTAL, item.getSubTotal().toString());
                row.put(TextInvoiceParameters.UNIDAD_MEDIDA, this.getUnidadMedidadLiteral(item.getUnidadMedida()));

                data.add(row);
        }

        details = printDetailsPOS(transaction);

        return split(details, ";", LENGTH_POS, false);
    }

    protected String printDetailsPOS(Integer transaction) {
        String response = "";
        for (HashMap<String, String> item : data) {
            String row = new String(this.details);
            row = row.replace(TextInvoiceParameters.COD_PRODUCTO, item.get(TextInvoiceParameters.CODIGO_PRODUCTO));

            StringBuilder cantidad = new StringBuilder(row);
            cantidad = replaceNumberValues(TextInvoiceParameters.CANTIDAD, item.get(TextInvoiceParameters.CANTI).toString(),
                cantidad,item.get(TextInvoiceParameters.CANTI).toString().length());
            cantidad = replace(TextInvoiceParameters.PREC_UNITA, item.get(TextInvoiceParameters.PREC_UNITA), cantidad);

            row = cantidad.toString();

            row = row.replace(TextInvoiceParameters.UNIDAD_MEDIDA, item.get(TextInvoiceParameters.UNIDAD_MEDIDA));
            row = row.replace(TextInvoiceParameters.DESCRIPCION, item.get(TextInvoiceParameters.DESCRIPCION));
            row = row.replace(TextInvoiceParameters.PREC_UNITA, item.get(TextInvoiceParameters.PREC_UNITA));
            row = row.replace(TextInvoiceParameters.DESCUENTO, item.get(TextInvoiceParameters.DESCUENTO));


            StringBuilder subTotal = new StringBuilder(row);
            subTotal = replaceNumberValues(TextInvoiceParameters.SUB_TOTAL, item.get(TextInvoiceParameters.SUB_TOTAL),
                subTotal, item.get(TextInvoiceParameters.SUB_TOTAL).length());
            row = subTotal.toString();

            response += row;
            return response;
        }

        return response;

    }

    protected String[] generateFooterPOS_2() {
        StringBuilder footer = new StringBuilder(this.footer_2);

        footer = replaceNumberValues(TextInvoiceParameters.FTR_SUBTOTAL_FOB,
            this.invoice.getCabecera().getTotalGastosNacionalesFob() != null ?
                this.invoice.getCabecera().getTotalGastosNacionalesFob().toString() : "0.00"
            , footer, this.invoice.getCabecera().getTotalGastosNacionalesFob() != null ?
                this.invoice.getCabecera().getTotalGastosNacionalesFob().toString().length() : "0.00".length());

        return split(footer.toString(), ";", LENGTH_POS, false) ;
    }

    @Override
    protected String[] generateFooterPOS() {
        parameters.put(TextInvoiceParameters.FTR_TOTAL_DETAIL, invoice.getCabecera().getMontoDetalle().toString());
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, invoice.getCabecera().getCodigoMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.FTR_INCOTERM, invoice.getCabecera().getIncotermDetalle());

        String footer = printFooterPOS_1();

        return split(footer, ";", LENGTH_POS, false);
    }

    protected String[] generateFooterPOS_3() {
        parameters.put(TextInvoiceParameters.FTR_MONTO_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_MONTO_IVA, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL_LITERAL, integerToLiteral(invoice.getCabecera().getMontoTotal().intValue()));
        parameters.put(TextInvoiceParameters.FTR_TOTAL_DECIMAL, obtainDecimalPart(invoice.getCabecera().getMontoTotal()));
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, invoice.getCabecera().getCodigoMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.HDR_CODIGO_SECTOR, invoice.getCabecera().getCodigoDocumentoSector().toString());
        parameters.put(TextInvoiceParameters.FTR_LEYENDA, invoice.getCabecera().getLeyenda());
        parameters.put(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, this.modalidadLeyenda);
        parameters.put(TextInvoiceParameters.FTR_MENSAJE, MENSAJE);
        parameters.put(TextInvoiceParameters.FTR_SUBTOTAL_CIF, invoice.getCabecera().getTotalGastosInternacionales().toString());

        parameters.put(TextInvoiceParameters.FTR_SUBTOTAL_MONEDA, invoice.getCabecera().getCodigoMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.FTR_TOTAL_MONEDA, invoice.getCabecera().getMontoTotalMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.FTR_DESCUENTO_MONEDA, invoice.getCabecera().getMontoTotalMoneda().equals(1) ? "Bolivianos" : "Dolares");
        parameters.put(TextInvoiceParameters.FTR_SUBTOTAL, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_DESCUENTO, invoice.getCabecera().getDescuentoAdicional().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_BOXES_DESCRIPTION, invoice.getCabecera().getNumeroDescripcionPaquetesBultos().toString());
        parameters.put(TextInvoiceParameters.FTR_ADITIONAL_INFORMATION, invoice.getCabecera().getInformacionAdicional().toString());

        String qr = String.format(this.siatUrl,
            parameters.get(TextInvoiceParameters.HDR_NIT), parameters.get(TextInvoiceParameters.HDR_CUF), parameters.get(TextInvoiceParameters.HDR_FACTURA),
            parameters.get(TextInvoiceParameters.HDR_CODIGO_SECTOR));

        parameters.put(TextInvoiceParameters.FTR_QR, qr);

        String footer = printFooterPOS_3();
        return split(footer, ";", LENGTH_POS, false);
    }

    protected String printFooterPOS_1() {
        StringBuilder footer = new StringBuilder(this.footer_1);

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MONEDA_LITERAL, parameters.get(TextInvoiceParameters.FTR_MONEDA_LITERAL)));

        footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTAL_DETAIL, parameters.get(TextInvoiceParameters.FTR_TOTAL_DETAIL)
            , footer, parameters.get(TextInvoiceParameters.FTR_TOTAL_DETAIL).length());
        footer = replace(TextInvoiceParameters.FTR_INCOTERM,parameters.get(TextInvoiceParameters.FTR_INCOTERM),footer);

        String response = footer.toString();


        return response;
    }

    protected String printFooterPOS_3() {
        StringBuilder footer = new StringBuilder(this.footer_3);

        //footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTAL_DEVUELTO, parameters.get(TextInvoiceParameters.FTR_TOTAL_DEVUELTO), footer, parameters.get(TextInvoiceParameters.FTR_TOTAL_DEVUELTO).length());
        //footer = replaceNumberValues(TextInvoiceParameters.FTR_DEBITO_FISCAL, parameters.get(TextInvoiceParameters.FTR_DEBITO_FISCAL), footer, parameters.get(TextInvoiceParameters.FTR_DEBITO_FISCAL).length());

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_IVA, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA).length());
        }

        footer = replaceNumberValues(TextInvoiceParameters.FTR_SUBTOTAL_CIF, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL_CIF), footer, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL_CIF).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_DESCUENTO, parameters.get(TextInvoiceParameters.FTR_DESCUENTO), footer, parameters.get(TextInvoiceParameters.FTR_DESCUENTO).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_SUBTOTAL, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL), footer, parameters.get(TextInvoiceParameters.FTR_SUBTOTAL).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTAL, parameters.get(TextInvoiceParameters.FTR_TOTAL), footer, parameters.get(TextInvoiceParameters.FTR_TOTAL).length());

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_LITERAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_LITERAL)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_BOXES_DESCRIPTION, parameters.get(TextInvoiceParameters.FTR_BOXES_DESCRIPTION)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_ADITIONAL_INFORMATION, parameters.get(TextInvoiceParameters.FTR_ADITIONAL_INFORMATION)));
        footer = replace(TextInvoiceParameters.FTR_TOTAL_MONEDA,parameters.get(TextInvoiceParameters.FTR_TOTAL_MONEDA), footer);
        footer = replace(TextInvoiceParameters.FTR_SUBTOTAL_MONEDA,parameters.get(TextInvoiceParameters.FTR_SUBTOTAL_MONEDA), footer);
        footer = replace(TextInvoiceParameters.FTR_DESCUENTO_MONEDA,parameters.get(TextInvoiceParameters.FTR_DESCUENTO_MONEDA), footer);


        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_DECIMAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_DECIMAL)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MONEDA_LITERAL, parameters.get(TextInvoiceParameters.FTR_MONEDA_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_LEYENDA, parameters.get(TextInvoiceParameters.FTR_LEYENDA)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MENSAJE, MENSAJE));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, parameters.get(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_QR, parameters.get(TextInvoiceParameters.FTR_QR)));

        return footer.toString();
    }

    private ArrayList<PdfComercialExportacionGastos> convertoJsonToGastos(String json){
        ArrayList<PdfComercialExportacionGastos> response = new ArrayList<>();

        if (json == null){
            return response ;
        }

        if (json.length() <= 1){
            return response;
        }

        Gson g = new Gson();
        Map<String, String> s = g.fromJson(json, HashMap.class);
        if (!s.isEmpty()){
            s.entrySet().stream().forEach(e -> {
                PdfComercialExportacionGastos data = new PdfComercialExportacionGastos();
                data.setDescripcion(e.getKey());
                data.setSubTotal( new BigDecimal(e.getValue()));
                response.add(data);
            });
        }
        return response;
    }

}
