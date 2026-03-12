import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUIDs fijos — deben coincidir con luminia-users y luminia-business seeds
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

// Métodos de pago por defecto que Luminia habilita en su propio negocio.
// Cada negocio cliente configura los suyos propios al registrarse.
const PAYMENT_METHODS = [
  {
    id: '00000000-0000-0000-0004-000000000001',
    type: 'CASH' as const,
    name: 'Efectivo',
    isDefault: true,
  },
  {
    id: '00000000-0000-0000-0004-000000000002',
    type: 'BANK_TRANSFER' as const,
    name: 'Transferencia bancaria',
    isDefault: false,
  },
  {
    id: '00000000-0000-0000-0004-000000000003',
    type: 'QR_SIMPLE' as const,
    name: 'QR Simple Bolivia',
    isDefault: false,
  },
];

async function main() {
  console.log('Seeding luminia-payments...');

  for (const method of PAYMENT_METHODS) {
    await prisma.paymentMethodConfig.upsert({
      where: { id: method.id },
      update: {},
      create: {
        id: method.id,
        businessId: LUMINIA_BUSINESS_ID,
        type: method.type,
        name: method.name,
        // credentials: null,
        isDefault: method.isDefault,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
    console.log(`  PaymentMethodConfig: ${method.name} (${method.type})${method.isDefault ? ' [default]' : ''}`);
  }

  // Billetera de Luminia como negocio (para recibir pagos de suscripciones)
  await prisma.wallet.upsert({
    where: {
      businessId_ownerId_ownerType_currency: {
        businessId: LUMINIA_BUSINESS_ID,
        ownerId: LUMINIA_BUSINESS_ID,
        ownerType: 'BUSINESS',
        currency: 'BOB',
      },
    },
    update: {},
    create: {
      businessId: LUMINIA_BUSINESS_ID,
      ownerId: LUMINIA_BUSINESS_ID,
      ownerType: 'BUSINESS',
      balance: 0,
      currency: 'BOB',
      active: true,
    },
  });
  console.log('  Wallet: Luminia (BUSINESS / BOB)');

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
