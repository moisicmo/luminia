import { Injectable, Logger, NotFoundException, BadRequestException } from '@nestjs/common';
import { prisma } from '@/lib/prisma';

const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

@Injectable()
export class SubscriptionsService {
  private readonly logger = new Logger(SubscriptionsService.name);

  // ─── Platform Plans ────────────────────────────────────────────────────────

  async listPlatformPlans(onlyPublic: boolean = true) {
    const plans = await prisma.plan.findMany({
      where: {
        businessId: LUMINIA_BUSINESS_ID,
        ...(onlyPublic ? { active: true } : {}),
      },
      orderBy: { price: 'asc' },
    });
    return plans.map((p) => ({
      ...p,
      price: Number(p.price),
      discountPrice: Number(p.discountPrice),
      features: p.features ? JSON.parse(p.features) : [],
    }));
  }

  // ─── Platform Subscriptions ────────────────────────────────────────────────

  async subscribeToPlatform(businessId: string, planId: string, createdBy: string) {
    const plan = await prisma.plan.findFirst({
      where: { id: planId, businessId: LUMINIA_BUSINESS_ID, active: true },
    });
    if (!plan) throw new NotFoundException('Plan no encontrado');

    const existing = await prisma.subscription.findFirst({
      where: {
        ownerId: businessId,
        ownerType: 'BUSINESS',
        status: { in: ['TRIAL', 'ACTIVE'] },
      },
    });
    if (existing) throw new BadRequestException('El negocio ya tiene una suscripción activa');

    const now = new Date();
    const trialEndsAt = plan.trialDays > 0
      ? new Date(now.getTime() + plan.trialDays * 24 * 60 * 60 * 1000)
      : null;
    const endDate = new Date(now);
    endDate.setMonth(endDate.getMonth() + 1);

    const subscription = await prisma.subscription.create({
      data: {
        planId,
        ownerId: businessId,
        ownerType: 'BUSINESS',
        status: plan.trialDays > 0 ? 'TRIAL' : 'ACTIVE',
        startDate: now,
        endDate,
        trialEndsAt,
        createdBy,
      },
      include: { plan: true },
    });

    return {
      ...subscription,
      plan: {
        ...subscription.plan,
        price: Number(subscription.plan.price),
        discountPrice: Number(subscription.plan.discountPrice),
        features: subscription.plan.features ? JSON.parse(subscription.plan.features) : [],
      },
    };
  }

  async getPlatformSubscription(businessId: string) {
    const subscription = await prisma.subscription.findFirst({
      where: { ownerId: businessId, ownerType: 'BUSINESS' },
      include: { plan: true },
      orderBy: { createdAt: 'desc' },
    });
    if (!subscription) throw new NotFoundException('Suscripción no encontrada');
    return {
      ...subscription,
      plan: {
        ...subscription.plan,
        price: Number(subscription.plan.price),
        discountPrice: Number(subscription.plan.discountPrice),
        features: subscription.plan.features ? JSON.parse(subscription.plan.features) : [],
      },
    };
  }

  async cancelPlatformSubscription(businessId: string, reason: string, updatedBy: string) {
    const subscription = await prisma.subscription.findFirst({
      where: { ownerId: businessId, ownerType: 'BUSINESS', status: { in: ['TRIAL', 'ACTIVE'] } },
    });
    if (!subscription) throw new NotFoundException('Suscripción activa no encontrada');

    return prisma.subscription.update({
      where: { id: subscription.id },
      data: { status: 'CANCELLED', updatedBy },
    });
  }

  // ─── Business Plans ────────────────────────────────────────────────────────

  async createPlan(data: any, createdBy: string) {
    const { businessId, name, price, discountPrice, billingCycle, seatLimit, trialDays, features } = data;
    return prisma.plan.create({
      data: {
        businessId,
        name,
        price: price ?? 0,
        discountPrice: discountPrice ?? price ?? 0,
        billingCycle: billingCycle ?? 'MENSUAL',
        seatLimit: seatLimit ?? 1,
        trialDays: trialDays ?? 0,
        features: features ? JSON.stringify(features) : null,
        active: true,
        createdBy,
      },
    });
  }

