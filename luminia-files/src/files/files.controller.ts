import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { FilesService } from './files.service';

@Controller()
export class FilesController {
  constructor(private readonly filesService: FilesService) {}

  @MessagePattern('file.create')
  create(@Payload() data: any) {
    return this.filesService.create(data);
  }

  @MessagePattern('file.findOne')
  findOne(@Payload() data: { id: string }) {
    return this.filesService.findOne(data.id);
  }

  @MessagePattern('file.list')
  list(@Payload() data: { businessId: string; category?: string }) {
    return this.filesService.listByBusiness(data.businessId, data.category);
  }

  @MessagePattern('file.delete')
  remove(@Payload() data: { id: string }) {
    return this.filesService.remove(data.id);
  }
}
