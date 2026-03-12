import 'dotenv/config';
import { PrismaClient } from '@/generated/prisma/client';
import { PrismaPg } from '@prisma/adapter-pg';
import { Pool } from 'pg';

// luminia-billers no requiere datos semilla estáticos.
//
// Las tablas de lookup de SIAT (Activity, BroadcastType, InvoiceType,
// CancellationReason, MeasurementUnit, etc.) se sincronizan dinámicamente
// desde la API del SIAT por negocio, usando el proceso de sincronización
// propio del servicio (SyncSetting + SyncLog).
//
// SiatConfig, BranchOffice, PointSale, Cuis, Cufd se crean al momento
// en que cada negocio se registra en el sistema de facturación boliviano.
//
// Si necesitas datos de prueba para desarrollo, ejecuta la sincronización
// SIAT con el entorno TEST:
//   POST /siat/sync → llama a los endpoints SIAT y pobla las tablas lookup.

const pool = new Pool({ connectionString: process.env.DATABASE_URL });
const prisma = new PrismaClient({ adapter: new PrismaPg(pool) });

async function main() {
  console.log('Seeding luminia-billers...');
  console.log('  (sin datos semilla estáticos — los catálogos SIAT se sincronizan por negocio)');
  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
