import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class TransactionService {
  private readonly logger = new Logger(TransactionService.name);

  // ─── Transactions ─────────────────────────────────────────────────────────

  async create(
    data: {
      businessId: string;
      direction: string;
      referenceType: string;
      referenceId?: string;
      amount: number;
      currency?: string;
      description?: string;
      methodConfigId?: string;
    },
    createdBy: string,
  ) {
    try {
      const fee = 0;
      const netAmount = Number(data.amount) - fee;

      return await prisma.transaction.create({
        data: {
          businessId: data.businessId,
          direction: data.direction as any,
          referenceType: data.referenceType as any,
          referenceId: data.referenceId,
          amount: data.amount,
          fee,
          netAmount,
          currency: data.currency ?? 'BOB',
          description: data.description,
          methodConfigId: data.methodConfigId,
          status: 'PENDING',
          createdBy,
        },
      });
    } catch (err) {
      this.logger.error(`[transaction.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear transacción' });
    }
  }

  async confirm(
    id: string,
    gatewayRef: string,
    gatewayResponse: any,
    updatedBy: string,
  ) {
    try {
      const transaction = await prisma.transaction.findFirst({ where: { id } });
      if (!transaction) {
        throw new RpcException({ status: 404, message: 'Transacción no encontrada' });
      }
      if (!['PENDING', 'PROCESSING'].includes(transaction.status)) {
        throw new RpcException({
          status: 400,
          message: 'Solo se pueden confirmar transacciones en estado PENDING o PROCESSING',
        });
      }

      return await prisma.transaction.update({
        where: { id },
        data: {
          status: 'PAID',
          gatewayRef,
          gatewayResponse,
          processedAt: new Date(),
          updatedBy,
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[transaction.confirm] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al confirmar transacción' });
    }
  }

  async fail(id: string, reason: string, updatedBy: string) {
    try {
      const transaction = await prisma.transaction.findFirst({ where: { id } });
      if (!transaction) {
        throw new RpcException({ status: 404, message: 'Transacción no encontrada' });
      }

      return await prisma.transaction.update({
        where: { id },
        data: { status: 'FAILED', failureReason: reason, updatedBy },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[transaction.fail] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al marcar transacción como fallida' });
    }
  }

  async cancel(id: string, updatedBy: string) {
    try {
      const transaction = await prisma.transaction.findFirst({ where: { id } });
      if (!transaction) {
        throw new RpcException({ status: 404, message: 'Transacción no encontrada' });
      }
      if (transaction.status !== 'PENDING') {
        throw new RpcException({
          status: 400,
          message: 'Solo se pueden cancelar transacciones en estado PENDING',
        });
      }

      return await prisma.transaction.update({
        where: { id },
        data: { status: 'CANCELLED', updatedBy },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[transaction.cancel] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al cancelar transacción' });
    }
  }

  async list(
    businessId: string,
    filters: {
      direction?: string;
      referenceType?: string;
      referenceId?: string;
      status?: string;
      from?: string;
      to?: string;
    },
  ) {
    try {
      const where: any = { businessId };

      if (filters.direction) where.direction = filters.direction;
      if (filters.referenceType) where.referenceType = filters.referenceType;
      if (filters.referenceId) where.referenceId = filters.referenceId;
      if (filters.status) where.status = filters.status;
      if (filters.from || filters.to) {
        where.createdAt = {};
        if (filters.from) where.createdAt.gte = new Date(filters.from);
        if (filters.to) where.createdAt.lte = new Date(filters.to);
      }

      return await prisma.transaction.findMany({
        where,
        orderBy: { createdAt: 'desc' },
        take: 50,
      });
    } catch (err) {
      this.logger.error(`[transaction.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar transacciones' });
    }
  }

  async findOne(id: string, businessId: string) {
    try {
      const transaction = await prisma.transaction.findFirst({
        where: { id, businessId },
        include: { methodConfig: true },
      });
      if (!transaction) {
        throw new RpcException({ status: 404, message: 'Transacción no encontrada' });
      }
      return transaction;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[transaction.findOne] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener transacción' });
    }
  }

  // ─── Payment Methods ──────────────────────────────────────────────────────

  async configureMethod(
    businessId: string,
    data: { type: string; name: string; credentials?: any; isDefault?: boolean },
    createdBy: string,
  ) {
    try {
      if (data.isDefault) {
        await prisma.paymentMethodConfig.updateMany({
          where: { businessId, isDefault: true },
          data: { isDefault: false },
        });
      }

      return await prisma.paymentMethodConfig.create({
        data: {
          businessId,
          type: data.type as any,
          name: data.name,
          credentials: data.credentials,
          isDefault: data.isDefault ?? false,
          createdBy,
        },
      });
    } catch (err) {
      this.logger.error(`[configureMethod] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al configurar método de pago' });
    }
  }

  async listMethods(businessId: string) {
    try {
      return await prisma.paymentMethodConfig.findMany({
        where: { businessId, active: true },
        orderBy: { createdAt: 'asc' },
      });
    } catch (err) {
      this.logger.error(`[listMethods] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar métodos de pago' });
    }
  }
}
