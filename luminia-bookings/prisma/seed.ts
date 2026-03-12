import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUIDs fijos — deben coincidir con los otros seeds
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

// ─── Servicios reservables de ejemplo ───────────────────────────────────────

const SERVICES = [
  {
    id: '00000000-0000-0000-0020-000000000001',
    name: 'Consulta dental general',
    description: 'Revisión dental de rutina con limpieza incluida.',
    durationMin: 30,
    capacity: 1,
    price: 150,
    requiresProvider: true,
  },
  {
    id: '00000000-0000-0000-0020-000000000002',
    name: 'Corte de pelo caballero',
    description: 'Corte de pelo clásico o moderno para caballeros.',
    durationMin: 30,
    capacity: 1,
    price: 30,
    requiresProvider: true,
  },
  {
    id: '00000000-0000-0000-0020-000000000003',
    name: 'Clase de Spinning',
    description: 'Clase grupal de spinning de alta intensidad.',
    durationMin: 45,
    capacity: 20,
    price: 25,
    requiresProvider: true,
  },
  {
    id: '00000000-0000-0000-0020-000000000004',
    name: 'Masaje relajante',
    description: 'Sesión de masaje de cuerpo completo para relajación.',
    durationMin: 60,
    capacity: 1,
    price: 120,
    requiresProvider: true,
  },
  {
    id: '00000000-0000-0000-0020-000000000005',
    name: 'Consulta psicológica',
    description: 'Sesión de terapia individual con psicólogo.',
    durationMin: 50,
    capacity: 1,
    price: 200,
    requiresProvider: true,
  },
];

async function main() {
  console.log('Seeding luminia-bookings...');

  for (const svc of SERVICES) {
    await prisma.bookableService.upsert({
      where: { id: svc.id },
      update: {},
      create: {
        id: svc.id,
        businessId: LUMINIA_BUSINESS_ID,
        name: svc.name,
        description: svc.description,
        durationMin: svc.durationMin,
        capacity: svc.capacity,
        price: svc.price,
        currency: 'BOB',
        requiresProvider: svc.requiresProvider,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
  }
  console.log(`  BookableServices: ${SERVICES.length} servicios`);

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
