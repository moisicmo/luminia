import { Module, Global } from '@nestjs/common';
import { SiatSoapService } from './siat-soap.service';

@Global()
@Module({
  providers: [SiatSoapService],
  exports: [SiatSoapService],
})
export class SiatModule {}
