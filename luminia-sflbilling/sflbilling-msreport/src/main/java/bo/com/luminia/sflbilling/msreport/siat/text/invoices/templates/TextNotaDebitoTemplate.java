package bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates;

public class TextNotaDebitoTemplate {
    /**
     * Nota Debito A4
     */
    public static final String HEADER_A4 =
        "\n" +
            "<b>                                                                                    NIT : <HDR_NIT>                            ;\n" +
            "<b><HDR_RAZON_SOCIAL_EMISOR>                                 ;                  FACTURA No.: <HDR_FACTURA>                        ;\n" +
            "<b><HDR_DIRECCION>                                           ;                   CÓDIGO DE : <HDR_CUF>                            ;\n" +
            "<b>Telf. : <HDR_TELEFONO>                                              ;      AUTORIZACIÓN   <HDR_CUF>                            ;\n" +
            "<b><HDR_MUNICIPIO>                                           ;;                <HDR_KEY_1> : <HDR_VALUE_1>                        ;\n" +
            "                                                             ;                 <HDR_KEY_2> : <HDR_VALUE_2>                        ;\n" +
            "                                                             ;                 <HDR_KEY_3> : <HDR_VALUE_3>                        ;\n" +
            "                                                             ;                 <HDR_KEY_4> : <HDR_VALUE_4>                        ;\n" +
            "\n" +
            "                                                       NOTA CRÉDITO-DÉBITO\n" +
            "                                                 (Con Derecho a Credito Fiscal)\n" +
            "\n" +
            "              Fecha : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>; COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razón Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ;   Fecha Factura :<HDR_FECHA_FACTURA>                     ;\n" +
            "        Nro Factura : <HDR_NRO_FACTURA>                            ;                 CUF :<HDR_CUF_FACTURA>                       ;\n" +
            "                                                                                          <HDR_CUF_FACTURA>                       ;\n" +
            "\n" +
            "DATOS FACTURA ORIGINAL\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO     | CANTIDAD   | DESCRIPCIÓN                                         | PRECIO UNITARIO |  DESCUENTO  |  SUBTOTAL   |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";

    public static final String HEADER_A4_2 =
        "\n" +
            "<b>                                                                                    NIT : <HDR_NIT>                            ;\n" +
            "<b><HDR_RAZON_SOCIAL_EMISOR>                                 ;                  FACTURA No.: <HDR_FACTURA>                        ;\n" +
            "<b><HDR_DIRECCION>                                           ;                   CÓDIGO DE : <HDR_CUF>                            ;\n" +
            "<b><HDR_DIRECCION>                                           ;                AUTORIZACIÓN   <HDR_CUF>                            ;\n" +
            "<b>Telf. : <HDR_TELEFONO>                                    ;;                <HDR_KEY_1> : <HDR_VALUE_1>                        ;\n" +
            "<b><HDR_MUNICIPIO>                                           ;;                <HDR_KEY_2> : <HDR_VALUE_2>                        ;\n" +
            "                                                             ;                 <HDR_KEY_3> : <HDR_VALUE_3>                        ;\n" +
            "                                                             ;                 <HDR_KEY_4> : <HDR_VALUE_4>                        ;\n" +
            "\n" +
            "                                                       NOTA CRÉDITO-DÉBITO\n" +
            "                                                 (Con Derecho a Credito Fiscal)\n" +
            "\n" +
            "              Fecha : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>; COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razón Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ;   Fecha Factura :<HDR_FECHA_FACTURA>                     ;\n" +
            "        Nro Factura : <HDR_NRO_FACTURA>                            ;                 CUF :<HDR_CUF_FACTURA>                       ;\n" +
            "                                                                                          <HDR_CUF_FACTURA>                       ;\n" +
            "\n" +
            "DATOS FACTURA ORIGINAL\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO     | CANTIDAD   | DESCRIPCIÓN                                         | PRECIO UNITARIO |  DESCUENTO  |  SUBTOTAL   |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";


    public static final String DETAILS_A4 =
            "<codProducto>   ; ;  <cantid>   <descripcion>                                       ; ;     <precUnit> ;   <descu>  ;  <subTotal> |";

