import { Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { JwtModule } from '@nestjs/jwt';
import { envs } from './config';
import { AuthModule } from './auth/auth.module';
import { BusinessModule } from './business/business.module';
import { UsersModule } from './users/users.module';
import { CommonModule } from './common/common.module';
import { InventoryModule } from './inventory/inventory.module';
import { BillingModule } from './billing/billing.module';
import { SubscriptionsModule } from './subscriptions/subscriptions.module';
import { OrdersModule } from './orders/orders.module';
import { PaymentsModule } from './payments/payments.module';
import { MallModule } from './mall/mall.module';
import { FilesModule } from './files/files.module';
import { ConversationsModule } from './conversations/conversations.module';
import { AuthGuard } from './common/guards/auth.guard';

@Module({
  imports: [
    JwtModule.register({ secret: envs.jwtSecret }),
    CommonModule,
    AuthModule,
    BusinessModule,
    UsersModule,
    InventoryModule,
    BillingModule,
    SubscriptionsModule,
    OrdersModule,
    PaymentsModule,
    MallModule,
    FilesModule,
    ConversationsModule,
  ],
  providers: [
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
  ],
})
export class AppModule {}
