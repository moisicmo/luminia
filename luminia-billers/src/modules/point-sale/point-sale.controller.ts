import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import {
  PointSaleService,
  CreatePointSaleDto,
  UpdatePointSaleDto,
} from './point-sale.service';

@Controller()
export class PointSaleController {
  constructor(private readonly service: PointSaleService) {}

  @MessagePattern('pointSale.create')
  create(@Payload() dto: CreatePointSaleDto) {
    return this.service.create(dto);
  }

  @MessagePattern('pointSale.findAll')
  findAll(@Payload() data: { branchOfficeId: string }) {
    return this.service.findAll(data.branchOfficeId);
  }

  @MessagePattern('pointSale.findOne')
  findOne(@Payload() data: { id: string }) {
    return this.service.findOne(data.id);
  }

  @MessagePattern('pointSale.update')
  update(@Payload() data: { id: string; dto: UpdatePointSaleDto }) {
    return this.service.update(data.id, data.dto);
  }
}
