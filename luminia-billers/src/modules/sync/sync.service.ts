import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../../lib/prisma';
import { SiatSoapService } from '../siat/siat-soap.service';

@Injectable()
export class SyncService {
  private readonly logger = new Logger(SyncService.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly siat: SiatSoapService,
  ) {}

  // ─── CUIS ─────────────────────────────────────────────────────────────────

  async synchronizeCuis(businessId: string, branchOfficeSiatId: number, pointSaleSiatId: number) {
    const pointSale = await this.prisma.pointSale.findFirst({
      where: {
        pointSaleSiatId,
        branchOffice: { branchOfficeSiatId, businessId },
      },
      include: { branchOffice: true },
    });
    if (!pointSale) {
      throw new Error(`PointSale not found for business=${businessId} branch=${branchOfficeSiatId} pos=${pointSaleSiatId}`);
    }

    const nit = await this.getBusinessNit(businessId);

    const siatRes = await this.siat.solicitarCuis({
      SolicitudCuis: {
        codigoAmbiente: 2,
        codigoPuntoVenta: pointSaleSiatId,
        codigoSistema: businessId,
        codigoSucursal: branchOfficeSiatId,
        nit,
      },
    });

    const cuisData = siatRes?.RespuestaCuis;
    if (!cuisData?.codigo) {
      throw new Error('SIAT did not return a CUIS code');
    }

    // Deactivate previous CUIS for this point of sale
    await this.prisma.cuis.updateMany({
      where: { pointSaleId: pointSale.id, active: true },
      data: { active: false },
    });

    const cuis = await this.prisma.cuis.create({
      data: {
        code: cuisData.codigo,
        startDate: new Date(cuisData.fechaVigencia ?? new Date()),
        endDate: new Date(cuisData.fechaVigencia ?? new Date(Date.now() + 86400000 * 365)),
        active: true,
        pointSaleId: pointSale.id,
      },
    });

    this.logger.log(`CUIS synchronized for pointSale=${pointSaleSiatId}: ${cuis.code}`);
    return cuis;
  }

  // ─── CUFD ─────────────────────────────────────────────────────────────────

  async synchronizeCufd(cuisId: string) {
    const cuis = await this.prisma.cuis.findUnique({
      where: { id: cuisId },
      include: {
        pointSale: {
          include: { branchOffice: true },
        },
      },
    });
    if (!cuis) throw new Error(`CUIS not found: ${cuisId}`);

    const nit = await this.getBusinessNit(cuis.pointSale.branchOffice.businessId);

    const siatRes = await this.siat.solicitarCufd({
      SolicitudCufd: {
        codigoAmbiente: 2,
        codigoPuntoVenta: cuis.pointSale.pointSaleSiatId,
        codigoSistema: cuis.pointSale.branchOffice.businessId,
        codigoSucursal: cuis.pointSale.branchOffice.branchOfficeSiatId,
        nit,
        cuis: cuis.code,
      },
    });

    const cufdData = siatRes?.RespuestaCufd;
    if (!cufdData?.codigo) {
      throw new Error('SIAT did not return a CUFD code');
    }

    // Deactivate previous CUFD
    await this.prisma.cufd.updateMany({
      where: { cuisId, active: true },
      data: { active: false },
    });

    const cufd = await this.prisma.cufd.create({
      data: {
        code: cufdData.codigo,
        controlCode: cufdData.codigoControl ?? '',
        address: cufdData.direccion ?? '',
        startDate: new Date(cufdData.fechaVigencia ?? new Date()),
        endDate: new Date(cufdData.fechaVigencia ?? new Date(Date.now() + 86400000)),
        active: true,
        cuisId,
      },
    });

    this.logger.log(`CUFD synchronized for cuis=${cuisId}: ${cufd.code}`);
    return cufd;
  }

  // ─── Synchronize ALL codes for a business ─────────────────────────────────

  async synchronizeAllCodes(businessId: string) {
    const branchOffices = await this.prisma.branchOffice.findMany({
      where: { businessId, active: true },
      include: {
        pointSales: {
          where: { active: true },
          include: {
            cuis: { where: { active: true }, orderBy: { startDate: 'desc' }, take: 1 },
          },
        },
      },
    });

    const results: any[] = [];

    for (const branch of branchOffices) {
      for (const pos of branch.pointSales) {
        try {
          // Sync CUIS first
          const cuis = await this.synchronizeCuis(
            businessId,
            branch.branchOfficeSiatId,
            pos.pointSaleSiatId,
          );
          // Then CUFD
          const cufd = await this.synchronizeCufd(cuis.id);
          results.push({ branchSiat: branch.branchOfficeSiatId, posSiat: pos.pointSaleSiatId, cuis: cuis.code, cufd: cufd.code });
        } catch (err) {
          this.logger.error(`Sync failed for branch=${branch.branchOfficeSiatId} pos=${pos.pointSaleSiatId}: ${err.message}`);
          results.push({ branchSiat: branch.branchOfficeSiatId, posSiat: pos.pointSaleSiatId, error: err.message });
        }
      }
    }

    return results;
  }

  // ─── Synchronize SIAT master data (catalogs) ──────────────────────────────

