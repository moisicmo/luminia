import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { SiatConfigService, UpsertSiatConfigDto } from './siat-config.service';

@Controller()
export class SiatConfigController {
  constructor(private readonly service: SiatConfigService) {}

  @MessagePattern('siatConfig.findOne')
  findOne(@Payload() data: { businessId: string }) {
    return this.service.findOne(data.businessId);
  }

  @MessagePattern('siatConfig.upsert')
  upsert(@Payload() data: { dto: UpsertSiatConfigDto; businessId: string; userId: string }) {
    return this.service.upsert(data.dto, data.businessId, data.userId);
  }

  @MessagePattern('siatConfig.deactivate')
  deactivate(@Payload() data: { businessId: string; userId: string }) {
    return this.service.deactivate(data.businessId, data.userId);
  }
}
