import { prisma } from '@/lib/prisma';
import 'dotenv/config';

// UUIDs fijos — deben coincidir con luminia-users y luminia-business seeds
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

const PLANS = [
  {
    name: 'Starter',
    price: 0.0,
    discountPrice: 0.0,
    billingCycle: 'MENSUAL' as const,
    seatLimit: 1,
    trialDays: 14,
    features: JSON.stringify([
      '1 usuario',
      '1 sucursal',
      'Tienda virtual básica',
      'Soporte por email',
    ]),
  },
  {
    name: 'Emprendedor',
    price: 29.99,
    discountPrice: 29.99,
    billingCycle: 'MENSUAL' as const,
    seatLimit: 5,
    trialDays: 14,
    features: JSON.stringify([
      '5 usuarios',
      '2 sucursales',
      'Tienda virtual completa',
      'Reportes básicos',
      'Soporte por email y chat',
    ]),
  },
  {
    name: 'Negocio',
    price: 79.99,
    discountPrice: 79.99,
    billingCycle: 'MENSUAL' as const,
    seatLimit: 20,
    trialDays: 14,
    features: JSON.stringify([
      '20 usuarios',
      'Sucursales ilimitadas',
      'Tienda virtual + POS',
      'Reportes avanzados',
      'Integraciones',
      'Soporte prioritario',
    ]),
  },
  {
    name: 'Enterprise',
    price: 199.99,
    discountPrice: 199.99,
    billingCycle: 'MENSUAL' as const,
    seatLimit: 100,
    trialDays: 30,
    features: JSON.stringify([
      'Usuarios ilimitados',
      'Sucursales ilimitadas',
      'Todas las funcionalidades',
      'API dedicada',
      'Onboarding personalizado',
      'Soporte 24/7',
    ]),
  },
];

async function main() {
  console.log('Seeding luminia-suscriptions...');

  for (const plan of PLANS) {
    const created = await prisma.plan.upsert({
      where: {
        id: `00000000-0000-0000-0001-${String(PLANS.indexOf(plan) + 1).padStart(12, '0')}`,
      },
      update: {},
      create: {
        id: `00000000-0000-0000-0001-${String(PLANS.indexOf(plan) + 1).padStart(12, '0')}`,
        businessId: LUMINIA_BUSINESS_ID,
        name: plan.name,
        price: plan.price,
        discountPrice: plan.discountPrice,
        billingCycle: plan.billingCycle,
        seatLimit: plan.seatLimit,
        trialDays: plan.trialDays,
        features: plan.features,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
    console.log(`  Plan: ${created.name} — $${created.price}/${created.billingCycle} (${plan.seatLimit} seats, ${plan.trialDays}d trial)`);
  }

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
