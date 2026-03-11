import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { PlanService } from './plan.service';

@Controller()
export class PlanController {
  constructor(private readonly planService: PlanService) {}

  // ─── Platform Plans ────────────────────────────────────────────────────────

  @MessagePattern('subscriptions.platform-plans.create')
  createPlatformPlan(@Payload() d: { data: any; createdBy: string }) {
    return this.planService.createPlatformPlan(d.data, d.createdBy);
  }

  @MessagePattern('subscriptions.platform-plans.list')
  listPlatformPlans(@Payload() d: { onlyPublic?: boolean }) {
    return this.planService.listPlatformPlans(d.onlyPublic ?? true);
  }

  @MessagePattern('subscriptions.platform-plans.update')
  updatePlatformPlan(@Payload() d: { id: string; data: any }) {
    return this.planService.updatePlatformPlan(d.id, d.data);
  }

  // ─── Business Plans ────────────────────────────────────────────────────────

  @MessagePattern('subscriptions.plans.create')
  createPlan(@Payload() d: { data: any; createdBy: string }) {
    return this.planService.createPlan(d.data, d.createdBy);
  }

  @MessagePattern('subscriptions.plans.list')
  listPlans(@Payload() d: { businessId: string }) {
    return this.planService.listPlans(d.businessId);
  }

  @MessagePattern('subscriptions.plans.update')
  updatePlan(
    @Payload() d: { id: string; businessId: string; data: any; updatedBy: string },
  ) {
    return this.planService.updatePlan(d.id, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('subscriptions.plans.remove')
  removePlan(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.planService.removePlan(d.id, d.businessId, d.updatedBy);
  }
}
