import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

// ─── Interval helper ──────────────────────────────────────────────────────────

function addInterval(date: Date, interval: string, count: number): Date {
  const d = new Date(date);
  switch (interval) {
    case 'MONTHLY':
      d.setMonth(d.getMonth() + count);
      break;
    case 'QUARTERLY':
      d.setMonth(d.getMonth() + count * 3);
      break;
    case 'ANNUAL':
      d.setFullYear(d.getFullYear() + count);
      break;
    case 'ONE_TIME':
      d.setFullYear(d.getFullYear() + 100);
      break;
  }
  return d;
}

@Injectable()
export class SubscriptionService {
  private readonly logger = new Logger(SubscriptionService.name);

  // ─── Platform Subscriptions ────────────────────────────────────────────────

  async subscribeToPlatform(businessId: string, planId: string, createdBy: string) {
    try {
      const plan = await prisma.platformPlan.findFirst({
        where: { id: planId, active: true },
      });
      if (!plan) {
        throw new RpcException({ status: 404, message: 'Plan de plataforma no encontrado' });
      }

      const now = new Date();

      if (plan.trialDays > 0) {
        const trialEnd = addInterval(now, 'MONTHLY', 0);
        trialEnd.setDate(trialEnd.getDate() + plan.trialDays);

        return await prisma.platformSubscription.create({
          data: {
            businessId,
            planId,
            createdBy,
            status: 'TRIAL',
            trialEndsAt: trialEnd,
            currentPeriodStart: now,
            currentPeriodEnd: trialEnd,
            nextBillingDate: trialEnd,
          },
          include: { plan: true },
        });
      } else {
        const periodEnd = addInterval(now, plan.interval, plan.intervalCount);
        const dueDate = new Date(now);
        dueDate.setDate(dueDate.getDate() + 3);

        const subscription = await prisma.platformSubscription.create({
          data: {
            businessId,
            planId,
            createdBy,
            status: 'PENDING',
            currentPeriodStart: now,
            currentPeriodEnd: periodEnd,
            nextBillingDate: periodEnd,
          },
          include: { plan: true },
        });

        await prisma.platformSubPayment.create({
          data: {
            subscriptionId: subscription.id,
            amount: plan.price,
            currency: plan.currency,
            status: 'PENDING',
            periodStart: now,
            periodEnd: periodEnd,
            dueDate,
          },
        });

        return subscription;
      }
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[subscribeToPlatform] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al suscribir al plan de plataforma' });
    }
  }

  async getPlatformSubscription(businessId: string) {
    try {
      const subscription = await prisma.platformSubscription.findUnique({
        where: { businessId },
        include: {
          plan: true,
          payments: {
            orderBy: { createdAt: 'desc' },
            take: 3,
          },
        },
      });
      if (!subscription) {
        throw new RpcException({ status: 404, message: 'Suscripción de plataforma no encontrada' });
      }
      return subscription;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[getPlatformSubscription] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener suscripción de plataforma' });
    }
  }

