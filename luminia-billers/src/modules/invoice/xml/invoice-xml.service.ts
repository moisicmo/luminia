import { Injectable } from '@nestjs/common';
import { create } from 'xmlbuilder2';
import {
  InvoiceXml,
  InvoiceHeader,
  InvoiceDetailLine,
  DocumentSectorType,
} from './invoice-xml.types';
import { IssueInvoiceDto } from '../dto/issue-invoice.dto';

@Injectable()
export class InvoiceXmlService {
  /**
   * Builds the XML string for a buy-sell invoice (facturaComputarizadaCompraVenta).
   */
  buildCompraVentaXml(invoice: InvoiceXml): string {
    const root = create({ version: '1.0', encoding: 'UTF-8' }).ele(
      'facturaComputarizadaCompraVenta',
    );
    this.buildCabecera(root, invoice.cabecera);
    this.buildDetalle(root, invoice.detalle);
    return root.end({ prettyPrint: false });
  }

  /**
   * Builds XML for property rental invoice (facturaComputarizadaAlquilerBienInmueble).
   */
  buildAlquilerXml(invoice: InvoiceXml): string {
    const root = create({ version: '1.0', encoding: 'UTF-8' }).ele(
      'facturaComputarizadaAlquilerBienInmueble',
    );
    this.buildCabecera(root, invoice.cabecera);
    this.buildDetalle(root, invoice.detalle);
    return root.end({ prettyPrint: false });
  }

  /**
   * Routes to the correct XML builder based on documentSectorType.
   */
  buildXml(invoice: InvoiceXml, sectorType: DocumentSectorType): string {
    switch (sectorType) {
      case DocumentSectorType.ALQUILER_BIENES_INMUEBLES:
        return this.buildAlquilerXml(invoice);
      default:
        return this.buildCompraVentaXml(invoice);
    }
  }

  /**
   * Converts the DTO + SIAT data into the InvoiceXml structure.
   */
  fromDto(
    dto: IssueInvoiceDto,
    invoiceNumber: number,
    cuf: string,
    cufd: string,
    branchOfficeSiatId: number,
    pointSaleSiatId: number,
    legend: string,
  ): InvoiceXml {
    const header = dto.header as Record<string, any>;

    const cabecera: InvoiceHeader = {
      nitEmisor: Number(header['nitEmisor']),
      razonSocialEmisor: header['razonSocialEmisor'],
      municipio: header['municipio'],
      telefono: header['telefono'],
      numeroFactura: invoiceNumber,
      cuf,
      cufd,
      codigoSucursal: branchOfficeSiatId,
      direccion: header['direccion'],
      codigoPuntoVenta: pointSaleSiatId,
      fechaEmision: new Date().toISOString(),
      nombreRazonSocial: header['nombreRazonSocial'],
      codigoTipoDocumentoIdentidad: Number(
        header['codigoTipoDocumentoIdentidad'],
      ),
      numeroDocumento: header['numeroDocumento'],
      complemento: header['complemento'],
      codigoCliente: header['codigoCliente'],
      codigoMetodoPago: Number(header['codigoMetodoPago']),
      numeroTarjeta: header['numeroTarjeta'],
      montoTotal: Number(header['montoTotal']),
      montoTotalSujetoIva: Number(header['montoTotalSujetoIva']),
      codigoMoneda: Number(header['codigoMoneda'] ?? 1),
      tipoCambio: Number(header['tipoCambio'] ?? 1),
      montoTotalMoneda: Number(header['montoTotalMoneda'] ?? header['montoTotal']),
      montoGiftCard: header['montoGiftCard']
        ? Number(header['montoGiftCard'])
        : undefined,
      descuentoAdicional: header['descuentoAdicional']
        ? Number(header['descuentoAdicional'])
        : undefined,
      codigoExcepcion: header['codigoExcepcion']
        ? Number(header['codigoExcepcion'])
        : undefined,
      cafc: header['cafc'],
      leyenda: legend,
      usuario: header['usuario'],
      codigoDocumentoSector: dto.documentSectorType,
    };

    const detalle: InvoiceDetailLine[] = (dto.detail ?? []).map((d) => ({
      actividadEconomica: d['actividadEconomica'],
      codigoProductoSin: Number(d['codigoProductoSin']),
      codigoProducto: d['codigoProducto'],
      descripcion: d['descripcion'],
      cantidad: Number(d['cantidad']),
      unidadMedida: Number(d['unidadMedida']),
      precioUnitario: Number(d['precioUnitario']),
      montoDescuento: d['montoDescuento'] ? Number(d['montoDescuento']) : undefined,
      subTotal: Number(d['subTotal']),
      numeroSerie: d['numeroSerie'],
      numeroImei: d['numeroImei'],
    }));

    return { cabecera, detalle };
  }

