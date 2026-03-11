import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { OrderService } from './order.service';

@Controller()
export class OrderController {
  constructor(private readonly orderService: OrderService) {}

  @MessagePattern('orders.create')
  create(@Payload() d: { data: any; createdBy: string }) {
    return this.orderService.create(d.data, d.createdBy);
  }

  @MessagePattern('orders.addItem')
  addItem(
    @Payload() d: { orderId: string; businessId: string; item: any; createdBy: string },
  ) {
    return this.orderService.addItem(d.orderId, d.businessId, d.item, d.createdBy);
  }

  @MessagePattern('orders.removeItem')
  removeItem(
    @Payload() d: { orderId: string; itemId: string; businessId: string; updatedBy: string },
  ) {
    return this.orderService.removeItem(d.orderId, d.itemId, d.businessId, d.updatedBy);
  }

  @MessagePattern('orders.addPayment')
  addPayment(
    @Payload() d: { orderId: string; businessId: string; payment: any; createdBy: string },
  ) {
    return this.orderService.addPayment(d.orderId, d.businessId, d.payment, d.createdBy);
  }

  @MessagePattern('orders.confirmPayment')
  confirmPayment(
    @Payload() d: { orderId: string; paymentId: string; transactionId: string; updatedBy: string },
  ) {
    return this.orderService.confirmPayment(
      d.orderId,
      d.paymentId,
      d.transactionId,
      d.updatedBy,
    );
  }

  @MessagePattern('orders.confirm')
  confirm(@Payload() d: { orderId: string; businessId: string; updatedBy: string }) {
    return this.orderService.confirm(d.orderId, d.businessId, d.updatedBy);
  }

  @MessagePattern('orders.cancel')
  cancel(
    @Payload() d: { orderId: string; businessId: string; reason: string; updatedBy: string },
  ) {
    return this.orderService.cancel(d.orderId, d.businessId, d.reason, d.updatedBy);
  }

  @MessagePattern('orders.list')
  list(@Payload() d: { businessId: string; filters: any }) {
    return this.orderService.list(d.businessId, d.filters ?? {});
  }

  @MessagePattern('orders.findOne')
  findOne(@Payload() d: { id: string; businessId: string }) {
    return this.orderService.findOne(d.id, d.businessId);
  }
}
