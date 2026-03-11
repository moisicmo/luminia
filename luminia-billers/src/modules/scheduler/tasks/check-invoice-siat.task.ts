import { Injectable, Logger } from '@nestjs/common';
import { Cron, CronExpression } from '@nestjs/schedule';
import { PrismaService } from '../../../lib/prisma';
import { SiatSoapService } from '../../siat/siat-soap.service';

/**
 * Periodically checks invoice status with SIAT (every 30s).
 * Equivalent to CheckInvoiceSiatTask from Java.
 */
@Injectable()
export class CheckInvoiceSiatTask {
  private readonly logger = new Logger(CheckInvoiceSiatTask.name);
  private readonly BATCH_SIZE = 30;
  private processing = false;

  constructor(
    private readonly prisma: PrismaService,
    private readonly siat: SiatSoapService,
  ) {}

  @Cron('*/30 * * * * *') // every 30 seconds
  async run() {
    if (this.processing) return;
    this.processing = true;

    try {
      const pendingInvoices = await this.prisma.invoice.findMany({
        where: { status: 'PENDING' },
        take: this.BATCH_SIZE,
        include: {
          cufd: { include: { cuis: { include: { pointSale: { include: { branchOffice: true } } } } } },
        },
      });

      for (const invoice of pendingInvoices) {
        await this.checkInvoice(invoice);
      }
    } catch (err) {
      this.logger.error(`CheckInvoiceSiat error: ${err.message}`);
    } finally {
      this.processing = false;
    }
  }

  private async checkInvoice(invoice: any) {
    try {
      const siatRes = await this.siat.verificacionEstadoFactura({
        SolicitudServicioVerificacionEstadoFactura: {
          codigoAmbiente: 2,
          codigoPuntoVenta: invoice.cufd.cuis.pointSale.pointSaleSiatId,
          codigoSistema: invoice.cufd.cuis.pointSale.branchOffice.businessId,
          codigoSucursal: invoice.cufd.cuis.pointSale.branchOffice.branchOfficeSiatId,
          nit: Number(invoice.nit),
          cuf: invoice.cuf,
        },
      });

      const siatCode = siatRes?.RespuestaServicioFacturacion?.codigoEstado;
      let newStatus: string | null = null;

      if (siatCode === 901) newStatus = 'ACCEPTED';
      else if (siatCode === 908) newStatus = 'REJECTED';
      else if (siatCode === 952) newStatus = 'CANCELLED';

      if (newStatus && newStatus !== invoice.status) {
        await this.prisma.invoice.update({
          where: { id: invoice.id },
          data: { status: newStatus as any },
        });
        this.logger.log(`Invoice ${invoice.cuf}: ${invoice.status} → ${newStatus}`);
      }
    } catch (err) {
      this.logger.warn(`Could not verify invoice ${invoice.cuf}: ${err.message}`);
    }
  }
}
