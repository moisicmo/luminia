import { prisma } from '@/lib/prisma';
import 'dotenv/config';

// UUIDs fijos — deben coincidir con luminia-users/prisma/seed.ts
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_SYSTEM_ID = '00000000-0000-0000-0000-000000000002';

export const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

async function main() {
  console.log('Seeding luminia-business...');

  // 1. Business: Luminia como empresa registrada en su propia plataforma
  const business = await prisma.business.upsert({
    where: { id: LUMINIA_BUSINESS_ID },
    update: {},
    create: {
      id: LUMINIA_BUSINESS_ID,
      ownerId: LUMINIA_PERSON_ID,
      name: 'Luminia',
      businessType: 'ADMINISTRACION',
      url: 'https://luminia.com',
      active: true,
      createdBy: LUMINIA_PERSON_ID,
    },
  });
  console.log(`  Business: ${business.name}`);

  // 2. SystemAssignment: Luminia tiene acceso a su propio sistema
  await prisma.systemAssignment.upsert({
    where: {
      businessId_systemId: {
        businessId: LUMINIA_BUSINESS_ID,
        systemId: LUMINIA_SYSTEM_ID,
      },
    },
    update: {},
    create: {
      businessId: LUMINIA_BUSINESS_ID,
      systemId: LUMINIA_SYSTEM_ID,
      createdBy: LUMINIA_PERSON_ID,
    },
  });
  console.log(`  SystemAssignment: Luminia ↔ Luminia Platform`);

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
