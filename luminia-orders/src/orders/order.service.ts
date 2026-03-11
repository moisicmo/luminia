import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class OrderService {
  private readonly logger = new Logger(OrderService.name);

  // ─── Create ───────────────────────────────────────────────────────────────

  async create(
    data: {
      businessId: string;
      branchId?: string;
      customerId?: string;
      customerName?: string;
      sellerId?: string;
      type?: string;
      currency?: string;
      notes?: string;
      items: {
        itemType: string;
        itemId?: string;
        name: string;
        sku?: string;
        unitName?: string;
        quantity: number;
        unitPrice: number;
        discount?: number;
        taxRate?: number;
        notes?: string;
      }[];
    },
    createdBy: string,
  ) {
    try {
      const year = new Date().getFullYear();
      const count = await prisma.order.count({ where: { businessId: data.businessId } });
      const orderNumber = `${year}-${String(count + 1).padStart(5, '0')}`;

      let subtotal = 0;
      let taxAmount = 0;

      const itemsData = data.items.map((item) => {
        const qty = Number(item.quantity);
        const unitPrice = Number(item.unitPrice);
        const discount = Number(item.discount ?? 0);
        const itemSubtotal = qty * unitPrice - discount;
        const itemTaxAmount = item.taxRate
          ? itemSubtotal * (Number(item.taxRate) / 100)
          : 0;
        const itemTotal = itemSubtotal + itemTaxAmount;

        subtotal += itemSubtotal;
        taxAmount += itemTaxAmount;

        return {
          itemType: item.itemType as any,
          itemId: item.itemId,
          name: item.name,
          sku: item.sku,
          unitName: item.unitName,
          quantity: qty,
          unitPrice,
          discount,
          taxRate: item.taxRate,
          taxAmount: itemTaxAmount,
          subtotal: itemSubtotal,
          total: itemTotal,
          notes: item.notes,
        };
      });

      const total = subtotal + taxAmount;

      return await prisma.$transaction(async (tx) => {
        const order = await tx.order.create({
          data: {
            businessId: data.businessId,
            branchId: data.branchId,
            customerId: data.customerId,
            customerName: data.customerName,
            sellerId: data.sellerId,
            type: data.type as any,
            currency: data.currency,
            notes: data.notes,
            orderNumber,
            subtotal,
            taxAmount,
            total,
            amountPaid: 0,
            amountDue: total,
            createdBy,
            items: { create: itemsData },
          },
          include: { items: true },
        });
        return order;
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear la orden' });
    }
  }

  // ─── Add Item ─────────────────────────────────────────────────────────────

  async addItem(
    orderId: string,
    businessId: string,
    item: {
      itemType: string;
      itemId?: string;
      name: string;
      sku?: string;
      unitName?: string;
      quantity: number;
      unitPrice: number;
      discount?: number;
      taxRate?: number;
      notes?: string;
    },
    createdBy: string,
  ) {
    try {
      const order = await prisma.order.findFirst({
        where: { id: orderId, businessId },
        include: { items: true },
      });
      if (!order) throw new RpcException({ status: 404, message: 'Orden no encontrada' });
      if (order.status !== 'DRAFT') {
        throw new RpcException({ status: 400, message: 'Solo se pueden modificar órdenes en estado DRAFT' });
      }

      const qty = Number(item.quantity);
      const unitPrice = Number(item.unitPrice);
      const discount = Number(item.discount ?? 0);
      const itemSubtotal = qty * unitPrice - discount;
      const itemTaxAmount = item.taxRate
        ? itemSubtotal * (Number(item.taxRate) / 100)
        : 0;
      const itemTotal = itemSubtotal + itemTaxAmount;

      await prisma.orderItem.create({
        data: {
          orderId,
          itemType: item.itemType as any,
          itemId: item.itemId,
          name: item.name,
          sku: item.sku,
          unitName: item.unitName,
          quantity: qty,
          unitPrice,
          discount,
          taxRate: item.taxRate,
          taxAmount: itemTaxAmount,
          subtotal: itemSubtotal,
          total: itemTotal,
          notes: item.notes,
        },
      });

      return await this.recalculateOrderTotals(orderId);
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.addItem] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al agregar ítem a la orden' });
    }
  }

  // ─── Remove Item ──────────────────────────────────────────────────────────

  async removeItem(orderId: string, itemId: string, businessId: string, updatedBy: string) {
    try {
      const order = await prisma.order.findFirst({ where: { id: orderId, businessId } });
      if (!order) throw new RpcException({ status: 404, message: 'Orden no encontrada' });
      if (order.status !== 'DRAFT') {
        throw new RpcException({ status: 400, message: 'Solo se pueden modificar órdenes en estado DRAFT' });
      }

      const item = await prisma.orderItem.findFirst({ where: { id: itemId, orderId } });
      if (!item) throw new RpcException({ status: 404, message: 'Ítem no encontrado' });

      await prisma.orderItem.delete({ where: { id: itemId } });

      return await this.recalculateOrderTotals(orderId, updatedBy);
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.removeItem] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al eliminar ítem de la orden' });
    }
  }

  // ─── Add Payment ──────────────────────────────────────────────────────────

  async addPayment(
    orderId: string,
    businessId: string,
    payment: { method: string; amount: number; currency?: string; reference?: string },
    createdBy: string,
  ) {
    try {
      const order = await prisma.order.findFirst({
        where: { id: orderId, businessId },
      });
      if (!order) throw new RpcException({ status: 404, message: 'Orden no encontrada' });
      if (!['DRAFT', 'CONFIRMED'].includes(order.status)) {
        throw new RpcException({
          status: 400,
          message: 'Solo se pueden agregar pagos a órdenes en estado DRAFT o CONFIRMED',
        });
      }

      await prisma.orderPayment.create({
        data: {
          orderId,
          method: payment.method as any,
          amount: payment.amount,
          currency: payment.currency ?? order.currency,
          status: 'PENDING',
          reference: payment.reference,
          createdBy,
        },
      });

      const newAmountPaid = Number(order.amountPaid) + Number(payment.amount);
      const newAmountDue = Math.max(0, Number(order.total) - newAmountPaid);

      return await prisma.order.update({
        where: { id: orderId },
        data: {
          amountPaid: newAmountPaid,
          amountDue: newAmountDue,
          updatedBy: createdBy,
        },
        include: { items: true, payments: true },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.addPayment] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al agregar pago a la orden' });
    }
  }

  // ─── Confirm Payment ──────────────────────────────────────────────────────

  async confirmPayment(
    orderId: string,
    paymentId: string,
    transactionId: string,
    updatedBy: string,
  ) {
    try {
      const orderPayment = await prisma.orderPayment.findFirst({
        where: { id: paymentId, orderId },
      });
      if (!orderPayment) {
        throw new RpcException({ status: 404, message: 'Pago no encontrado' });
      }

      await prisma.orderPayment.update({
        where: { id: paymentId },
        data: {
          status: 'PAID',
          paymentId: transactionId,
          paidAt: new Date(),
        },
      });

      const payments = await prisma.orderPayment.findMany({
        where: { orderId, status: 'PAID' },
      });
      const totalPaid = payments.reduce((sum, p) => sum + Number(p.amount), 0);
      const order = await prisma.order.findFirst({ where: { id: orderId } });
      const amountDue = Math.max(0, Number(order!.total) - totalPaid);

      return await prisma.order.update({
        where: { id: orderId },
        data: { amountPaid: totalPaid, amountDue, updatedBy },
        include: { items: true, payments: true },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.confirmPayment] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al confirmar pago' });
    }
  }

  // ─── Confirm ──────────────────────────────────────────────────────────────

  async confirm(orderId: string, businessId: string, updatedBy: string) {
    try {
      const order = await prisma.order.findFirst({ where: { id: orderId, businessId } });
      if (!order) throw new RpcException({ status: 404, message: 'Orden no encontrada' });
      if (order.status !== 'DRAFT') {
        throw new RpcException({ status: 400, message: 'Solo se pueden confirmar órdenes en estado DRAFT' });
      }

      return await prisma.order.update({
        where: { id: orderId },
        data: { status: 'CONFIRMED', confirmedAt: new Date(), updatedBy },
        include: { items: true, payments: true },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.confirm] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al confirmar la orden' });
    }
  }

  // ─── Cancel ───────────────────────────────────────────────────────────────

  async cancel(orderId: string, businessId: string, reason: string, updatedBy: string) {
    try {
      const order = await prisma.order.findFirst({ where: { id: orderId, businessId } });
      if (!order) throw new RpcException({ status: 404, message: 'Orden no encontrada' });
      if (order.status === 'DELIVERED') {
        throw new RpcException({ status: 400, message: 'No se puede cancelar una orden ya entregada' });
      }
      if (!['DRAFT', 'CONFIRMED'].includes(order.status)) {
        throw new RpcException({ status: 400, message: 'Solo se pueden cancelar órdenes en estado DRAFT o CONFIRMED' });
      }

      return await prisma.order.update({
        where: { id: orderId },
        data: {
          status: 'CANCELLED',
          cancelledAt: new Date(),
          cancelReason: reason,
          updatedBy,
        },
        include: { items: true, payments: true },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.cancel] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al cancelar la orden' });
    }
  }

  // ─── List ─────────────────────────────────────────────────────────────────

  async list(
    businessId: string,
    filters: {
      status?: string;
      customerId?: string;
      type?: string;
      from?: string;
      to?: string;
    },
  ) {
    try {
      const where: any = { businessId };

      if (filters.status) where.status = filters.status;
      if (filters.customerId) where.customerId = filters.customerId;
      if (filters.type) where.type = filters.type;
      if (filters.from || filters.to) {
        where.createdAt = {};
        if (filters.from) where.createdAt.gte = new Date(filters.from);
        if (filters.to) where.createdAt.lte = new Date(filters.to);
      }

      return await prisma.order.findMany({
        where,
        include: {
          _count: { select: { items: true } },
          payments: true,
        },
        orderBy: { createdAt: 'desc' },
        take: 50,
      });
    } catch (err) {
      this.logger.error(`[order.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar órdenes' });
    }
  }

  // ─── Find One ─────────────────────────────────────────────────────────────

  async findOne(id: string, businessId: string) {
    try {
      const order = await prisma.order.findFirst({
        where: { id, businessId },
        include: { items: true, payments: true },
      });
      if (!order) throw new RpcException({ status: 404, message: 'Orden no encontrada' });
      return order;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[order.findOne] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener la orden' });
    }
  }

  // ─── Private helpers ──────────────────────────────────────────────────────

  private async recalculateOrderTotals(orderId: string, updatedBy?: string) {
    const items = await prisma.orderItem.findMany({ where: { orderId } });

    let subtotal = 0;
    let taxAmount = 0;

    for (const item of items) {
      subtotal += Number(item.subtotal);
      taxAmount += Number(item.taxAmount);
    }

    const total = subtotal + taxAmount;
    const order = await prisma.order.findFirst({ where: { id: orderId } });
    const amountDue = Math.max(0, total - Number(order!.amountPaid));

    return await prisma.order.update({
      where: { id: orderId },
      data: { subtotal, taxAmount, total, amountDue, updatedBy },
      include: { items: true, payments: true },
    });
  }
}
