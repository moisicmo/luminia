package bo.com.luminia.sflbilling.msreport.siat.text.invoices;

import bo.com.luminia.sflbilling.msreport.siat.text.TextInvoice;
import bo.com.luminia.sflbilling.msreport.siat.text.TextInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates.TextNotaConciliacionTemplate;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlConciliacionCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlConciliacionOriginalDetail;

import java.util.HashMap;

public class TextCmpNotaConciliacionInvoice extends TextInvoice<XmlConciliacionCmpInvoice> {

    private String footer_1, footer_2;

    public TextCmpNotaConciliacionInvoice(XmlConciliacionCmpInvoice invoice, String paperType) {
        super(invoice, paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
        if (paperType == TextInvoice.PAPER_A4) {
            if (invoice.getCabecera().getDireccion().length() > MAX_DIRECCION_LENGTH) {
                this.header = TextNotaConciliacionTemplate.HEADER_A4_2;
            }else {
                this.header = TextNotaConciliacionTemplate.HEADER_A4;
            }
            this.details = TextNotaConciliacionTemplate.DETAILS_A4;
            this.footer_1 = TextNotaConciliacionTemplate.FOOTER_A4_1;
            this.footer_2 = TextNotaConciliacionTemplate.FOOTER_A4_2;
        } else {
            this.header = TextNotaConciliacionTemplate.HEADER_POS;
            this.details = TextNotaConciliacionTemplate.DETAILS_POS;
            this.footer_1 = TextNotaConciliacionTemplate.FOOTER_POS_1;
            this.footer_2 = TextNotaConciliacionTemplate.FOOTER_POS_2;
        }
    }

    @Override
    public String[] generate() {
        if (paperType == TextInvoice.PAPER_A4) {
            return concatWithStream(
                concatWithStream(
                    concatWithStream(
                        concatWithStream(
                            generateHeaderA4(), generateDetailsA4(1)),
                        generateFooterA4()),
                    generateDetailsA4(2)), generateFooterA4_());
        } else {
            return concatWithStream(
                concatWithStream(
                    concatWithStream(
                        concatWithStream(
                            generateHeaderPOS(), generateDetailsPOS(1)),
                        generateFooterPOS()), generateDetailsPOS(2))
                , generateFooterPOS_());
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
        parameters.put(TextInvoiceParameters.HDR_FECHA_FACTURA, XmlDateUtil.convertDateToYyyymmddhhmmssSSS(this.invoice.getCabecera().getFechaEmisionFactura()));
        parameters.put(TextInvoiceParameters.HDR_NRO_FACTURA, invoice.getCabecera().getNumeroFactura().toString());
        parameters.put(TextInvoiceParameters.HDR_CUF_FACTURA, invoice.getCabecera().getNumeroAutorizacionCuf());
        parameters.put(TextInvoiceParameters.HDR_COD_CONTROL, invoice.getCabecera().getCodigoControl());
        parameters.put(TextInvoiceParameters.HDR_COD_CLIENTE, invoice.getCabecera().getCodigoCliente());

        checkForOptional(invoice.getCabecera().getOptional());

        String header = generateHeaderA4Default();

        return split(header, "\n", LENGTH_A4, false);
    }

    @Override
    protected String[] generateDetailsA4() {
        return new String[0];
    }

    protected String[] generateDetailsA4(Integer transaccion) {
        if (data.isEmpty()) {
            for (XmlConciliacionOriginalDetail item : invoice.getDetalle()) {
                HashMap<String, String> row = new HashMap<>();
                row.put(TextInvoiceParameters.CODIGO_PRODUCTO, item.getCodigoProducto());
                row.put(TextInvoiceParameters.CANTIDAD, item.getCantidad().toString());
                row.put(TextInvoiceParameters.PRECIO_UNITARIO, item.getPrecioUnitario().toString());
                row.put(TextInvoiceParameters.DESCRIPCION, item.getDescripcion());
                row.put(TextInvoiceParameters.DESCUENTO, item.getMontoDescuento().toString());
                row.put(TextInvoiceParameters.SUB_TOTAL, item.getSubTotal().toString());

                data.add(row);
            }
        }
        String details = generateDetailsA4Print(transaccion);

        return split(details, "\n", LENGTH_A4, false);
    }

    protected String generateDetailsA4Print(Integer transaccion) {
        String response = "";
        for (HashMap<String, String> item : data) {
            StringBuilder row = new StringBuilder(this.details);
            row = replace(TextInvoiceParameters.CODIGO_PRODUCTO, item.get(TextInvoiceParameters.CODIGO_PRODUCTO), row);
            row = replaceNumberValues(TextInvoiceParameters.CANTIDAD, item.get(TextInvoiceParameters.CANTIDAD), row, item.get(TextInvoiceParameters.CANTIDAD).length());
            row = replace(TextInvoiceParameters.DESCRIPCION, item.get(TextInvoiceParameters.DESCRIPCION), row);
            row = replaceNumberValues(TextInvoiceParameters.PRECIO_UNITARIO, item.get(TextInvoiceParameters.PRECIO_UNITARIO), row, item.get(TextInvoiceParameters.PRECIO_UNITARIO).length());
            row = replaceNumberValues(TextInvoiceParameters.DESCUENTO, item.get(TextInvoiceParameters.DESCUENTO), row, item.get(TextInvoiceParameters.DESCUENTO).length());
            row = replaceNumberValues(TextInvoiceParameters.SUB_TOTAL, item.get(TextInvoiceParameters.SUB_TOTAL), row, item.get(TextInvoiceParameters.SUB_TOTAL).length());

            response += row + "\n";
        }
        return response;
    }

    @Override
    protected String[] generateFooterA4() {
        parameters.put(TextInvoiceParameters.FTR_TOTAL_ORIGINAL, invoice.getCabecera().getMontoTotalOriginal().toString());
        String footer = generateFooterA4_1();

        return split(footer, "\n", LENGTH_A4, false);
    }

    protected String[] generateFooterA4_() {
        parameters.put(TextInvoiceParameters.FTR_MONTO_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_MONTO_IVA, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL_LITERAL, integerToLiteral(invoice.getCabecera().getMontoTotalConciliado().intValue()));
        parameters.put(TextInvoiceParameters.FTR_TOTAL_DECIMAL, obtainDecimalPart(invoice.getCabecera().getMontoTotalConciliado()));
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, TextInvoice.BOLIVIANOS);
        parameters.put(TextInvoiceParameters.HDR_CODIGO_SECTOR, invoice.getCabecera().getCodigoDocumentoSector().toString());
        parameters.put(TextInvoiceParameters.FTR_LEYENDA, invoice.getCabecera().getLeyenda());
        parameters.put(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, this.modalidadLeyenda);
        parameters.put(TextInvoiceParameters.FTR_CONCILIADO, invoice.getCabecera().getMontoTotalConciliado().toString());

        String qr = String.format(this.siatUrl,
            parameters.get(TextInvoiceParameters.HDR_NIT), parameters.get(TextInvoiceParameters.HDR_CUF), parameters.get(TextInvoiceParameters.HDR_FACTURA),
            parameters.get(TextInvoiceParameters.HDR_CODIGO_SECTOR));

        parameters.put(TextInvoiceParameters.FTR_QR, qr);
        parameters.put(TextInvoiceParameters.FTR_DEBITO_FISCAL, invoice.getCabecera().getDebitoFiscalIva() != null ?
            invoice.getCabecera().getDebitoFiscalIva().toString() : "0.00");
        parameters.put(TextInvoiceParameters.FTR_CREDITO_FISCAL, invoice.getCabecera().getCreditoFiscalIva() != null ?
            invoice.getCabecera().getCreditoFiscalIva().toString() : "0.00");

        String footer = generateFooterA4_2();
        return split(footer, "\n", LENGTH_A4, false);
    }

    protected String generateFooterA4_1() {
        StringBuilder footer = new StringBuilder(this.footer_1);

        footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTAL_ORIGINAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_ORIGINAL)
            , footer, parameters.get(TextInvoiceParameters.FTR_TOTAL_ORIGINAL).length());


