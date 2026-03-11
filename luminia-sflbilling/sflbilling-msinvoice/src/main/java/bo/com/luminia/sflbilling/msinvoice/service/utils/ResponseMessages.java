package bo.com.luminia.sflbilling.msinvoice.service.utils;

public class ResponseMessages {

    public static final String WARNING_LISTA_VACIA = "La lista se encuentra vacia.";
    public static final String WARNING_OPERACION_NO_EJECUTADA = "La operacion no ha sido ejecutada. Contacte al administrador.";
    public static final String SUCCESS_OPERACION_EXITOSA = "La operacion se realizo con exito.";
    public static final String SUCCESS_ELIMINACION_EXITOSA = "Registro eliminado con exito.";
    public static final String SUCCESS_ACTUALIZACION_EXITOSA = "Registro actualizado con exito.";
    public static final String ERROR_REGISTRO_NO_ENCONTRADO = "El registro no fue encontrado.";
    public static final String ERROR_REGISTRO_COMPANY_NO_ENCONTRADO = "El registro %d de company no fue encontrado.";
    public static final String ERROR_DEBE_ESPECIFICAR_ID = "Debe especificar un ID.";
    public static final String ERROR_EXISTE_BRANCH_ID = "Ya existe un branch <%d> en la empresa.";
    public static final String ERROR_EXISTE_SIGNATURE = "Ya existe un signature en la empresa.";
    public static final String ERROR_NO_EXISTE_PRODUCT_SERVICE = "No existe el product_service <%d> en el sistema.";
    public static final String ERROR_NO_EXISTE_MEASUREMENT_UNIT = "No existe el measurement <%d> en el sistema.";
    public static final String ERROR_REGISTRO_NO_GUARDADO = "No se pudo almacenar el registro.";
    public static final String ERROR_EMPRESA_NO_ENCONTRADA = "No se pudo encontrar la empresa <%d> o se encuentra inactiva.";
    public static final String ERROR_CAMPO_REQUERIDO = "El campo <%s> es un valor requerido.";
    public static final String ERROR_SUCURSAL_NO_ENCONTRADA = "No se pudo encontrar la sucursal <%d>.";
    public static final String ERROR_NO_EXISTE_POINT_SALE_TYPE = "No existe el point_sale_type <%d> en el sistema.";
    public static final String ERROR_CREDENCIALES_INVALIDOS = "Credenciales Inválidos.";
    public static final String ERROR_SOLO_PERMITIDO_DOS_COMPANY = "Solo esta permitido 2 empresas con el mismo nit.";
    public static final String ERROR_COMPANY_REGISTRADO_MODALIDAD = "Ya se encuentra registrado una empresa con la modalidad enviada.";
    public static final String ERROR_NO_SIAT_CONNECTION = "No hay Conectividad con el SIAT. Codigo de Error : %d";
    public static final String ERROR_CUIS_NOT_FOUND = "Codigo Cuis no encontrado o no ha sido generado";
    public static final String ERROR_CUFD_NOT_FOUND = "Codigo Cufd no encontrado o no ha sido generado";
    public static final String ERROR_DATE_SYNC = "No se puede sincronizar la fecha";
    public static final String ERROR_INVOICE_VALIDATION = "Ocurrio un error en la validación de la factura.";
    public static final String ERROR_PRODUCTOS_HOMOLOGADOS = "No existen productos homologados para la empresa.";
    public static final String ERROR_NOT_FOUND_HOMOLOGADO = "El código de producto: %s no se encuentra homologado";
    public static final String ERROR_COMPANY_NOT_FOUND = "No se pudo encontrar la empresa <%s> o se encuentra inactiva.";
    public static final String ERROR_INVOICE_NOT_FOUND = "La factura con CUF: <%s> no existe o ya se encuentra Anulada.";
    public static final String ERROR_INVOICE_CANCELLED_NOT_FOUND = "La factura con CUF: <%s> no existe o no está Anulada.";
    public static final String ERROR_INVOICE_GENERATION = "Ocurrio un error en la generación de la factura";
    public static final String ERROR_GENERAR_FIRMA = "Existio un error al momento de generar la firma del archivo";
    public static final String ERROR_INVOICE_VALIDATION_PACKAGE_SEND = "La empresa no se encuentra activa para la emisión masiva de documentos fiscales.";
    public static final String ERROR_INVOICE_VALIDATION_LIMIT_PACKAGE_SEND = "El límite de facturas por lote es <%d>. Cantidad de facturas enviadas: <%d>.";
    public static final String ERROR_COMPANY_NOT_ENABLED = "La empresa no se encuentra activa por sincronización pendiente.";

    public static final String ERROR_COMPANY_INVOICE_DOESNT_MATCH = "La factura no pertenece a la compañía actual.";



}
