import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceSubscriptions } from '@/config';

const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class SubscriptionsService {
  private readonly logger = new Logger(SubscriptionsService.name);

  constructor(
    @Inject(RMQServiceSubscriptions.getName())
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

  // ─── Platform Plans ────────────────────────────────────────────────────────

  listPlatformPlans() {
    return this.send('subscriptions.platform-plans.list', { onlyPublic: true });
  }

  // ─── Platform Subscriptions ────────────────────────────────────────────────

  subscribeToPlatform(businessId: string, planId: string, userId: string) {
    return this.send('subscriptions.platform.subscribe', {
      businessId,
      planId,
      createdBy: userId,
    });
  }

  getPlatformSubscription(businessId: string) {
    return this.send('subscriptions.platform.get', { businessId });
  }

  cancelPlatformSubscription(businessId: string, reason: string, userId: string) {
    return this.send('subscriptions.platform.cancel', {
      businessId,
      reason,
      updatedBy: userId,
    });
  }

  // ─── Business Plans ────────────────────────────────────────────────────────

  createPlan(businessId: string, data: any, userId: string) {
    return this.send('subscriptions.plans.create', {
      data: { ...data, businessId },
      createdBy: userId,
    });
  }

  listPlans(businessId: string) {
    return this.send('subscriptions.plans.list', { businessId });
  }

  updatePlan(id: string, businessId: string, data: any, userId: string) {
    return this.send('subscriptions.plans.update', {
      id,
      businessId,
      data,
      updatedBy: userId,
    });
  }

  removePlan(id: string, businessId: string, userId: string) {
    return this.send('subscriptions.plans.remove', {
      id,
      businessId,
      updatedBy: userId,
    });
  }

  // ─── Business Subscriptions ────────────────────────────────────────────────

  subscribe(businessId: string, data: any, userId: string) {
    return this.send('subscriptions.subscribe', {
      businessId,
      ...data,
      createdBy: userId,
    });
  }

  listSubscriptions(businessId: string, filters: any) {
    return this.send('subscriptions.list', { businessId, filters: filters ?? {} });
  }

  getSubscription(id: string, businessId: string) {
    return this.send('subscriptions.findOne', { id, businessId });
  }

  cancelSubscription(id: string, businessId: string, reason: string, userId: string) {
    return this.send('subscriptions.cancel', {
      id,
      businessId,
      reason,
      updatedBy: userId,
    });
  }
}
