import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { SubscriptionService } from './subscription.service';

@Controller()
export class SubscriptionController {
  constructor(private readonly subscriptionService: SubscriptionService) {}

  // ─── Platform Subscriptions ────────────────────────────────────────────────

  @MessagePattern('subscriptions.platform.subscribe')
  subscribeToPlatform(
    @Payload() d: { businessId: string; planId: string; createdBy: string },
  ) {
    return this.subscriptionService.subscribeToPlatform(d.businessId, d.planId, d.createdBy);
  }

  @MessagePattern('subscriptions.platform.get')
  getPlatformSubscription(@Payload() d: { businessId: string }) {
    return this.subscriptionService.getPlatformSubscription(d.businessId);
  }

  @MessagePattern('subscriptions.platform.activate')
  activatePlatformSubscription(
    @Payload() d: { subscriptionId: string; orderId: string; transactionId: string },
  ) {
    return this.subscriptionService.activatePlatformSubscription(
      d.subscriptionId,
      d.orderId,
      d.transactionId,
    );
  }

  @MessagePattern('subscriptions.platform.cancel')
  cancelPlatformSubscription(
    @Payload() d: { businessId: string; reason: string; updatedBy: string },
  ) {
    return this.subscriptionService.cancelPlatformSubscription(
      d.businessId,
      d.reason,
      d.updatedBy,
    );
  }

  // ─── Business Subscriptions ────────────────────────────────────────────────

  @MessagePattern('subscriptions.subscribe')
  subscribe(
    @Payload() d: {
      businessId: string;
      planId: string;
      customerId: string;
      customerName: string;
      createdBy: string;
      metadata?: any;
    },
  ) {
    return this.subscriptionService.subscribe(
      d.businessId,
      d.planId,
      d.customerId,
      d.customerName,
      d.createdBy,
      d.metadata,
    );
  }

  @MessagePattern('subscriptions.list')
  listSubscriptions(
    @Payload() d: {
      businessId: string;
      filters: { customerId?: string; status?: string; planId?: string };
    },
  ) {
    return this.subscriptionService.listSubscriptions(d.businessId, d.filters ?? {});
  }

  @MessagePattern('subscriptions.findOne')
  getSubscription(@Payload() d: { id: string; businessId: string }) {
    return this.subscriptionService.getSubscription(d.id, d.businessId);
  }

  @MessagePattern('subscriptions.activate')
  activateSubscription(
    @Payload() d: { subscriptionId: string; orderId: string; transactionId: string },
  ) {
    return this.subscriptionService.activateSubscription(
      d.subscriptionId,
      d.orderId,
      d.transactionId,
    );
  }

  @MessagePattern('subscriptions.cancel')
  cancelSubscription(
    @Payload() d: { id: string; businessId: string; reason: string; updatedBy: string },
  ) {
    return this.subscriptionService.cancelSubscription(
      d.id,
      d.businessId,
      d.reason,
      d.updatedBy,
    );
  }

  @MessagePattern('subscriptions.renewal-cron')
  renewalCron() {
    return this.subscriptionService.renewalCron();
  }
}
