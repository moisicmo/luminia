import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class PlanService {
  private readonly logger = new Logger(PlanService.name);

  // ─── Platform Plans ────────────────────────────────────────────────────────

  async createPlatformPlan(
    data: {
      name: string;
      description?: string;
      price: number;
      currency?: string;
      interval: string;
      intervalCount?: number;
      trialDays?: number;
      maxProducts?: number;
      maxMembers?: number;
      maxWarehouses?: number;
      modules?: string[];
      isPublic?: boolean;
      sortOrder?: number;
    },
    createdBy: string,
  ) {
    try {
      return await prisma.platformPlan.create({ data: { ...data as any, createdBy } });
    } catch (err) {
      this.logger.error(`[platformPlan.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear plan de plataforma' });
    }
  }

  async listPlatformPlans(onlyPublic = true) {
    try {
      return await prisma.platformPlan.findMany({
        where: onlyPublic ? { active: true, isPublic: true } : { active: true },
        orderBy: [{ sortOrder: 'asc' }, { price: 'asc' }],
      });
    } catch (err) {
      this.logger.error(`[platformPlan.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar planes de plataforma' });
    }
  }

  async updatePlatformPlan(id: string, data: Record<string, any>) {
    try {
      const existing = await prisma.platformPlan.findFirst({ where: { id, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Plan no encontrado' });
      return await prisma.platformPlan.update({ where: { id }, data });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[platformPlan.update] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar plan' });
    }
  }

  // ─── Business Plans ────────────────────────────────────────────────────────

  async createPlan(
    data: {
      businessId: string;
      name: string;
      description?: string;
      price: number;
      currency?: string;
      interval: string;
      intervalCount?: number;
      trialDays?: number;
      features?: any;
      maxSlots?: number;
      sortOrder?: number;
    },
    createdBy: string,
  ) {
    try {
      return await prisma.plan.create({ data: { ...data as any, createdBy } });
    } catch (err) {
      this.logger.error(`[plan.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear plan' });
    }
  }

  async listPlans(businessId: string) {
    try {
      return await prisma.plan.findMany({
        where: { businessId, active: true },
        include: { _count: { select: { subscriptions: true } } },
        orderBy: [{ sortOrder: 'asc' }, { price: 'asc' }],
      });
    } catch (err) {
      this.logger.error(`[plan.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar planes' });
    }
  }

  async updatePlan(
    id: string,
    businessId: string,
    data: Record<string, any>,
    updatedBy: string,
  ) {
    try {
      const existing = await prisma.plan.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Plan no encontrado' });
      return await prisma.plan.update({ where: { id }, data: { ...data, updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[plan.update] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar plan' });
    }
  }

  async removePlan(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.plan.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Plan no encontrado' });

      const active = await prisma.subscription.count({
        where: { planId: id, status: { in: ['TRIAL', 'ACTIVE', 'PENDING'] as any } },
      });
      if (active > 0) {
        throw new RpcException({
          status: 400,
          message: 'No se puede eliminar un plan con suscripciones activas',
        });
      }

      await prisma.plan.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[plan.remove] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al eliminar plan' });
    }
  }
}
