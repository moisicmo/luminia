package bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates;

public class TextNotaConciliacionTemplate {
    /**
     * Nota Conciliacion A4
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
            "                                                       NOTA DE CONCILIACIÓN\n" +
            "\n" +
            "              Fecha: <HDR_FECHA>                                      ;        NIT/CI/CEX: <HDR_NIT_CI_EX>; COMPLEMENTO: <HDR_CMPL>;\n" +
            "Nombre/Razón Social: <HDR_NOMBRE_RAZON_SOCIAL>                        ; Código de Cliente: <HDR_COD_CLIENTE>            ;\n" +
            "                     <HDR_NOMBRE_RAZON_SOCIAL>                        ;\n" +
            "\n"+
            "                                                        FACTURA ORIGINAL\n"+
            "       Nro Factura : <HDR_NRO_FACTURA>                                ;   Cod Control :<HDR_COD_CONTROL>                         ;\n" +
            "     Fecha Factura : <HDR_FECHA_FACTURA>                        ;                 CUF :<HDR_CUF_FACTURA>                         ;\n" +
            "                                                                                        <HDR_CUF_FACTURA>                        ;\n" +
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
            "                                                       NOTA DE CONCILIACIÓN\n" +
            "\n" +
            "              Fecha: <HDR_FECHA>                                      ;        NIT/CI/CEX: <HDR_NIT_CI_EX>; COMPLEMENTO: <HDR_CMPL>;\n" +
            "Nombre/Razón Social: <HDR_NOMBRE_RAZON_SOCIAL>                        ; Código de Cliente: <HDR_COD_CLIENTE>            ;\n" +
            "                     <HDR_NOMBRE_RAZON_SOCIAL>                        ;\n" +
            "\n"+
            "                                                        FACTURA ORIGINAL\n"+
            "       Nro Factura : <HDR_NRO_FACTURA>                                ;   Cod Control :<HDR_COD_CONTROL>                         ;\n" +
            "     Fecha Factura : <HDR_FECHA_FACTURA>                        ;                 CUF :<HDR_CUF_FACTURA>                         ;\n" +
            "                                                                                        <HDR_CUF_FACTURA>                        ;\n" +
            "\n" +
            "DATOS FACTURA ORIGINAL\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO     | CANTIDAD   | DESCRIPCIÓN                                         | PRECIO UNITARIO |  DESCUENTO  |  SUBTOTAL   |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";


    public static final String DETAILS_A4 =
            "<codProducto>   ; ; <cantid>   <descripcion>                                       ; ;      <precUnit> ;    <descu>  ;<subTotal> |";

    public static final String FOOTER_A4_1 =
        "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                    MONTO TOTAL ORIGINAL Bs. ;<FTR_TOTAL_ORIGINAL> \n" +
            "\n" +
            "DATOS DE LA CONCILIACIÓN\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO     | CANTIDAD   | DESCRIPCIÓN                                         | PRECIO UNITARIO |  DESCUENTO  |  SUBTOTAL   |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";


    public static final String FOOTER_A4_2 =
        "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                 MONTO TOTAL CONCILIADO Bs.      ;<FTR_CONCILIADO> \n" +
            "                                                                                     CRÉDITO FISCAL IVA Bs. ; <FTR_CREDITO_FISCAL> \n" +
            "                                                                                      DÉDITO FISCAL IVA Bs. ;  <FTR_DEBITO_FISCAL> \n" +
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
     * Compra Venta POS
     */
    public static final String HEADER_POS =
        "NOTA CONCILIACIÓN;" +
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
            " COD.CLIENTE:<HDR_COD_CLIENTE>;" +
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
            "MONTO TOTAL CONCILIADO;" +
            "Bs.     <FTR_CONCILIADO>;" +
            "MONTO CRÉDITO FISCAL IVA;" +
            "Bs.     <FTR_CREDITO_FISCAL>;" +
            "MONTO DÉBITO FISCAL IVA;" +
            " Bs.    <FTR_DEBITO_FISCAL>;" +
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
