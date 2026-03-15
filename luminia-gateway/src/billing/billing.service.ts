import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceBillers } from '@/config';

const RMQ_TIMEOUT_MS = 15_000;

@Injectable()
export class BillingService {
  private readonly logger = new Logger(BillingService.name);

  constructor(
    @Inject(RMQServiceBillers.getName())
    private readonly billerClient: ClientProxy,
  ) {}

  private async send<T>(pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] enviando → ${pattern}`);
    try {
      const result = await firstValueFrom(
        this.billerClient.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      this.logger.log(`[RMQ] respuesta OK ← ${pattern}`);
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        this.logger.error(`[RMQ] TIMEOUT: ${pattern}`);
        throw new HttpException(
          `El servicio de facturación no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      this.logger.error(`[RMQ] ERROR en ${pattern}: ${JSON.stringify(err)}`);
      throw err;
    }
  }

  // ─── Branch Offices ──────────────────────────────────────────────────────────

  createBranchOffice(data: any) {
    return this.send('branchOffice.create', data);
  }

  listBranchOffices(businessId: string) {
    return this.send('branchOffice.findAll', { businessId });
  }

  findBranchOffice(id: string, businessId: string) {
    return this.send('branchOffice.findOne', { id, businessId });
  }

  updateBranchOffice(id: string, data: any) {
    return this.send('branchOffice.update', { id, ...data });
  }

  // ─── Point of Sale ──────────────────────────────────────────────────────────

  createPointSale(data: any) {
    return this.send('pointSale.create', data);
  }

  listPointSales(branchOfficeId: string, businessId: string) {
    return this.send('pointSale.findAll', { branchOfficeId, businessId });
  }

  findPointSale(id: string, businessId: string) {
    return this.send('pointSale.findOne', { id, businessId });
  }

  updatePointSale(id: string, data: any) {
    return this.send('pointSale.update', { id, ...data });
  }

  // ─── Sync (CUIS / CUFD / Parámetros) ──────────────────────────────────────

  syncCodes(businessId: string, pointSaleId: string) {
    return this.send('sync.codes', { businessId, pointSaleId });
  }

  syncAllCodes(businessId: string) {
    return this.send('sync.allCodes', { businessId });
  }

  syncParameters(businessId: string) {
    return this.send('sync.parameters', { businessId });
  }

  // ─── Invoice ────────────────────────────────────────────────────────────────

  issueInvoice(data: any) {
    return this.send('invoice.issue', data);
  }

  issueSimpleInvoice(data: any) {
    return this.send('invoice.issueSimple', data);
  }

  cancelInvoice(data: any) {
    return this.send('invoice.cancel', data);
  }

  cancelReversal(data: any) {
    return this.send('invoice.cancelReversal', data);
  }

  checkNit(data: any) {
    return this.send('invoice.checkNit', data);
  }

  checkInvoiceStatus(data: any) {
    return this.send('invoice.checkStatus', data);
  }

  listInvoices(businessId: string, filters?: any) {
    return this.send('invoice.list', { businessId, filters });
  }

  // ─── SIAT Config ────────────────────────────────────────────────────────────

  findSiatConfig(businessId: string) {
    return this.send('siatConfig.findOne', { businessId });
  }

  upsertSiatConfig(dto: any, businessId: string, userId: string) {
    return this.send('siatConfig.upsert', { dto, businessId, userId });
  }

  deactivateSiatConfig(businessId: string, userId: string) {
    return this.send('siatConfig.deactivate', { businessId, userId });
  }

  // ─── Catalogs ───────────────────────────────────────────────────────────────

  listActivities(businessId: string) {
    return this.send('catalog.activities', { businessId });
  }

  listMeasurementUnits(businessId: string) {
    return this.send('catalog.measurementUnits', { businessId });
  }

  listProductServices(businessId: string) {
    return this.send('catalog.productServices', { businessId });
  }

  listPaymentMethodTypes(businessId: string) {
    return this.send('catalog.paymentMethodTypes', { businessId });
  }

  listCurrencyTypes(businessId: string) {
    return this.send('catalog.currencyTypes', { businessId });
  }

  listDocumentSectorTypes(businessId: string) {
    return this.send('catalog.documentSectorTypes', { businessId });
  }

  listIdentityDocumentTypes(businessId: string) {
    return this.send('catalog.identityDocumentTypes', { businessId });
  }

  listCancellationReasons(businessId: string) {
    return this.send('catalog.cancellationReasons', { businessId });
  }

  // ─── Signatures ─────────────────────────────────────────────────────────────

  uploadSignature(data: any) {
    return this.send('signature.upload', data);
  }

  findActiveSignature(businessId: string) {
    return this.send('signature.findActive', { businessId });
  }

  listSignatures(businessId: string) {
    return this.send('signature.list', { businessId });
  }

  deactivateSignature(id: string, userId: string) {
    return this.send('signature.deactivate', { id, userId });
  }
}
