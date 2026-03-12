import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUIDs fijos — deben coincidir con los otros seeds
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

// ─── Vehículos de ejemplo ───────────────────────────────────────────────────

const VEHICLES = [
  {
    id: '00000000-0000-0000-0060-000000000001',
    plate: 'ABC-1234',
    brand: 'Mercedes-Benz',
    model: 'O500',
    year: 2022,
    capacity: 45,
    vehicleType: 'BUS',
  },
  {
    id: '00000000-0000-0000-0060-000000000002',
    plate: 'DEF-5678',
    brand: 'Toyota',
    model: 'HiAce',
    year: 2023,
    capacity: 15,
    vehicleType: 'MINIBUS',
  },
  {
    id: '00000000-0000-0000-0060-000000000003',
    plate: 'GHI-9012',
    brand: 'Hyundai',
    model: 'H1',
    year: 2024,
    capacity: 9,
    vehicleType: 'VAN',
  },
];

// ─── Rutas de ejemplo ───────────────────────────────────────────────────────

const ROUTES = [
  {
    id: '00000000-0000-0000-0061-000000000001',
    name: 'La Paz - Oruro',
    origin: 'Terminal de Buses La Paz',
    destination: 'Terminal de Buses Oruro',
    distanceKm: 230,
    durationMin: 210,
    routeType: 'INTERCITY',
  },
  {
    id: '00000000-0000-0000-0061-000000000002',
    name: 'La Paz - Cochabamba',
    origin: 'Terminal de Buses La Paz',
    destination: 'Terminal de Buses Cochabamba',
    distanceKm: 380,
    durationMin: 420,
    routeType: 'INTERCITY',
  },
  {
    id: '00000000-0000-0000-0061-000000000003',
    name: 'Ruta Colegio San Ignacio',
    origin: 'Zona Sur - Calle 21',
    destination: 'Colegio San Ignacio - Sopocachi',
    distanceKm: 12,
    durationMin: 40,
    routeType: 'SCHOOL',
  },
  {
    id: '00000000-0000-0000-0061-000000000004',
    name: 'Tour Salar de Uyuni',
    origin: 'La Paz',
    destination: 'Salar de Uyuni',
    distanceKm: 550,
    durationMin: 600,
    routeType: 'TOURISM',
  },
];

// Paradas intermedias
const ROUTE_STOPS = [
  // La Paz - Oruro
  {
    id: '00000000-0000-0000-0062-000000000001',
    routeIdx: 0,
    name: 'Peaje Senkata',
    order: 1,
    arrivalOffsetMin: 30,
  },
  {
    id: '00000000-0000-0000-0062-000000000002',
    routeIdx: 0,
    name: 'Caracollo',
    order: 2,
    arrivalOffsetMin: 150,
  },
  // La Paz - Cochabamba
  {
    id: '00000000-0000-0000-0062-000000000003',
    routeIdx: 1,
    name: 'Oruro (parada)',
    order: 1,
    arrivalOffsetMin: 210,
  },
  {
    id: '00000000-0000-0000-0062-000000000004',
    routeIdx: 1,
    name: 'Confital',
    order: 2,
    arrivalOffsetMin: 330,
  },
  // Ruta escolar
  {
    id: '00000000-0000-0000-0062-000000000005',
    routeIdx: 2,
    name: 'Parada Megacenter',
    order: 1,
    arrivalOffsetMin: 10,
  },
  {
    id: '00000000-0000-0000-0062-000000000006',
    routeIdx: 2,
    name: 'Parada Plaza Avaroa',
    order: 2,
    arrivalOffsetMin: 25,
  },
];

async function main() {
  console.log('Seeding luminia-transports...');

  // 1. Vehículos
  for (const v of VEHICLES) {
    await prisma.vehicle.upsert({
      where: {
        businessId_plate: {
          businessId: LUMINIA_BUSINESS_ID,
          plate: v.plate,
        },
      },
      update: {},
      create: {
        id: v.id,
        businessId: LUMINIA_BUSINESS_ID,
        plate: v.plate,
        brand: v.brand,
        model: v.model,
        year: v.year,
        capacity: v.capacity,
        vehicleType: v.vehicleType as any,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
  }
  console.log(`  Vehicles: ${VEHICLES.length} vehículos`);

  // 2. Asientos para el bus (primer vehículo)
  const BUS_ID = VEHICLES[0].id;
  const seatCodes: string[] = [];
  for (let row = 1; row <= 11; row++) {
    for (const col of ['A', 'B', 'C', 'D']) {
      seatCodes.push(`${row}${col}`);
    }
  }
  const seats = seatCodes.slice(0, 44);
  for (const code of seats) {
    const seatId = `00000000-0000-0000-0063-${code.padStart(12, '0')}`;
    await prisma.seat.upsert({
      where: {
        vehicleId_seatCode: {
          vehicleId: BUS_ID,
          seatCode: code,
        },
      },
      update: {},
      create: {
        id: seatId,
        vehicleId: BUS_ID,
        seatCode: code,
        floor: 1,
        seatType: 'STANDARD',
        active: true,
      },
    });
  }
  console.log(`  Seats: ${seats.length} asientos para bus ${VEHICLES[0].plate}`);

  // 3. Rutas
  for (const r of ROUTES) {
    await prisma.route.upsert({
      where: { id: r.id },
      update: {},
      create: {
        id: r.id,
        businessId: LUMINIA_BUSINESS_ID,
        name: r.name,
        origin: r.origin,
        destination: r.destination,
        distanceKm: r.distanceKm,
        durationMin: r.durationMin,
        routeType: r.routeType as any,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
  }
  console.log(`  Routes: ${ROUTES.length} rutas`);

  // 4. Paradas de ruta
  for (const stop of ROUTE_STOPS) {
    await prisma.routeStop.upsert({
      where: { id: stop.id },
      update: {},
      create: {
        id: stop.id,
        routeId: ROUTES[stop.routeIdx].id,
        name: stop.name,
        order: stop.order,
        arrivalOffsetMin: stop.arrivalOffsetMin,
      },
    });
  }
  console.log(`  RouteStops: ${ROUTE_STOPS.length} paradas`);

  // 5. Programación escolar (ruta del colegio, lunes a viernes)
  const SCHOOL_SCHEDULE_ID = '00000000-0000-0000-0064-000000000001';
  await prisma.routeSchedule.upsert({
    where: { id: SCHOOL_SCHEDULE_ID },
    update: {},
    create: {
      id: SCHOOL_SCHEDULE_ID,
      routeId: ROUTES[2].id,
      vehicleId: VEHICLES[2].id,
      daysOfWeek: [1, 2, 3, 4, 5],
      departureTime: '07:00',
      returnTime: '13:30',
      validFrom: new Date('2026-02-01'),
      validUntil: new Date('2026-11-30'),
      active: true,
      createdBy: LUMINIA_PERSON_ID,
    },
  });
  console.log('  RouteSchedules: 1 programación escolar');

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
