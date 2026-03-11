import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { SyncService } from './sync.service';

@Controller()
export class SyncController {
  constructor(private readonly syncService: SyncService) {}

  @MessagePattern('sync.codes')
  synchronizeCodes(
    @Payload()
    data: {
      businessId: string;
      branchOfficeSiatId: number;
      pointSaleSiatId: number;
    },
  ) {
    return this.syncService.synchronizeCuis(
      data.businessId,
      data.branchOfficeSiatId,
      data.pointSaleSiatId,
    );
  }

  @MessagePattern('sync.allCodes')
  synchronizeAllCodes(@Payload() data: { businessId: string }) {
    return this.syncService.synchronizeAllCodes(data.businessId);
  }

  @MessagePattern('sync.parameters')
  synchronizeParameters(
    @Payload() data: { businessId: string; nit: string; cuis: string },
  ) {
    return this.syncService.synchronizeParameters(
      data.businessId,
      data.nit,
      data.cuis,
    );
  }
}
