import { Module } from '@nestjs/common';
import { SubscriptionsModule } from './subscriptions/subscriptions.module';

@Module({
  imports: [SubscriptionsModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
