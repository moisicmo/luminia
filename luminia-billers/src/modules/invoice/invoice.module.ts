import { Module } from '@nestjs/common';
import { InvoiceController } from './invoice.controller';
import { InvoiceService } from './invoice.service';
import { InvoiceXmlService } from './xml/invoice-xml.service';

@Module({
  controllers: [InvoiceController],
  providers: [InvoiceService, InvoiceXmlService],
  exports: [InvoiceService],
})
export class InvoiceModule {}
