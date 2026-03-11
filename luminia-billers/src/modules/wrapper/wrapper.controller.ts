import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { WrapperService } from './wrapper.service';

@Controller()
export class WrapperController {
  constructor(private readonly service: WrapperService) {}

  @MessagePattern('wrapper.createOnline')
  createOnline(
    @Payload()
    data: {
      businessId: string;
      branchOfficeSiatId: number;
      pointSaleSiatId: number;
      sectorDocumentTypeId: string;
      scheduleSettingId?: string;
    },
  ) {
    return this.service.createOnlineWrapper(
      data.businessId,
      data.branchOfficeSiatId,
      data.pointSaleSiatId,
      data.sectorDocumentTypeId,
      data.scheduleSettingId,
    );
  }

  @MessagePattern('wrapper.validate')
  validate(@Payload() data: { wrapperId: string; businessId: string }) {
    return this.service.validateWrapper(data.wrapperId, data.businessId);
  }
}
