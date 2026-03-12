import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { SubscriptionsService } from './subscriptions.service';

@Controller()
export class SubscriptionsController {
  constructor(private readonly subscriptionsService: SubscriptionsService) {}

  // ─── Platform Plans ────────────────────────────────────────────────────────

  @MessagePattern('subscriptions.platform-plans.list')
  listPlatformPlans(@Payload() data: { onlyPublic?: boolean }) {
    return this.subscriptionsService.listPlatformPlans(data?.onlyPublic ?? true);
  }

  // ─── Platform Subscriptions ────────────────────────────────────────────────

  @MessagePattern('subscriptions.platform.subscribe')
  subscribeToPlatform(@Payload() data: { businessId: string; planId: string; createdBy: string }) {
    return this.subscriptionsService.subscribeToPlatform(data.businessId, data.planId, data.createdBy);
  }

  @MessagePattern('subscriptions.platform.get')
  getPlatformSubscription(@Payload() data: { businessId: string }) {
    return this.subscriptionsService.getPlatformSubscription(data.businessId);
  }

  @MessagePattern('subscriptions.platform.cancel')
  cancelPlatformSubscription(@Payload() data: { businessId: string; reason: string; updatedBy: string }) {
    return this.subscriptionsService.cancelPlatformSubscription(data.businessId, data.reason, data.updatedBy);
  }

  // ─── Business Plans ────────────────────────────────────────────────────────

  @MessagePattern('subscriptions.plans.create')
  createPlan(@Payload() data: { data: any; createdBy: string }) {
    return this.subscriptionsService.createPlan(data.data, data.createdBy);
  }

  @MessagePattern('subscriptions.plans.list')
  listPlans(@Payload() data: { businessId: string }) {
    return this.subscriptionsService.listPlans(data.businessId);
  }

  @MessagePattern('subscriptions.plans.update')
  updatePlan(@Payload() data: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.subscriptionsService.updatePlan(data.id, data.businessId, data.data, data.updatedBy);
  }

  @MessagePattern('subscriptions.plans.remove')
  removePlan(@Payload() data: { id: string; businessId: string; updatedBy: string }) {
    return this.subscriptionsService.removePlan(data.id, data.businessId, data.updatedBy);
  }

  // ─── Customer Subscriptions ────────────────────────────────────────────────

  @MessagePattern('subscriptions.subscribe')
  subscribe(@Payload() data: any) {
    const { createdBy, ...rest } = data;
    return this.subscriptionsService.subscribe(rest, createdBy);
  }

  @MessagePattern('subscriptions.list')
  listSubscriptions(@Payload() data: { businessId: string; filters: any }) {
    return this.subscriptionsService.listSubscriptions(data.businessId, data.filters);
  }

  @MessagePattern('subscriptions.findOne')
  getSubscription(@Payload() data: { id: string; businessId: string }) {
    return this.subscriptionsService.getSubscription(data.id, data.businessId);
  }

  @MessagePattern('subscriptions.cancel')
  cancelSubscription(@Payload() data: { id: string; businessId: string; reason: string; updatedBy: string }) {
    return this.subscriptionsService.cancelSubscription(data.id, data.businessId, data.reason, data.updatedBy);
  }
}