        String response = footer.toString();

        return response;
    }

    protected String generateFooterA4_2() {
        StringBuilder footer = new StringBuilder(this.footer_2);

        footer = replaceNumberValues(TextInvoiceParameters.FTR_CONCILIADO, parameters.get(TextInvoiceParameters.FTR_CONCILIADO), footer, parameters.get(TextInvoiceParameters.FTR_CONCILIADO).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_CREDITO_FISCAL, parameters.get(TextInvoiceParameters.FTR_CREDITO_FISCAL), footer, parameters.get(TextInvoiceParameters.FTR_CREDITO_FISCAL).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_DEBITO_FISCAL, parameters.get(TextInvoiceParameters.FTR_DEBITO_FISCAL), footer, parameters.get(TextInvoiceParameters.FTR_DEBITO_FISCAL).length());

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_IVA, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA).length());
        }

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_LITERAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_LITERAL)));
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
        parameters.put(TextInvoiceParameters.HDR_PUNTO_VENTA, this.getPointOfSale());
        parameters.put(TextInvoiceParameters.HDR_COD_CLIENTE, invoice.getCabecera().getCodigoCliente());
        parameters.put(TextInvoiceParameters.HDR_FECHA_FACTURA, XmlDateUtil.convertDateToYyyymmddhhmmssSSS(this.invoice.getCabecera().getFechaEmisionFactura()));
        parameters.put(TextInvoiceParameters.HDR_NRO_FACTURA, invoice.getCabecera().getNumeroFactura().toString());
        parameters.put(TextInvoiceParameters.HDR_CUF_FACTURA, invoice.getCabecera().getNumeroAutorizacionCuf());
        parameters.put(TextInvoiceParameters.HDR_COD_CONTROL, invoice.getCabecera().getCodigoControl());

        checkForOptional(invoice.getCabecera().getOptional());

        String response = generateHeaderPOSDefault();

        return split(response, ";", LENGTH_POS, true);
    }

    protected String[] generateDetailsPOS() {
        return null;
    }

    protected String[] generateDetailsPOS(Integer transaction) {
        String details = "";

        if (data.isEmpty()) {
            for (XmlConciliacionOriginalDetail item : invoice.getDetalle()) {

                HashMap<String, String> row = new HashMap<>();
                row.put(TextInvoiceParameters.CODIGO_PRODUCTO, item.getCodigoProducto());
                row.put(TextInvoiceParameters.CANTI, item.getCantidad().toString());
                row.put(TextInvoiceParameters.PREC_UNITA, item.getPrecioUnitario().toString());
                row.put(TextInvoiceParameters.DESCRIPCION, item.getDescripcion());
                row.put(TextInvoiceParameters.DESCUENTO, item.getMontoDescuento().toString());
                row.put(TextInvoiceParameters.SUB_TOTAL, item.getSubTotal().toString());

                data.add(row);
            }
        }
        details = generateDetailsPOSPrint(transaction);

        return split(details, ";", LENGTH_POS, false);
    }

    protected String generateDetailsPOSPrint(Integer transaction) {
        String response = "";
        for (HashMap<String, String> item : data) {
            String row = new String(this.details);
            row = row.replace(TextInvoiceParameters.CODIGO_PRODUCTO, item.get(TextInvoiceParameters.CODIGO_PRODUCTO));

            StringBuilder cantidad = new StringBuilder(row);
            cantidad = replace(TextInvoiceParameters.CANTI, item.get(TextInvoiceParameters.CANTI), cantidad);

            cantidad = replace(TextInvoiceParameters.PREC_UNITA, item.get(TextInvoiceParameters.PREC_UNITA), cantidad);

            row = cantidad.toString();

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

    @Override
    protected String[] generateFooterPOS() {
        parameters.put(TextInvoiceParameters.FTR_TOTAL_ORIGINAL, invoice.getCabecera().getMontoTotalOriginal().toString());

        String footer = generateFooterPOS_1();

        return split(footer, ";", LENGTH_POS, false);
    }

    protected String[] generateFooterPOS_() {
        parameters.put(TextInvoiceParameters.FTR_MONTO_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_MONTO_IVA, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL_LITERAL, integerToLiteral(invoice.getCabecera().getMontoTotalConciliado().intValue()));
        parameters.put(TextInvoiceParameters.FTR_TOTAL_DECIMAL, obtainDecimalPart(invoice.getCabecera().getMontoTotalConciliado()));
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, TextInvoice.BOLIVIANOS);
        parameters.put(TextInvoiceParameters.HDR_CODIGO_SECTOR, invoice.getCabecera().getCodigoDocumentoSector().toString());
        parameters.put(TextInvoiceParameters.FTR_LEYENDA, invoice.getCabecera().getLeyenda());
        parameters.put(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, this.modalidadLeyenda);

        String qr = String.format(this.siatUrl,
            parameters.get(TextInvoiceParameters.HDR_NIT), parameters.get(TextInvoiceParameters.HDR_CUF), parameters.get(TextInvoiceParameters.HDR_FACTURA),
            parameters.get(TextInvoiceParameters.HDR_CODIGO_SECTOR));
        parameters.put(TextInvoiceParameters.FTR_QR,qr);

        parameters.put(TextInvoiceParameters.FTR_CONCILIADO, invoice.getCabecera().getMontoTotalConciliado().toString());
        parameters.put(TextInvoiceParameters.FTR_DEBITO_FISCAL, invoice.getCabecera().getDebitoFiscalIva() != null ?
            invoice.getCabecera().getDebitoFiscalIva().toString() : "0.00");
        parameters.put(TextInvoiceParameters.FTR_CREDITO_FISCAL, invoice.getCabecera().getCreditoFiscalIva() != null ?
            invoice.getCabecera().getCreditoFiscalIva().toString() : "0.00");

        String footer = generateFooterPOS_2();
        return split(footer, ";", LENGTH_POS, false);
    }


    protected String generateFooterPOS_1() {
        StringBuilder footer = new StringBuilder(this.footer_1);

        footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTAL_ORIGINAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_ORIGINAL)
            , footer, parameters.get(TextInvoiceParameters.FTR_TOTAL_ORIGINAL).length());

        String response = footer.toString();

        return response;
    }


    protected String generateFooterPOS_2() {
        StringBuilder footer = new StringBuilder(this.footer_2);

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_IVA, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA).length());
        }

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_CREDITO_FISCAL, parameters.get(TextInvoiceParameters.FTR_CREDITO_FISCAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_LITERAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_DECIMAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_DECIMAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MONEDA_LITERAL, parameters.get(TextInvoiceParameters.FTR_MONEDA_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_LEYENDA, parameters.get(TextInvoiceParameters.FTR_LEYENDA)));

        footer = replaceNumberValues(TextInvoiceParameters.FTR_CONCILIADO, parameters.get(TextInvoiceParameters.FTR_CONCILIADO), footer, parameters.get(TextInvoiceParameters.FTR_CONCILIADO).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_DEBITO_FISCAL, parameters.get(TextInvoiceParameters.FTR_DEBITO_FISCAL), footer, parameters.get(TextInvoiceParameters.FTR_DEBITO_FISCAL).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_CREDITO_FISCAL, parameters.get(TextInvoiceParameters.FTR_CREDITO_FISCAL), footer, parameters.get(TextInvoiceParameters.FTR_CREDITO_FISCAL).length());

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, parameters.get(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_QR, parameters.get(TextInvoiceParameters.FTR_QR)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MENSAJE, MENSAJE));

        return footer.toString();
    }

}
