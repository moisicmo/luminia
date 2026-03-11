import {
  Injectable,
  Logger,
  BadRequestException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../../lib/prisma';
import { SiatSoapService } from '../siat/siat-soap.service';
import { SignatureService } from '../signature/signature.service';
import { InvoiceXmlService } from './xml/invoice-xml.service';
import { IssueInvoiceDto } from './dto/issue-invoice.dto';
import { CancelInvoiceDto, CancellationReversalDto, CheckNitDto } from './dto/cancel-invoice.dto';
import { DocumentSectorType, InvoiceStatus } from './xml/invoice-xml.types';
import { envs } from '../../config';

@Injectable()
export class InvoiceService {
  private readonly logger = new Logger(InvoiceService.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly siat: SiatSoapService,
    private readonly signature: SignatureService,
    private readonly xmlService: InvoiceXmlService,
  ) {}

  // ─── Emit ─────────────────────────────────────────────────────────────────

  async issue(dto: IssueInvoiceDto, businessId: string) {
    const stopwatch = Date.now();

    // 1. Resolve active CUFD for this branch + point of sale
    const cufd = await this.getActiveCufd(
      businessId,
      dto.branchOfficeSiat,
      dto.pointSaleSiat,
    );

    // 2. Load active signature (digital certificate) for the business
    const sig = await this.prisma.signature.findFirst({
      where: { businessId, active: true },
    });
    if (!sig) throw new BadRequestException('No active signature for business');

    // 3. Pick a random legend for this document sector
    const legend = await this.getRandomLegend(businessId, dto.documentSectorType);

    // 4. Generate invoice number
    const invoiceNumber = await this.nextInvoiceNumber(cufd.id);

    // 5. Calculate CUF
    const cuf = this.generateCuf(
      cufd.code,
      invoiceNumber,
      dto.branchOfficeSiat,
      dto.pointSaleSiat,
    );

    // 6. Build XML
    const invoiceXml = this.xmlService.fromDto(
      dto,
      invoiceNumber,
      cuf,
      cufd.code,
      dto.branchOfficeSiat,
      dto.pointSaleSiat,
      legend,
    );
    const xmlString = this.xmlService.buildXml(
      invoiceXml,
      dto.documentSectorType as DocumentSectorType,
    );

    // 7. Sign XML
    const signedXml = this.signature.signXml(
      xmlString,
      sig.privateKey,
      sig.certificate,
    );

    // 8. GZIP + Base64
    const xmlBase64 = await this.signature.gzipBase64(signedXml);

    // 9. Save invoice as PENDING
    const invoice = await this.prisma.invoice.create({
      data: {
        businessId,
        invoiceNumber,
        cuf,
        broadcastDate: new Date(),
        invoiceXml: signedXml,
        invoiceHash: this.signature.sha256(signedXml),
        invoiceJson: JSON.stringify(dto),
        // Issuer
        issuerNit: String(dto.header['nitEmisor'] ?? ''),
        issuerName: String(dto.header['razonSocialEmisor'] ?? ''),
        // Buyer
        buyerNit: String(dto.header['nitAdquirente'] ?? '0'),
        buyerName: String(dto.header['nombreRazonSocial'] ?? 'SIN NOMBRE'),
        buyerComplement: dto.header['complemento'] ?? null,
        buyerEmail: dto.email ?? null,
        // Totals
        totalAmount: dto.header['montoTotal'] ?? 0,
        vatableAmount: dto.header['montoTotalSujetoIva'] ?? 0,
        iceAmount: dto.header['montoTotalIce'] ?? 0,
        iehdAmount: dto.header['montoTotalIehd'] ?? 0,
        additionalDiscount: dto.header['descuentoAdicional'] ?? 0,
        status: InvoiceStatus.PENDING,
        modality: 'UNIFIED',
        cufdId: cufd.id,
        sectorDocumentTypeId: await this.resolveSectorDocTypeId(
          businessId,
          dto.documentSectorType,
        ),
        broadcastTypeId: await this.resolveBroadcastTypeId(businessId, 1),
        invoiceTypeId: await this.resolveInvoiceTypeId(businessId, 1),
        createdBy: businessId,
      },
    });

    // 10. Send to SIAT
    let receptionCode: string | null = null;
    let siatAccepted = false;

    const requestLog = await this.prisma.invoiceRequest.create({
      data: {
        requestDate: new Date(),
        businessId,
        invoiceId: invoice.id,
        request: xmlBase64,
        cuf,
        type: 'INVOICE_SEND',
      },
    });

    try {
      const siatRes = await this.siat.recepcionFactura({
        SolicitudServicioRecepcionFactura: {
          codigoAmbiente: 2, // 1=produccion, 2=piloto
          codigoPuntoVenta: dto.pointSaleSiat,
          codigoSistema: dto.businessCode,
          codigoSucursal: dto.branchOfficeSiat,
          nit: Number(dto.header['nitEmisor']),
          codigoDocumentoSector: dto.documentSectorType,
          codigoEmision: 1,
          codigoModalidad: 2,
          cufd: cufd.code,
          cuis: cufd.cuis.code,
          tipoFacturaDocumento: 1,
          archivo: xmlBase64,
          fechaEnvio: new Date().toISOString(),
          hashArchivo: this.signature.sha256(xmlBase64),
        },
      });

      receptionCode = siatRes?.RespuestaServicioFacturacion?.codigoRecepcion;
      const transactionState =
        siatRes?.RespuestaServicioFacturacion?.codigoEstado;
      siatAccepted = transactionState === 901;

      await this.prisma.invoiceRequest.update({
        where: { id: requestLog.id },
        data: {
          responseDate: new Date(),
          cuf: receptionCode,
          response: siatAccepted,
          elapsedTime: BigInt(Date.now() - stopwatch),
        },
      });
    } catch (err) {
      this.logger.error(`SIAT emission error: ${err.message}`);
      await this.prisma.invoiceRequest.update({
        where: { id: requestLog.id },
        data: {
          responseDate: new Date(),
          response: false,
          observation: err.message,
          elapsedTime: BigInt(Date.now() - stopwatch),
        },
      });
    }

    // 11. Update invoice status
    const finalStatus = siatAccepted ? InvoiceStatus.ACCEPTED : InvoiceStatus.PENDING;
    await this.prisma.invoice.update({
      where: { id: invoice.id },
      data: { status: finalStatus, receptionCode },
    });

    return {
      invoiceId: invoice.id,
      cuf,
      invoiceNumber,
      receptionCode,
      status: finalStatus,
      accepted: siatAccepted,
    };
  }

  // ─── Cancel ───────────────────────────────────────────────────────────────

  async cancel(dto: CancelInvoiceDto, businessId: string) {
    const invoice = await this.prisma.invoice.findFirst({
      where: { cuf: dto.cuf },
    });
    if (!invoice) throw new NotFoundException(`Invoice not found: ${dto.cuf}`);
    if (invoice.status === InvoiceStatus.CANCELLED) {
      throw new BadRequestException('Invoice already cancelled');
    }

    const cufd = await this.prisma.cufd.findUnique({
      where: { id: invoice.cufdId },
      include: { cuis: { include: { pointSale: true } } },
    });
    if (!cufd) throw new BadRequestException('CUFD not found for invoice');

    const sig = await this.prisma.signature.findFirst({
      where: { businessId, active: true },
    });
    if (!sig) throw new BadRequestException('No active signature for business');

    try {
      const siatRes = await this.siat.anulacionFactura({
        SolicitudServicioAnulacionFactura: {
          codigoAmbiente: 2,
          codigoPuntoVenta: cufd.cuis.pointSale.pointSaleSiatId,
          codigoSistema: businessId,
          codigoSucursal: cufd.cuis.pointSale.branchOfficeId,
          nit: Number(invoice.buyerNit),
          cuf: dto.cuf,
          codigoMotivo: dto.cancellationReasonId,
        },
      });

      const accepted =
        siatRes?.RespuestaServicioFacturacion?.codigoEstado === 901;

      if (accepted) {
        await this.prisma.invoice.update({
          where: { id: invoice.id },
          data: { status: InvoiceStatus.CANCELLED, updatedBy: businessId },
        });
      }

      return {
        cuf: dto.cuf,
        accepted,
        transactionState: siatRes?.RespuestaServicioFacturacion?.codigoEstado,
      };
    } catch (err) {
      this.logger.error(`SIAT cancellation error: ${err.message}`);
      throw new BadRequestException(`SIAT error: ${err.message}`);
    }
  }

  // ─── Cancellation reversal ────────────────────────────────────────────────

  async cancelReversal(dto: CancellationReversalDto, businessId: string) {
    const invoice = await this.prisma.invoice.findFirst({
      where: { cuf: dto.cuf },
    });
    if (!invoice) throw new NotFoundException(`Invoice not found: ${dto.cuf}`);

    const cufd = await this.prisma.cufd.findUnique({
      where: { id: invoice.cufdId },
      include: { cuis: { include: { pointSale: true } } },
    });
    if (!cufd) throw new BadRequestException('CUFD not found for invoice');

    try {
      const siatRes = await this.siat.reversionAnulacionFactura({
        SolicitudServicioReversionAnulacionFactura: {
          codigoAmbiente: 2,
          codigoPuntoVenta: cufd.cuis.pointSale.pointSaleSiatId,
          codigoSistema: businessId,
          codigoSucursal: cufd.cuis.pointSale.branchOfficeId,
          nit: Number(invoice.buyerNit),
          cuf: dto.cuf,
        },
      });

      const accepted =
        siatRes?.RespuestaServicioFacturacion?.codigoEstado === 901;

      if (accepted) {
        await this.prisma.invoice.update({
          where: { id: invoice.id },
          data: { status: InvoiceStatus.ACCEPTED, updatedBy: businessId },
        });
      }

      return { cuf: dto.cuf, accepted };
    } catch (err) {
      this.logger.error(`SIAT reversal error: ${err.message}`);
      throw new BadRequestException(`SIAT error: ${err.message}`);
    }
  }

  // ─── Check NIT ────────────────────────────────────────────────────────────

  async checkNit(dto: CheckNitDto, businessId: string) {
    const cufd = await this.prisma.cufd.findFirst({
      where: {
        active: true,
        cuis: {
          active: true,
          pointSale: {
            branchOffice: { businessId },
          },
        },
      },
      include: { cuis: { include: { pointSale: true } } },
    });

    if (!cufd) throw new BadRequestException('No active CUFD found');

    try {
      const siatRes = await this.siat.verificacionNit({
        SolicitudServicioVerificacionNit: {
          codigoAmbiente: 2,
          codigoPuntoVenta: cufd.cuis.pointSale.pointSaleSiatId,
          codigoSistema: dto.businessCode,
          codigoSucursal: cufd.cuis.pointSale.branchOfficeId,
          nit: dto.nit,
          cuis: cufd.cuis.code,
          cufd: cufd.code,
        },
      });

      return {
        nit: dto.nit,
        valid: siatRes?.RespuestaServicioFacturacion?.codigoEstado === 901,
        message: siatRes?.RespuestaServicioFacturacion?.mensajesList,
      };
    } catch (err) {
      this.logger.error(`SIAT checkNit error: ${err.message}`);
      throw new BadRequestException(`SIAT error: ${err.message}`);
    }
  }

  // ─── Status check ─────────────────────────────────────────────────────────

  async checkStatus(cuf: string) {
    const invoice = await this.prisma.invoice.findFirst({
      where: { cuf },
      include: {
        cufd: { include: { cuis: { include: { pointSale: true } } } },
      },
    });
    if (!invoice) throw new NotFoundException(`Invoice not found: ${cuf}`);

    const siatRes = await this.siat.verificacionEstadoFactura({
      SolicitudServicioVerificacionEstadoFactura: {
        codigoAmbiente: 2,
        codigoPuntoVenta: invoice.cufd.cuis.pointSale.pointSaleSiatId,
        codigoSistema: invoice.cufd.cuis.pointSale.branchOfficeId,
        codigoSucursal: invoice.cufd.cuis.pointSale.branchOfficeId,
        nit: Number(invoice.buyerNit),
        cuf,
      },
    });

    return {
      cuf,
      siatStatus: siatRes?.RespuestaServicioFacturacion,
      localStatus: invoice.status,
    };
  }

  // ─── Helpers ──────────────────────────────────────────────────────────────

  private async getActiveCufd(
    businessId: string,
    branchOfficeSiatId: number,
    pointSaleSiatId: number,
  ) {
    const cufd = await this.prisma.cufd.findFirst({
      where: {
        active: true,
        endDate: { gt: new Date() },
        cuis: {
          active: true,
          pointSale: {
            pointSaleSiatId,
            active: true,
            branchOffice: {
              branchOfficeSiatId,
              businessId,
            },
          },
        },
      },
      include: { cuis: true },
      orderBy: { startDate: 'desc' },
    });

    if (!cufd) {
      throw new BadRequestException(
        `No active CUFD for branch ${branchOfficeSiatId} / pointSale ${pointSaleSiatId}`,
      );
    }
    return cufd;
  }

  private async nextInvoiceNumber(cufdId: string): Promise<number> {
    const last = await this.prisma.invoice.findFirst({
      where: { cufdId },
      orderBy: { invoiceNumber: 'desc' },
    });
    return (last?.invoiceNumber ?? 0) + 1;
  }

  private generateCuf(
    cufd: string,
    invoiceNumber: number,
    branchSiat: number,
    pointSaleSiat: number,
  ): string {
    // Simplified CUF generation - production systems use SIAT's algorithm
    const base = `${cufd}${String(invoiceNumber).padStart(10, '0')}${branchSiat}${pointSaleSiat}`;
    return this.signature.sha256(base).substring(0, 70).toUpperCase();
  }

  private async getRandomLegend(
    businessId: string,
    sectorType: number,
  ): Promise<string> {
    const legends = await this.prisma.invoiceLegend.findMany({
      where: { businessId, active: true },
    });
    if (!legends.length) return 'ESTE DOCUMENTO ES LA REPRESENTACION GRAFICA DE UN DOCUMENTO FISCAL DIGITAL EMITIDO EN UNA MODALIDAD DE FACTURACION EN LINEA';
    const random = legends[Math.floor(Math.random() * legends.length)];
    return random.description;
  }

  private async resolveSectorDocTypeId(
    businessId: string,
    siatId: number,
  ): Promise<string> {
    const record = await this.prisma.sectorDocumentType.findFirst({
      where: { businessId, siatId },
    });
    if (!record)
      throw new BadRequestException(
        `SectorDocumentType not found: siatId=${siatId}`,
      );
    return record.id;
  }

  private async resolveBroadcastTypeId(
    businessId: string,
    siatId: number,
  ): Promise<string> {
    const record = await this.prisma.broadcastType.findFirst({
      where: { businessId, siatId },
    });
    if (!record)
      throw new BadRequestException(
        `BroadcastType not found: siatId=${siatId}`,
      );
    return record.id;
  }

  private async resolveInvoiceTypeId(
    businessId: string,
    siatId: number,
  ): Promise<string> {
    const record = await this.prisma.invoiceType.findFirst({
      where: { businessId, siatId },
    });
    if (!record)
      throw new BadRequestException(`InvoiceType not found: siatId=${siatId}`);
    return record.id;
  }
}
