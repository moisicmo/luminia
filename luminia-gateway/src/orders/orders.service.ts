import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceOrders } from '@/config';

const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class OrdersService {
  private readonly logger = new Logger(OrdersService.name);

  constructor(
    @Inject(RMQServiceOrders.getName())
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

  create(data: any, createdBy: string) {
    return this.send('orders.create', { data, createdBy });
  }

  addItem(orderId: string, businessId: string, item: any, createdBy: string) {
    return this.send('orders.addItem', { orderId, businessId, item, createdBy });
  }

  removeItem(orderId: string, itemId: string, businessId: string, updatedBy: string) {
    return this.send('orders.removeItem', { orderId, itemId, businessId, updatedBy });
  }

  addPayment(orderId: string, businessId: string, payment: any, createdBy: string) {
    return this.send('orders.addPayment', { orderId, businessId, payment, createdBy });
  }

  confirmPayment(
    orderId: string,
    paymentId: string,
    transactionId: string,
    updatedBy: string,
  ) {
    return this.send('orders.confirmPayment', {
      orderId,
      paymentId,
      transactionId,
      updatedBy,
    });
  }

  confirm(orderId: string, businessId: string, updatedBy: string) {
    return this.send('orders.confirm', { orderId, businessId, updatedBy });
  }

  cancel(orderId: string, businessId: string, reason: string, updatedBy: string) {
    return this.send('orders.cancel', { orderId, businessId, reason, updatedBy });
  }

  list(businessId: string, filters: any) {
    return this.send('orders.list', { businessId, filters: filters ?? {} });
  }

  findOne(id: string, businessId: string) {
    return this.send('orders.findOne', { id, businessId });
  }
}
