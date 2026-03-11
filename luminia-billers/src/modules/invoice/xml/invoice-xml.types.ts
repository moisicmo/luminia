/**
 * TypeScript types matching the XSD schemas for SIAT invoices.
 * Based on facturaComputarizadaCompraVenta.xsd and variants.
 */

export interface InvoiceHeader {
  nitEmisor: number;
  razonSocialEmisor: string;
  municipio: string;
  telefono?: string;
  numeroFactura: number;
  cuf: string;
  cufd: string;
  codigoSucursal: number;
  direccion: string;
  codigoPuntoVenta?: number;
  fechaEmision: string; // ISO datetime
  nombreRazonSocial: string;
  codigoTipoDocumentoIdentidad: number;
  numeroDocumento: string;
  complemento?: string;
  codigoCliente: string;
  codigoMetodoPago: number;
  numeroTarjeta?: string;
  montoTotal: number;
  montoTotalSujetoIva: number;
  codigoMoneda: number;
  tipoCambio: number;
  montoTotalMoneda: number;
  montoGiftCard?: number;
  descuentoAdicional?: number;
  codigoExcepcion?: number;
  cafc?: string;
  leyenda: string;
  usuario: string;
  codigoDocumentoSector: number;
}

export interface InvoiceDetailLine {
  actividadEconomica: string;
  codigoProductoSin: number;
  codigoProducto: string;
  descripcion: string;
  cantidad: number;
  unidadMedida: number;
  precioUnitario: number;
  montoDescuento?: number;
  subTotal: number;
  numeroSerie?: string;
  numeroImei?: string;
}

export interface InvoiceXml {
  cabecera: InvoiceHeader;
  detalle: InvoiceDetailLine[];
}

// ─── Invoice types (documentSectorType codes from SIAT) ──────────────────────

export enum DocumentSectorType {
  COMPRA_VENTA = 1,
  ALQUILER_BIENES_INMUEBLES = 2,
  ZONA_FRANCA = 3,
  EXPORTACION_SERVICIOS = 4,
  HOTEL = 5,
  TELECOMUNICACIONES = 6,
  SEGUROS = 7,
  SEGURIDAD_ALIMENTARIA = 8,
  SECTOR_EDUCATIVO = 9,
  SERVICIO_TURISTICO_HOSPEDAJE = 10,
  HOSPITAL_CLINICA = 11,
  BOLETO_AEREO = 12,
  SERVICIO_BASICO = 13,
  ENTIDAD_FINANCIERA = 14,
  COMERCIALIZACION_HIDROCARBURO = 15,
  COMERCIAL_EXPORTACION = 16,
  COMERCIAL_EXPORTACION_SERVICIO = 17,
  NOTA_CREDITO_DEBITO = 18,
  NOTA_FISCAL_CREDITO_DEBITO = 19,
  PREVALORADA = 20,
  RECIBO_ALQUILER = 21,
}

export enum ModalitySiat {
  UNIFIED = 'UNIFIED',
  FACTURADOR = 'FACTURADOR',
}

export enum InvoiceStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED',
}

export enum SiatStatus {
  RECEIVED = 'RECEIVED',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
}
