import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { TransactionService } from './transaction.service';

@Controller()
export class TransactionController {
  constructor(private readonly transactionService: TransactionService) {}

  @MessagePattern('payments.transactions.create')
  create(@Payload() d: { data: any; createdBy: string }) {
    return this.transactionService.create(d.data, d.createdBy);
  }

  @MessagePattern('payments.transactions.confirm')
  confirm(
    @Payload() d: { id: string; gatewayRef: string; gatewayResponse?: any; updatedBy: string },
  ) {
    return this.transactionService.confirm(d.id, d.gatewayRef, d.gatewayResponse, d.updatedBy);
  }

  @MessagePattern('payments.transactions.fail')
  fail(@Payload() d: { id: string; reason: string; updatedBy: string }) {
    return this.transactionService.fail(d.id, d.reason, d.updatedBy);
  }

  @MessagePattern('payments.transactions.cancel')
  cancel(@Payload() d: { id: string; updatedBy: string }) {
    return this.transactionService.cancel(d.id, d.updatedBy);
  }

  @MessagePattern('payments.transactions.list')
  list(@Payload() d: { businessId: string; filters: any }) {
    return this.transactionService.list(d.businessId, d.filters ?? {});
  }

  @MessagePattern('payments.transactions.findOne')
  findOne(@Payload() d: { id: string; businessId: string }) {
    return this.transactionService.findOne(d.id, d.businessId);
  }

  @MessagePattern('payments.methods.configure')
  configureMethod(@Payload() d: { businessId: string; data: any; createdBy: string }) {
    return this.transactionService.configureMethod(d.businessId, d.data, d.createdBy);
  }

  @MessagePattern('payments.methods.list')
  listMethods(@Payload() d: { businessId: string }) {
    return this.transactionService.listMethods(d.businessId);
  }
}
