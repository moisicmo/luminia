import { Module } from '@nestjs/common';
import { OrderModule } from './orders/order.module';

@Module({
  imports: [OrderModule],
})
export class AppModule {}
