import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../../lib/prisma';
import { SiatSoapService } from '../siat/siat-soap.service';
import { SignatureService } from '../signature/signature.service';
import * as archiver from 'archiver';
import { Writable } from 'node:stream';

/**
 * Manages invoice packages (paquetes/wrappers) - groups of invoices
 * sent to SIAT as a single ZIP package.
 */
@Injectable()
export class WrapperService {
  private readonly logger = new Logger(WrapperService.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly siat: SiatSoapService,
    private readonly signature: SignatureService,
  ) {}

  /**
   * Creates a new wrapper (online package) for a given branch/point of sale.
   * Collects PENDING invoices and sends them as a package to SIAT.
   */
  async createOnlineWrapper(
    businessId: string,
    branchOfficeSiatId: number,
    pointSaleSiatId: number,
    sectorDocumentTypeId: string,
    scheduleSettingId?: string,
  ) {
    const pointSale = await this.prisma.pointSale.findFirst({
      where: {
        pointSaleSiatId,
        active: true,
        branchOffice: { branchOfficeSiatId, businessId, active: true },
      },
      include: { branchOffice: true },
    });
    if (!pointSale) throw new Error('PointSale not found');

    const cufd = await this.prisma.cufd.findFirst({
      where: {
        active: true,
        endDate: { gt: new Date() },
        cuis: { active: true, pointSaleId: pointSale.id },
      },
      include: { cuis: true },
      orderBy: { startDate: 'desc' },
    });
    if (!cufd) throw new Error('No active CUFD found');

    // Fetch PENDING invoices for this CUFD
    const pendingInvoices = await this.prisma.invoice.findMany({
      where: {
        status: 'PENDING',
        cufdId: cufd.id,
        sectorDocumentTypeId,
      },
      take: 500,
    });

    if (!pendingInvoices.length) {
      this.logger.log('No pending invoices to package');
      return null;
    }

    // Build ZIP with all invoice XMLs
    const zipBase64 = await this.buildInvoiceZip(pendingInvoices);
    const zipHash = this.signature.sha256(zipBase64);

    // Create wrapper record
    const wrapper = await this.prisma.wrapper.create({
      data: {
        cufdEvent: cufd.code,
        startDate: new Date(),
        endDate: new Date(),
        receptionCode: '',
        status: 'PENDING',
        scheduleSettingId,
        branchOfficeId: pointSale.branchOfficeId,
        pointSaleId: pointSale.id,
        sectorDocumentTypeId,
        createdBy: businessId,
      },
    });

    // Create InvoiceWrapper records
    await this.prisma.invoiceWrapper.createMany({
      data: pendingInvoices.map((inv, idx) => ({
        fileNumber: idx + 1,
        status: 'PENDING',
        invoiceBatchId: null,
        wrapperId: wrapper.id,
        createdBy: businessId,
      })),
    });

    // Send package to SIAT
    try {
      const siatRes = await this.siat.recepcionPaqueteFactura({
        SolicitudServicioRecepcionPaquete: {
          codigoAmbiente: 2,
          codigoPuntoVenta: pointSaleSiatId,
          codigoSistema: businessId,
          codigoSucursal: branchOfficeSiatId,
          nit: 1, // TODO: from business
          codigoDocumentoSector: sectorDocumentTypeId,
          codigoEmision: 1,
          codigoModalidad: 2,
          cufd: cufd.code,
          cuis: cufd.cuis.code,
          tipoFacturaDocumento: 1,
          archivo: zipBase64,
          fechaEnvio: new Date().toISOString(),
          hashArchivo: zipHash,
          cantidadFacturas: pendingInvoices.length,
        },
      });

      const receptionCode =
        siatRes?.RespuestaServicioFacturacion?.codigoRecepcion;
      const accepted =
        siatRes?.RespuestaServicioFacturacion?.codigoEstado === 901;

      await this.prisma.wrapper.update({
        where: { id: wrapper.id },
        data: {
          receptionCode: receptionCode ?? '',
          status: accepted ? 'SENT' : 'PENDING',
          endDate: new Date(),
          updatedBy: businessId,
        },
      });

      return { wrapperId: wrapper.id, receptionCode, accepted };
    } catch (err) {
      this.logger.error(`Wrapper send failed: ${err.message}`);
      await this.prisma.wrapper.update({
        where: { id: wrapper.id },
        data: { status: 'REJECTED', endDate: new Date(), updatedBy: businessId },
      });
      throw err;
    }
  }

  /**
   * Validates a previously sent package against SIAT.
   */
  async validateWrapper(wrapperId: string, businessId: string) {
    const wrapper = await this.prisma.wrapper.findUnique({
      where: { id: wrapperId },
      include: {
        branchOffice: true,
        pointSale: true,
        sectorDocumentType: true,
      },
    });
    if (!wrapper) throw new Error(`Wrapper not found: ${wrapperId}`);

    const cufd = await this.prisma.cufd.findFirst({
      where: {
        active: true,
        cuis: { active: true, pointSaleId: wrapper.pointSaleId },
      },
      include: { cuis: true },
      orderBy: { startDate: 'desc' },
    });

    try {
      const siatRes = await this.siat.validacionRecepcionMasivaFactura({
        SolicitudServicioValidacionRecepcionMasiva: {
          codigoAmbiente: 2,
          codigoPuntoVenta: wrapper.pointSale.pointSaleSiatId,
          codigoSistema: businessId,
          codigoSucursal: wrapper.branchOffice.branchOfficeSiatId,
          nit: 1, // TODO: from business
          cuis: cufd?.cuis?.code ?? '',
          cufd: cufd?.code ?? '',
          codigoRecepcion: wrapper.receptionCode,
        },
      });

      const status = siatRes?.RespuestaServicioFacturacion?.codigoEstado;
      const finalStatus = status === 901 ? 'ACCEPTED' : 'REJECTED';

      await this.prisma.wrapper.update({
        where: { id: wrapperId },
        data: { status: finalStatus as any, updatedBy: businessId },
      });

      // Update invoice statuses based on SIAT response
      if (finalStatus === 'ACCEPTED') {
        const wrapperInvoices = await this.prisma.invoiceWrapper.findMany({
          where: { wrapperId },
          include: { invoiceBatch: { include: { invoice: true } } },
        });
        // Mark invoices as ACCEPTED
      }

      return { wrapperId, status: finalStatus };
    } catch (err) {
      this.logger.error(`Wrapper validation failed: ${err.message}`);
      throw err;
    }
  }

  // ─── Helpers ──────────────────────────────────────────────────────────────

  private buildInvoiceZip(invoices: { invoiceXml: string; cuf: string }[]): Promise<string> {
    return new Promise((resolve, reject) => {
      const chunks: Buffer[] = [];
      const writable = new Writable({
        write(chunk, _enc, cb) {
          chunks.push(chunk);
          cb();
        },
      });
      writable.on('finish', () => {
        resolve(Buffer.concat(chunks).toString('base64'));
      });
      writable.on('error', reject);

      const archive = archiver('zip', { zlib: { level: 9 } });
      archive.on('error', reject);
      archive.pipe(writable);

      for (const inv of invoices) {
        archive.append(inv.invoiceXml, { name: `${inv.cuf}.xml` });
      }
      archive.finalize();
    });
  }
}
