import { Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { JwtModule } from '@nestjs/jwt';
import { envs } from './config';
import { AuthModule } from './auth/auth.module';
import { BusinessModule } from './business/business.module';
import { UsersModule } from './users/users.module';
import { CommonModule } from './common/common.module';
import { StoreModule } from './store/store.module';
import { SubscriptionsModule } from './subscriptions/subscriptions.module';
import { OrdersModule } from './orders/orders.module';
import { PaymentsModule } from './payments/payments.module';
import { AuthGuard } from './common/guards/auth.guard';

@Module({
  imports: [
    JwtModule.register({ secret: envs.jwtSecret }),
    CommonModule,
    AuthModule,
    BusinessModule,
    UsersModule,
    StoreModule,
    SubscriptionsModule,
    OrdersModule,
    PaymentsModule,
  ],
  providers: [
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
  ],
})
export class AppModule {}
