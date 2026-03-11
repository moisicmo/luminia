import { Module } from '@nestjs/common';
import { WrapperController } from './wrapper.controller';
import { WrapperService } from './wrapper.service';

@Module({
  controllers: [WrapperController],
  providers: [WrapperService],
  exports: [WrapperService],
})
export class WrapperModule {}
