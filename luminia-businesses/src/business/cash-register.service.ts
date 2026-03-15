import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';
import { Decimal } from '@prisma/client/runtime/client';

@Injectable()
export class CashRegisterService {
  private readonly logger = new Logger(CashRegisterService.name);

  async open(
    dto: { pointOfSaleId: string; openingAmount: number; notes?: string },
    businessId: string,
    userId: string,
  ) {
    this.logger.log(`[cash.open] posId=${dto.pointOfSaleId} user=${userId}`);
    try {
      // Validate POS exists and belongs to this business
      const pos = await prisma.pointOfSale.findFirst({
        where: { id: dto.pointOfSaleId, active: true, branch: { businessId, active: true } },
        include: { branch: true },
      });
      if (!pos) {
        throw new RpcException({ status: 404, message: 'Punto de venta no encontrado' });
      }

      // Check no open session on this POS
      const existing = await prisma.cashRegisterSession.findFirst({
        where: { pointOfSaleId: dto.pointOfSaleId, status: 'OPEN' },
      });
      if (existing) {
        throw new RpcException({ status: 409, message: 'Ya existe una caja abierta en este punto de venta' });
      }

      const openingAmount = new Decimal(dto.openingAmount);

      return await prisma.cashRegisterSession.create({
        data: {
          businessId,
          pointOfSaleId: dto.pointOfSaleId,
          cashierId: userId,
          status: 'OPEN',
          openingAmount,
          expectedAmount: openingAmount,
          notes: dto.notes,
          createdBy: userId,
        },
        include: {
          pointOfSale: { include: { branch: true } },
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[cash.open] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al abrir caja' });
    }
  }

  async close(
    sessionId: string,
    dto: { closingAmount: number; notes?: string },
    userId: string,
  ) {
    this.logger.log(`[cash.close] sessionId=${sessionId}`);
    try {
      const session = await prisma.cashRegisterSession.findFirst({
        where: { id: sessionId, status: 'OPEN' },
      });
      if (!session) {
        throw new RpcException({ status: 404, message: 'Sesión de caja no encontrada o ya cerrada' });
      }
      if (session.cashierId !== userId) {
        throw new RpcException({ status: 403, message: 'Solo el cajero que abrió puede cerrar esta caja' });
      }

      const closingAmount = new Decimal(dto.closingAmount);
      const expectedAmount = session.expectedAmount ?? session.openingAmount;
      const difference = closingAmount.minus(expectedAmount);

      return await prisma.cashRegisterSession.update({
        where: { id: sessionId },
        data: {
          status: 'CLOSED',
          closingAmount,
          difference,
          closedAt: new Date(),
          notes: dto.notes ?? session.notes,
          updatedBy: userId,
        },
        include: {
          pointOfSale: { include: { branch: true } },
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[cash.close] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al cerrar caja' });
    }
  }

  async getActive(businessId: string, userId: string) {
    this.logger.log(`[cash.active] businessId=${businessId} user=${userId}`);
    try {
      const session = await prisma.cashRegisterSession.findFirst({
        where: { businessId, cashierId: userId, status: 'OPEN' },
        include: {
          pointOfSale: { include: { branch: true } },
        },
      });
      return session ?? null;
    } catch (err) {
      this.logger.error(`[cash.active] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener sesión activa' });
    }
  }

  async list(
    businessId: string,
    filters?: { pointOfSaleId?: string; status?: string; dateFrom?: string; dateTo?: string; take?: number; skip?: number },
  ) {
    this.logger.log(`[cash.list] businessId=${businessId}`);
    try {
      const where: any = { businessId };
      if (filters?.pointOfSaleId) where.pointOfSaleId = filters.pointOfSaleId;
      if (filters?.status) where.status = filters.status;
      if (filters?.dateFrom || filters?.dateTo) {
        where.openedAt = {};
        if (filters.dateFrom) where.openedAt.gte = new Date(filters.dateFrom);
        if (filters.dateTo) where.openedAt.lte = new Date(filters.dateTo);
      }

      const take = Math.min(filters?.take ?? 50, 100);
      const skip = filters?.skip ?? 0;

      const [items, total] = await Promise.all([
        prisma.cashRegisterSession.findMany({
          where,
          include: {
            pointOfSale: { include: { branch: { select: { id: true, name: true } } } },
          },
          orderBy: { openedAt: 'desc' },
          take,
          skip,
        }),
        prisma.cashRegisterSession.count({ where }),
      ]);

      return { items, total };
    } catch (err) {
      this.logger.error(`[cash.list] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar sesiones de caja' });
    }
  }

  async findOne(sessionId: string) {
    this.logger.log(`[cash.findOne] sessionId=${sessionId}`);
    try {
      const session = await prisma.cashRegisterSession.findFirst({
        where: { id: sessionId },
        include: {
          pointOfSale: { include: { branch: true } },
        },
      });
      if (!session) {
        throw new RpcException({ status: 404, message: 'Sesión de caja no encontrada' });
      }
      return session;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[cash.findOne] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener sesión de caja' });
    }
  }

  async addSaleAmount(sessionId: string, amount: number) {
    this.logger.log(`[cash.addSaleAmount] sessionId=${sessionId} amount=${amount}`);
    try {
      const session = await prisma.cashRegisterSession.findFirst({
        where: { id: sessionId, status: 'OPEN' },
      });
      if (!session) {
        throw new RpcException({ status: 404, message: 'Sesión de caja no encontrada o ya cerrada' });
      }
      const currentExpected = session.expectedAmount ?? session.openingAmount;
      const newExpected = currentExpected.add(new Decimal(amount));

      return await prisma.cashRegisterSession.update({
        where: { id: sessionId },
        data: { expectedAmount: newExpected },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[cash.addSaleAmount] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar monto de caja' });
    }
  }
}
