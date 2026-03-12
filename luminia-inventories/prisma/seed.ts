import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUIDs fijos — deben coincidir con luminia-users y luminia-business seeds
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

// ─── Unidades de medida base para el negocio Luminia ─────────────────────────

const UNITS = [
  { id: '00000000-0000-0000-0005-000000000001', name: 'Unidad', abbreviation: 'und' },
  { id: '00000000-0000-0000-0005-000000000002', name: 'Kilogramo', abbreviation: 'kg' },
  { id: '00000000-0000-0000-0005-000000000003', name: 'Gramo', abbreviation: 'g' },
  { id: '00000000-0000-0000-0005-000000000004', name: 'Litro', abbreviation: 'L' },
  { id: '00000000-0000-0000-0005-000000000005', name: 'Mililitro', abbreviation: 'ml' },
  { id: '00000000-0000-0000-0005-000000000006', name: 'Metro', abbreviation: 'm' },
  { id: '00000000-0000-0000-0005-000000000007', name: 'Centímetro', abbreviation: 'cm' },
  { id: '00000000-0000-0000-0005-000000000008', name: 'Caja', abbreviation: 'caja' },
  { id: '00000000-0000-0000-0005-000000000009', name: 'Docena', abbreviation: 'doc' },
  { id: '00000000-0000-0000-0005-000000000010', name: 'Par', abbreviation: 'par' },
  { id: '00000000-0000-0000-0005-000000000011', name: 'Paquete', abbreviation: 'paq' },
  { id: '00000000-0000-0000-0005-000000000012', name: 'Rollo', abbreviation: 'roll' },
];

// Conversiones estándar entre unidades
const UNIT_CONVERSIONS = [
  // 1 kg = 1000 g
  { fromId: '00000000-0000-0000-0005-000000000002', toId: '00000000-0000-0000-0005-000000000003', factor: 1000 },
  // 1 L = 1000 ml
  { fromId: '00000000-0000-0000-0005-000000000004', toId: '00000000-0000-0000-0005-000000000005', factor: 1000 },
  // 1 m = 100 cm
  { fromId: '00000000-0000-0000-0005-000000000006', toId: '00000000-0000-0000-0005-000000000007', factor: 100 },
  // 1 docena = 12 unidades
  { fromId: '00000000-0000-0000-0005-000000000009', toId: '00000000-0000-0000-0005-000000000001', factor: 12 },
  // 1 par = 2 unidades
  { fromId: '00000000-0000-0000-0005-000000000010', toId: '00000000-0000-0000-0005-000000000001', factor: 2 },
];

// ─── Categorías de productos base ─────────────────────────────────────────────

const CATEGORIES = [
  { id: '00000000-0000-0000-0006-000000000001', name: 'Alimentos y Bebidas', description: 'Productos alimenticios, bebidas y abarrotes' },
  { id: '00000000-0000-0000-0006-000000000002', name: 'Electrónica', description: 'Dispositivos electrónicos, accesorios y tecnología' },
  { id: '00000000-0000-0000-0006-000000000003', name: 'Ropa y Accesorios', description: 'Prendas de vestir, calzado y accesorios de moda' },
  { id: '00000000-0000-0000-0006-000000000004', name: 'Hogar y Jardín', description: 'Artículos para el hogar, muebles y jardinería' },
  { id: '00000000-0000-0000-0006-000000000005', name: 'Salud y Belleza', description: 'Productos de higiene, cosmética y cuidado personal' },
  { id: '00000000-0000-0000-0006-000000000006', name: 'Deportes y Fitness', description: 'Equipamiento deportivo, ropa técnica y suplementos' },
  { id: '00000000-0000-0000-0006-000000000007', name: 'Servicios', description: 'Servicios y mano de obra no físicos' },
  { id: '00000000-0000-0000-0006-000000000008', name: 'Otros', description: 'Productos varios sin categoría específica' },
];

async function main() {
  console.log('Seeding luminia-inventory...');

  // 1. Unidades
  for (const unit of UNITS) {
    await prisma.unit.upsert({
      where: { id: unit.id },
      update: {},
      create: {
        id: unit.id,
        businessId: LUMINIA_BUSINESS_ID,
        name: unit.name,
        abbreviation: unit.abbreviation,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
  }
  console.log(`  Units: ${UNITS.length} unidades de medida`);

  // 2. Conversiones entre unidades
  for (const conv of UNIT_CONVERSIONS) {
    await prisma.unitConversion.upsert({
      where: {
        businessId_fromUnitId_toUnitId: {
          businessId: LUMINIA_BUSINESS_ID,
          fromUnitId: conv.fromId,
          toUnitId: conv.toId,
        },
      },
      update: {},
      create: {
        businessId: LUMINIA_BUSINESS_ID,
        fromUnitId: conv.fromId,
        toUnitId: conv.toId,
        factor: conv.factor,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
  }
  console.log(`  UnitConversions: ${UNIT_CONVERSIONS.length} conversiones`);

  // 3. Categorías
  for (const cat of CATEGORIES) {
    await prisma.category.upsert({
      where: { id: cat.id },
      update: {},
      create: {
        id: cat.id,
        businessId: LUMINIA_BUSINESS_ID,
        name: cat.name,
        description: cat.description,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
  }
  console.log(`  Categories: ${CATEGORIES.length} categorías`);

  // 4. Almacén principal de Luminia
  await prisma.warehouse.upsert({
    where: { id: '00000000-0000-0000-0007-000000000001' },
    update: {},
    create: {
      id: '00000000-0000-0000-0007-000000000001',
      businessId: LUMINIA_BUSINESS_ID,
      name: 'Almacén Principal',
      description: 'Almacén principal del negocio Luminia',
      isDefault: true,
      active: true,
      createdBy: LUMINIA_PERSON_ID,
    },
  });
  console.log('  Warehouse: Almacén Principal (default)');

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
