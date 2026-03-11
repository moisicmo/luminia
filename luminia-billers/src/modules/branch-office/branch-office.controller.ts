import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import {
  BranchOfficeService,
  CreateBranchOfficeDto,
  UpdateBranchOfficeDto,
} from './branch-office.service';

@Controller()
export class BranchOfficeController {
  constructor(private readonly service: BranchOfficeService) {}

  @MessagePattern('branchOffice.create')
  create(@Payload() dto: CreateBranchOfficeDto) {
    return this.service.create(dto);
  }

  @MessagePattern('branchOffice.findAll')
  findAll(@Payload() data: { businessId: string }) {
    return this.service.findAll(data.businessId);
  }

  @MessagePattern('branchOffice.findOne')
  findOne(@Payload() data: { id: string }) {
    return this.service.findOne(data.id);
  }

  @MessagePattern('branchOffice.update')
  update(@Payload() data: { id: string; dto: UpdateBranchOfficeDto }) {
    return this.service.update(data.id, data.dto);
  }
}
