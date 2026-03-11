import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServicePayments } from '@/config';

const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class PaymentsService {
  private readonly logger = new Logger(PaymentsService.name);

  constructor(
    @Inject(RMQServicePayments.getName())
    private readonly client: ClientProxy,
  ) {}

  private async send<T>(pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] enviando → ${pattern}`);
    try {
      const result = await firstValueFrom(
        this.client.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      this.logger.log(`[RMQ] respuesta OK ← ${pattern}`);
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        this.logger.error(`[RMQ] TIMEOUT: ${pattern}`);
        throw new HttpException(
          `El servicio no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      this.logger.error(`[RMQ] ERROR en ${pattern}: ${JSON.stringify(err)}`);
      throw err;
    }
  }

  // ─── Payment Methods ──────────────────────────────────────────────────────

  configureMethod(businessId: string, data: any, createdBy: string) {
    return this.send('payments.methods.configure', { businessId, data, createdBy });
  }

  listMethods(businessId: string) {
    return this.send('payments.methods.list', { businessId });
  }

  // ─── Transactions ─────────────────────────────────────────────────────────

  createTransaction(data: any, createdBy: string) {
    return this.send('payments.transactions.create', { data, createdBy });
  }

  listTransactions(businessId: string, filters: any) {
    return this.send('payments.transactions.list', { businessId, filters: filters ?? {} });
  }

  findTransaction(id: string, businessId: string) {
    return this.send('payments.transactions.findOne', { id, businessId });
  }

  confirmTransaction(
    id: string,
    gatewayRef: string,
    gatewayResponse: any,
    updatedBy: string,
  ) {
    return this.send('payments.transactions.confirm', {
      id,
      gatewayRef,
      gatewayResponse,
      updatedBy,
    });
  }

  // ─── Wallet ───────────────────────────────────────────────────────────────

  getWallet(businessId: string, ownerId: string, ownerType: string, currency?: string) {
    return this.send('payments.wallet.get', { businessId, ownerId, ownerType, currency });
  }

  listMovements(walletId: string, businessId: string) {
    return this.send('payments.wallet.movements', { walletId, businessId });
  }
}
