import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { InvoiceService } from './invoice.service';
import { IssueInvoiceDto } from './dto/issue-invoice.dto';
import {
  CancelInvoiceDto,
  CancellationReversalDto,
  CheckNitDto,
} from './dto/cancel-invoice.dto';

@Controller()
export class InvoiceController {
  constructor(private readonly invoiceService: InvoiceService) {}

  @MessagePattern('invoice.issue')
  issue(@Payload() data: { dto: IssueInvoiceDto; businessId: string }) {
    return this.invoiceService.issue(data.dto, data.businessId);
  }

  @MessagePattern('invoice.cancel')
  cancel(@Payload() data: { dto: CancelInvoiceDto; businessId: string }) {
    return this.invoiceService.cancel(data.dto, data.businessId);
  }

  @MessagePattern('invoice.cancelReversal')
  cancelReversal(
    @Payload() data: { dto: CancellationReversalDto; businessId: string },
  ) {
    return this.invoiceService.cancelReversal(data.dto, data.businessId);
  }

  @MessagePattern('invoice.checkNit')
  checkNit(@Payload() data: { dto: CheckNitDto; businessId: string }) {
    return this.invoiceService.checkNit(data.dto, data.businessId);
  }

  @MessagePattern('invoice.checkStatus')
  checkStatus(@Payload() data: { cuf: string }) {
    return this.invoiceService.checkStatus(data.cuf);
  }
}