  async activatePlatformSubscription(
    subscriptionId: string,
    orderId: string,
    transactionId: string,
  ) {
    try {
      const subscription = await prisma.platformSubscription.findFirst({
        where: { id: subscriptionId },
        include: { plan: true },
      });
      if (!subscription) {
        throw new RpcException({ status: 404, message: 'Suscripción no encontrada' });
      }

      const pendingPayment = await prisma.platformSubPayment.findFirst({
        where: { subscriptionId, status: 'PENDING' },
        orderBy: { createdAt: 'desc' },
      });

      if (pendingPayment) {
        await prisma.platformSubPayment.update({
          where: { id: pendingPayment.id },
          data: { status: 'PAID', paidAt: new Date(), orderId, transactionId },
        });
      }

      const now = new Date();
      const periodEnd = addInterval(now, subscription.plan.interval, subscription.plan.intervalCount);

      return await prisma.platformSubscription.update({
        where: { id: subscriptionId },
        data: {
          status: 'ACTIVE',
          currentPeriodStart: now,
          currentPeriodEnd: periodEnd,
          nextBillingDate: periodEnd,
        },
        include: { plan: true },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[activatePlatformSubscription] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al activar suscripción de plataforma' });
    }
  }

  async cancelPlatformSubscription(businessId: string, reason: string, updatedBy: string) {
    try {
      const subscription = await prisma.platformSubscription.findUnique({
        where: { businessId },
      });
      if (!subscription) {
        throw new RpcException({ status: 404, message: 'Suscripción no encontrada' });
      }

      return await prisma.platformSubscription.update({
        where: { businessId },
        data: {
          status: 'CANCELLED',
          cancelledAt: new Date(),
          cancelReason: reason,
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[cancelPlatformSubscription] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al cancelar suscripción de plataforma' });
    }
  }

  // ─── Business Subscriptions ────────────────────────────────────────────────

  async subscribe(
    businessId: string,
    planId: string,
    customerId: string,
    customerName: string,
    createdBy: string,
    metadata?: any,
  ) {
    try {
      const plan = await prisma.plan.findFirst({
        where: { id: planId, businessId, active: true },
      });
      if (!plan) {
        throw new RpcException({ status: 404, message: 'Plan no encontrado' });
      }

      const now = new Date();

      if (plan.trialDays > 0) {
        const trialEnd = new Date(now);
        trialEnd.setDate(trialEnd.getDate() + plan.trialDays);

        return await prisma.subscription.create({
          data: {
            businessId,
            planId,
            customerId,
            customerName,
            createdBy,
            metadata,
            status: 'TRIAL',
            trialEndsAt: trialEnd,
            currentPeriodStart: now,
            currentPeriodEnd: trialEnd,
            nextBillingDate: trialEnd,
          },
          include: { plan: true },
        });
      } else {
        const periodEnd = addInterval(now, plan.interval, plan.intervalCount);
        const dueDate = new Date(now);
        dueDate.setDate(dueDate.getDate() + 3);

        const subscription = await prisma.subscription.create({
          data: {
            businessId,
            planId,
            customerId,
            customerName,
            createdBy,
            metadata,
            status: 'PENDING',
            currentPeriodStart: now,
            currentPeriodEnd: periodEnd,
            nextBillingDate: periodEnd,
          },
          include: { plan: true },
        });

        await prisma.subPayment.create({
          data: {
            subscriptionId: subscription.id,
            amount: plan.price,
            currency: plan.currency,
            status: 'PENDING',
            periodStart: now,
            periodEnd: periodEnd,
            dueDate,
          },
        });

        return subscription;
      }
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[subscribe] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear suscripción' });
    }
  }

  async listSubscriptions(
    businessId: string,
    filters: { customerId?: string; status?: string; planId?: string },
  ) {
    try {
      const where: any = { businessId };
      if (filters.customerId) where.customerId = filters.customerId;
      if (filters.status) where.status = filters.status;
      if (filters.planId) where.planId = filters.planId;

      return await prisma.subscription.findMany({
        where,
        include: { plan: true },
        orderBy: { createdAt: 'desc' },
      });
    } catch (err) {
      this.logger.error(`[listSubscriptions] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar suscripciones' });
    }
  }

  async getSubscription(id: string, businessId: string) {
    try {
      const subscription = await prisma.subscription.findFirst({
        where: { id, businessId },
        include: {
          plan: true,
          payments: { orderBy: { createdAt: 'desc' } },
        },
      });
      if (!subscription) {
        throw new RpcException({ status: 404, message: 'Suscripción no encontrada' });
      }
      return subscription;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[getSubscription] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener suscripción' });
    }
  }

  async activateSubscription(
    subscriptionId: string,
    orderId: string,
    transactionId: string,
  ) {
    try {
      const subscription = await prisma.subscription.findFirst({
        where: { id: subscriptionId },
        include: { plan: true },
      });
      if (!subscription) {
        throw new RpcException({ status: 404, message: 'Suscripción no encontrada' });
      }

      const pendingPayment = await prisma.subPayment.findFirst({
        where: { subscriptionId, status: 'PENDING' },
        orderBy: { createdAt: 'desc' },
      });

      if (pendingPayment) {
        await prisma.subPayment.update({
          where: { id: pendingPayment.id },
          data: { status: 'PAID', paidAt: new Date(), orderId, transactionId },
        });
      }

      const now = new Date();
      const periodEnd = addInterval(now, subscription.plan.interval, subscription.plan.intervalCount);

      return await prisma.subscription.update({
        where: { id: subscriptionId },
        data: {
          status: 'ACTIVE',
          currentPeriodStart: now,
          currentPeriodEnd: periodEnd,
          nextBillingDate: periodEnd,
        },
        include: { plan: true },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[activateSubscription] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al activar suscripción' });
    }
  }

  async cancelSubscription(
    id: string,
    businessId: string,
    reason: string,
    updatedBy: string,
  ) {
    try {
      const subscription = await prisma.subscription.findFirst({
        where: { id, businessId },
      });
      if (!subscription) {
        throw new RpcException({ status: 404, message: 'Suscripción no encontrada' });
      }

      return await prisma.subscription.update({
        where: { id },
        data: {
          status: 'CANCELLED',
          cancelledAt: new Date(),
          cancelReason: reason,
          updatedBy,
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[cancelSubscription] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al cancelar suscripción' });
    }
  }

  // ─── Renewal Cron ─────────────────────────────────────────────────────────

  async renewalCron() {
    const now = new Date();
    const dueIn3Days = new Date(now);
    dueIn3Days.setDate(dueIn3Days.getDate() + 3);

    let platformRenewalsCreated = 0;
    let subscriptionRenewalsCreated = 0;
    let platformSuspended = 0;
    let subscriptionSuspended = 0;

    try {
      // 1. Create new PlatformSubPayments for due platform subscriptions
      const duePlatformSubs = await prisma.platformSubscription.findMany({
        where: {
          status: 'ACTIVE',
          nextBillingDate: { lte: now },
        },
        include: { plan: true },
      });

      for (const sub of duePlatformSubs) {
        const periodEnd = addInterval(now, sub.plan.interval, sub.plan.intervalCount);
        await prisma.platformSubPayment.create({
          data: {
            subscriptionId: sub.id,
            amount: sub.plan.price,
            currency: sub.plan.currency,
            status: 'PENDING',
            periodStart: now,
            periodEnd,
            dueDate: dueIn3Days,
          },
        });
        platformRenewalsCreated++;
      }

      // 2. Create new SubPayments for due business subscriptions
      const dueSubscriptions = await prisma.subscription.findMany({
        where: {
          status: 'ACTIVE',
          nextBillingDate: { lte: now },
        },
        include: { plan: true },
      });

      for (const sub of dueSubscriptions) {
        const periodEnd = addInterval(now, sub.plan.interval, sub.plan.intervalCount);
        await prisma.subPayment.create({
          data: {
            subscriptionId: sub.id,
            amount: sub.plan.price,
            currency: sub.plan.currency,
            status: 'PENDING',
            periodStart: now,
            periodEnd,
            dueDate: dueIn3Days,
          },
        });
        subscriptionRenewalsCreated++;
      }

      // 3. Suspend platform subscriptions with overdue pending payments
      const overduePlatformPayments = await prisma.platformSubPayment.findMany({
        where: {
          status: 'PENDING',
          dueDate: { lt: now },
        },
        select: { subscriptionId: true },
        distinct: ['subscriptionId'],
      });

      for (const { subscriptionId } of overduePlatformPayments) {
        await prisma.platformSubscription.update({
          where: { id: subscriptionId },
          data: { status: 'SUSPENDED' },
        });
        platformSuspended++;
      }

      // 4. Suspend business subscriptions with overdue pending payments
      const overdueSubPayments = await prisma.subPayment.findMany({
        where: {
          status: 'PENDING',
          dueDate: { lt: now },
        },
        select: { subscriptionId: true },
        distinct: ['subscriptionId'],
      });

      for (const { subscriptionId } of overdueSubPayments) {
        await prisma.subscription.update({
          where: { id: subscriptionId },
          data: { status: 'SUSPENDED' },
        });
        subscriptionSuspended++;
      }

      return {
        platformRenewalsCreated,
        subscriptionRenewalsCreated,
        platformSuspended,
        subscriptionSuspended,
      };
    } catch (err) {
      this.logger.error(`[renewalCron] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error en cron de renovaciones' });
    }
  }
}