  async listPlans(businessId: string) {
    const plans = await prisma.plan.findMany({
      where: { businessId },
      orderBy: { price: 'asc' },
    });
    return plans.map((p) => ({
      ...p,
      price: Number(p.price),
      discountPrice: Number(p.discountPrice),
      features: p.features ? JSON.parse(p.features) : [],
    }));
  }

  async updatePlan(id: string, businessId: string, data: any, updatedBy: string) {
    const plan = await prisma.plan.findFirst({ where: { id, businessId } });
    if (!plan) throw new NotFoundException('Plan no encontrado');
    const { features, price, discountPrice, ...rest } = data;
    return prisma.plan.update({
      where: { id },
      data: {
        ...rest,
        ...(price !== undefined ? { price } : {}),
        ...(discountPrice !== undefined ? { discountPrice } : {}),
        ...(features !== undefined ? { features: JSON.stringify(features) } : {}),
        updatedBy,
      },
    });
  }

  async removePlan(id: string, businessId: string, updatedBy: string) {
    const plan = await prisma.plan.findFirst({ where: { id, businessId } });
    if (!plan) throw new NotFoundException('Plan no encontrado');
    return prisma.plan.update({
      where: { id },
      data: { active: false, updatedBy },
    });
  }

  // ─── Customer Subscriptions ────────────────────────────────────────────────

  async subscribe(data: any, createdBy: string) {
    const { businessId, planId, customerId } = data;
    const plan = await prisma.plan.findFirst({ where: { id: planId, businessId, active: true } });
    if (!plan) throw new NotFoundException('Plan no encontrado');

    const now = new Date();
    const trialEndsAt = plan.trialDays > 0
      ? new Date(now.getTime() + plan.trialDays * 24 * 60 * 60 * 1000)
      : null;
    const endDate = new Date(now);
    endDate.setMonth(endDate.getMonth() + 1);

    return prisma.subscription.create({
      data: {
        planId,
        ownerId: customerId,
        ownerType: 'CUSTOMER',
        status: plan.trialDays > 0 ? 'TRIAL' : 'ACTIVE',
        startDate: now,
        endDate,
        trialEndsAt,
        createdBy,
      },
    });
  }

  async listSubscriptions(businessId: string, filters: any) {
    const { customerId, status, planId } = filters ?? {};
    const planIds = await prisma.plan
      .findMany({ where: { businessId }, select: { id: true } })
      .then((ps) => ps.map((p) => p.id));

    return prisma.subscription.findMany({
      where: {
        planId: planId ? planId : { in: planIds },
        ...(customerId ? { ownerId: customerId } : {}),
        ...(status ? { status } : {}),
      },
      include: { plan: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async getSubscription(id: string, businessId: string) {
    const planIds = await prisma.plan
      .findMany({ where: { businessId }, select: { id: true } })
      .then((ps) => ps.map((p) => p.id));

    const sub = await prisma.subscription.findFirst({
      where: { id, planId: { in: planIds } },
      include: { plan: true },
    });
    if (!sub) throw new NotFoundException('Suscripción no encontrada');
    return sub;
  }

  async cancelSubscription(id: string, businessId: string, reason: string, updatedBy: string) {
    const planIds = await prisma.plan
      .findMany({ where: { businessId }, select: { id: true } })
      .then((ps) => ps.map((p) => p.id));

    const sub = await prisma.subscription.findFirst({
      where: { id, planId: { in: planIds } },
    });
    if (!sub) throw new NotFoundException('Suscripción no encontrada');

    return prisma.subscription.update({
      where: { id },
      data: { status: 'CANCELLED', updatedBy },
    });
  }
}
