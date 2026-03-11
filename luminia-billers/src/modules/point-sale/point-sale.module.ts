import { Module } from '@nestjs/common';
import { PointSaleController } from './point-sale.controller';
import { PointSaleService } from './point-sale.service';

@Module({
  controllers: [PointSaleController],
  providers: [PointSaleService],
  exports: [PointSaleService],
})
export class PointSaleModule {}
