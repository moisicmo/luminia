import { Module } from '@nestjs/common';
import { BranchOfficeController } from './branch-office.controller';
import { BranchOfficeService } from './branch-office.service';

@Module({
  controllers: [BranchOfficeController],
  providers: [BranchOfficeService],
  exports: [BranchOfficeService],
})
export class BranchOfficeModule {}
