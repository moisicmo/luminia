import { Module } from '@nestjs/common';
import { SiatConfigController } from './siat-config.controller';
import { SiatConfigService } from './siat-config.service';

@Module({
  controllers: [SiatConfigController],
  providers: [SiatConfigService],
  exports: [SiatConfigService],
})
export class SiatConfigModule {}