  // ─── Private helpers ─────────────────────────────────────────────────────

  private buildCabecera(root: any, c: InvoiceHeader) {
    const cab = root.ele('cabecera');
    cab.ele('nitEmisor').txt(String(c.nitEmisor));
    cab.ele('razonSocialEmisor').txt(c.razonSocialEmisor);
    cab.ele('municipio').txt(c.municipio);
    if (c.telefono) cab.ele('telefono').txt(c.telefono);
    cab.ele('numeroFactura').txt(String(c.numeroFactura));
    cab.ele('cuf').txt(c.cuf);
    cab.ele('cufd').txt(c.cufd);
    cab.ele('codigoSucursal').txt(String(c.codigoSucursal));
    cab.ele('direccion').txt(c.direccion);
    if (c.codigoPuntoVenta != null)
      cab.ele('codigoPuntoVenta').txt(String(c.codigoPuntoVenta));
    cab.ele('fechaEmision').txt(c.fechaEmision);
    cab.ele('nombreRazonSocial').txt(c.nombreRazonSocial);
    cab.ele('codigoTipoDocumentoIdentidad').txt(
      String(c.codigoTipoDocumentoIdentidad),
    );
    cab.ele('numeroDocumento').txt(c.numeroDocumento);
    if (c.complemento) cab.ele('complemento').txt(c.complemento);
    cab.ele('codigoCliente').txt(c.codigoCliente);
    cab.ele('codigoMetodoPago').txt(String(c.codigoMetodoPago));
    if (c.numeroTarjeta) cab.ele('numeroTarjeta').txt(c.numeroTarjeta);
    cab.ele('montoTotal').txt(String(c.montoTotal));
    cab.ele('montoTotalSujetoIva').txt(String(c.montoTotalSujetoIva));
    cab.ele('codigoMoneda').txt(String(c.codigoMoneda));
    cab.ele('tipoCambio').txt(String(c.tipoCambio));
    cab.ele('montoTotalMoneda').txt(String(c.montoTotalMoneda));
    if (c.montoGiftCard != null)
      cab.ele('montoGiftCard').txt(String(c.montoGiftCard));
    if (c.descuentoAdicional != null)
      cab.ele('descuentoAdicional').txt(String(c.descuentoAdicional));
    if (c.codigoExcepcion != null)
      cab.ele('codigoExcepcion').txt(String(c.codigoExcepcion));
    if (c.cafc) cab.ele('cafc').txt(c.cafc);
    cab.ele('leyenda').txt(c.leyenda);
    cab.ele('usuario').txt(c.usuario);
    cab.ele('codigoDocumentoSector').txt(String(c.codigoDocumentoSector));
  }

  private buildDetalle(root: any, lines: InvoiceDetailLine[]) {
    for (const line of lines) {
      const det = root.ele('detalle');
      det.ele('actividadEconomica').txt(line.actividadEconomica);
      det.ele('codigoProductoSin').txt(String(line.codigoProductoSin));
      det.ele('codigoProducto').txt(line.codigoProducto);
      det.ele('descripcion').txt(line.descripcion);
      det.ele('cantidad').txt(String(line.cantidad));
      det.ele('unidadMedida').txt(String(line.unidadMedida));
      det.ele('precioUnitario').txt(String(line.precioUnitario));
      if (line.montoDescuento != null)
        det.ele('montoDescuento').txt(String(line.montoDescuento));
      det.ele('subTotal').txt(String(line.subTotal));
      if (line.numeroSerie) det.ele('numeroSerie').txt(line.numeroSerie);
      if (line.numeroImei) det.ele('numeroImei').txt(line.numeroImei);
    }
  }
}