  async synchronizeParameters(businessId: string, nit: string, cuis: string) {
    const req = { SolicitudSincronizacion: { codigoAmbiente: 2, nit: Number(nit), cuis } };
    const errors: string[] = [];

    await Promise.allSettled([
      this.syncActivities(businessId, req, errors),
      this.syncSectorDocumentTypes(businessId, req, errors),
      this.syncBroadcastTypes(businessId, req, errors),
      this.syncInvoiceTypes(businessId, req, errors),
      this.syncSignificantEvents(businessId, req, errors),
      this.syncCancellationReasons(businessId, req, errors),
      this.syncOriginCountries(businessId, req, errors),
      this.syncMeasurementUnits(businessId, req, errors),
      this.syncPaymentMethodTypes(businessId, req, errors),
      this.syncCurrencyTypes(businessId, req, errors),
      this.syncPointSaleTypes(businessId, req, errors),
      this.syncInvoiceLegends(businessId, req, errors),
      this.syncServiceMessages(businessId, req, errors),
    ]);

    return { synchronized: true, errors };
  }

  // ─── Private catalog sync helpers ─────────────────────────────────────────

  private async syncActivities(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarActividades(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.activity.deleteMany({ where: { businessId } });
      await this.prisma.activity.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoActividad,
          description: item.descripcion,
          activityType: item.tipoActividad ?? '',
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`activities: ${err.message}`);
    }
  }

  private async syncSectorDocumentTypes(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaTipoDocumentoSector(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.sectorDocumentType.deleteMany({ where: { businessId } });
      await this.prisma.sectorDocumentType.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoDocumentoSector,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`sectorDocumentTypes: ${err.message}`);
    }
  }

  private async syncBroadcastTypes(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaTipoEmision(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.broadcastType.deleteMany({ where: { businessId } });
      await this.prisma.broadcastType.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoTipoEmision,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`broadcastTypes: ${err.message}`);
    }
  }

  private async syncInvoiceTypes(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaTipoFactura(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.invoiceType.deleteMany({ where: { businessId } });
      await this.prisma.invoiceType.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoTipoFactura,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`invoiceTypes: ${err.message}`);
    }
  }

  private async syncSignificantEvents(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaEventoSignificativo(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.significantEvent.deleteMany({ where: { businessId } });
      await this.prisma.significantEvent.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoEvento,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`significantEvents: ${err.message}`);
    }
  }

  private async syncCancellationReasons(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaMotivoAnulacion(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.cancellationReason.deleteMany({ where: { businessId } });
      await this.prisma.cancellationReason.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoMotivoAnulacion,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`cancellationReasons: ${err.message}`);
    }
  }

  private async syncOriginCountries(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaPaisOrigen(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.originCountry.deleteMany({ where: { businessId } });
      await this.prisma.originCountry.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoPais,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`originCountries: ${err.message}`);
    }
  }

  private async syncMeasurementUnits(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaUnidadMedida(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.measurementUnit.deleteMany({ where: { businessId } });
      await this.prisma.measurementUnit.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoClasificador,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`measurementUnits: ${err.message}`);
    }
  }

  private async syncPaymentMethodTypes(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaTipoMetodoPago(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.paymentMethodType.deleteMany({ where: { businessId } });
      await this.prisma.paymentMethodType.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoClasificador,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`paymentMethodTypes: ${err.message}`);
    }
  }

  private async syncCurrencyTypes(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaTipoMoneda(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.currencyType.deleteMany({ where: { businessId } });
      await this.prisma.currencyType.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoClasificador,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`currencyTypes: ${err.message}`);
    }
  }

  private async syncPointSaleTypes(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaTipoDocumentoIdentidad(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.pointSaleType.deleteMany({ where: { businessId } });
      await this.prisma.pointSaleType.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoClasificador,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`pointSaleTypes: ${err.message}`);
    }
  }

  private async syncInvoiceLegends(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaLeyendaFactura(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.invoiceLegend.deleteMany({ where: { businessId } });
      await this.prisma.invoiceLegend.createMany({
        data: items.map((item: any) => ({
          activityCode: item.codigoActividad,
          description: item.descripcionLeyenda,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`invoiceLegends: ${err.message}`);
    }
  }

  private async syncServiceMessages(businessId: string, req: any, errors: string[]) {
    try {
      const res = await this.siat.sincronizarParametricaMensajeServicio(req);
      const items = res?.RespuestaSincronizacion?.listaCodigos ?? [];
      await this.prisma.serviceMessage.deleteMany({ where: { businessId } });
      await this.prisma.serviceMessage.createMany({
        data: items.map((item: any) => ({
          siatId: item.codigoClasificador,
          description: item.descripcion,
          active: true,
          businessId,
        })),
        skipDuplicates: true,
      });
    } catch (err) {
      errors.push(`serviceMessages: ${err.message}`);
    }
  }

  private async getBusinessNit(businessId: string): Promise<number> {
    // NIT is stored in signature or branchOffice metadata
    // For now return a placeholder; this should be fetched from luminia-business
    const branch = await this.prisma.branchOffice.findFirst({
      where: { businessId },
    });
    if (!branch) throw new Error(`No branch found for business: ${businessId}`);
    return 1; // TODO: fetch NIT from luminia-business via RMQ
  }
}