    public static final String FOOTER_A4_1 =
        "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                    MONTO TOTAL ORIGINAL Bs. ;<FTR_TOTAL_ORIGINAL> \n" +
            "\n" +
            "DATOS DE LA DEVOLUCIÓN O RESCISIÓN\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO     | CANTIDAD   | DESCRIPCION                                         | PRECIO UNITARIO |  DESCUENTO  |  SUBTOTAL   |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";


    public static final String FOOTER_A4_2 =
        "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                    MONTO TOTAL DEVUELTO Bs. ;<FTR_TOTAL_DEVUELTO> \n" +
            "                                                                           MONTO EFECTIVO CRÉDITO-DÉBITO Bs. ;  <FTR_EFEC_CRE_DEB> \n" +
            "SON: <FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>\n" +
            "\n" +
            "<FTR_MENSAJE>\n" +
            "\n" +
            "<FTR_LEYENDA>\n" +
            "\n" +
            "<FTR_MODALIDAD_LEYENDA>\n"+
            "\n" +
            "<FTR_QR>\n" ;
    /**
     * Nota Debito POS
     */
    public static final String HEADER_POS =
        "NOTA CRÉDITO-DÉBITO;" +
            ";" +
            ";" +
            "<HDR_RAZON_SOCIAL_EMISOR>;" +
            "<HDR_PUNTO_VENTA>;" +
            "<HDR_DIRECCION>;" +
            "TEL. <HDR_TELEFONO>;" +
            "<HDR_MUNICIPIO>;" +
            "- - - - - - - - - - - - - - - -;" +
            "NIT;" +
            "<HDR_NIT>;" +
            "No FACTURA;" +
            "<HDR_FACTURA>;" +
            "CÓDIGO DE AUTORIZACIÓN;" +
            "<HDR_CUF>;" +
            "<HDR_KEY_1>;" +
            "<HDR_VALUE_1>;" +
            "<HDR_KEY_2>;" +
            "<HDR_VALUE_2>;" +
            "<HDR_KEY_3>;" +
            "<HDR_VALUE_3>;" +
            "<HDR_KEY_4>;" +
            "<HDR_VALUE_4>;" +
            "- - - - - - - - - - - - - - - -;" +
            "NOMBRE/RAZÓN:<HDR_NOMBRE_RAZON_SOCIAL>;" +
            "   NIT/CI/EX:<HDR_NIT_CI_EX>;" +
            "COMPLEMENTO:<HDR_CMPL>;"+
            "NRO FACTURA :<HDR_NRO_FACTURA>;" +
            "FECH.FACTURA:<HDR_FECHA_FACTURA>;" +
            "N.AUTORI/CUF:<HDR_CUF_FACTURA>;" +
            "- - - - - - - - - - - - - - - -;" +
            ";" +
            "DATOS FACTURA ORIGINAL;";

    public static final String DETAILS_POS =
        "<codProducto>-<descripcion>;" +
            "<cant>;x<precUnit>;; <subTotal>;";

    public static final String FOOTER_POS_1 =
        "- - - - - - - - - - - - - - - -;" +
            "MONTO TOTAL ORIGINAL;" +
            " Bs.      ;<FTR_TOTAL_ORIGINAL>;" +
            ";" +
            "DATOS DE LA DEVOLUCIÓN O RESCISIÓN;" +
            "- - - - - - - - - - - - - - - -;";


    public static final String FOOTER_POS_2 =
        "- - - - - - - - - - - - - - - -;" +
            "MONTO TOTAL DEVUELTO;" +
            " Bs.      ;<FTR_TOTAL_DEVUELTO>;" +
            "MONTO EFECTIVO CREDITO-DEBITO;" +
            " Bs.        ;<FTR_EFEC_CRE_DEB>;" +
            ";" +
            "Son:;" +
            "<FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>;" +
            ";" +
            "<FTR_MENSAJE>;"+
            ";" +
            "<FTR_LEYENDA>;"+
            ";" +
            "<FTR_MODALIDAD_LEYENDA>;"+
            ";"+
            "<FTR_QR>;" ;

}
