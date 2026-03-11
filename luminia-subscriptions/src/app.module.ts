import { Module } from '@nestjs/common';
import { PlanModule } from './plans/plan.module';
import { SubscriptionModule } from './subscriptions/subscription.module';

@Module({
  imports: [PlanModule, SubscriptionModule],
})
export class AppModule {}
