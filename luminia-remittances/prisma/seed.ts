import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// ─── Corredores de remesas habilitados ──────────────────────────────────────

const CORRIDORS = [
  {
    id: '00000000-0000-0000-0050-000000000001',
    originCountry: 'US',
    originCurrency: 'USD',
    destCountry: 'BO',
    destCurrency: 'BOB',
  },
  {
    id: '00000000-0000-0000-0050-000000000002',
    originCountry: 'ES',
    originCurrency: 'EUR',
    destCountry: 'BO',
    destCurrency: 'BOB',
  },
  {
    id: '00000000-0000-0000-0050-000000000003',
    originCountry: 'AR',
    originCurrency: 'ARS',
    destCountry: 'BO',
    destCurrency: 'BOB',
  },
  {
    id: '00000000-0000-0000-0050-000000000004',
    originCountry: 'BR',
    originCurrency: 'BRL',
    destCountry: 'BO',
    destCurrency: 'BOB',
  },
  {
    id: '00000000-0000-0000-0050-000000000005',
    originCountry: 'CL',
    originCurrency: 'CLP',
    destCountry: 'BO',
    destCurrency: 'BOB',
  },
];

// Tipos de cambio iniciales (aproximados)
const EXCHANGE_RATES = [
  { corridorIdx: 0, rate: 6.96, spreadPct: 0.015 },   // USD → BOB
  { corridorIdx: 1, rate: 7.55, spreadPct: 0.02 },    // EUR → BOB
  { corridorIdx: 2, rate: 0.0058, spreadPct: 0.03 },  // ARS → BOB
  { corridorIdx: 3, rate: 1.20, spreadPct: 0.025 },   // BRL → BOB
  { corridorIdx: 4, rate: 0.0072, spreadPct: 0.025 }, // CLP → BOB
];

// Canales de pago disponibles
const PAYMENT_CHANNELS = [
  {
    id: '00000000-0000-0000-0051-000000000001',
    country: 'BO',
    name: 'QR BNB',
    channelType: 'QR',
  },
  {
    id: '00000000-0000-0000-0051-000000000002',
    country: 'BO',
    name: 'Transferencia Bancaria',
    channelType: 'BANK_TRANSFER',
  },
  {
    id: '00000000-0000-0000-0051-000000000003',
    country: 'BO',
    name: 'Tigo Money',
    channelType: 'MOBILE_WALLET',
  },
  {
    id: '00000000-0000-0000-0051-000000000004',
    country: 'US',
    name: 'Zelle',
    channelType: 'BANK_TRANSFER',
  },
  {
    id: '00000000-0000-0000-0051-000000000005',
    country: 'PE',
    name: 'Yape',
    channelType: 'MOBILE_WALLET',
  },
];

async function main() {
  console.log('Seeding luminia-remittances...');

  // 1. Corredores
  for (const c of CORRIDORS) {
    await prisma.corridor.upsert({
      where: {
        originCountry_destCountry: {
          originCountry: c.originCountry,
          destCountry: c.destCountry,
        },
      },
      update: {},
      create: {
        id: c.id,
        originCountry: c.originCountry,
        originCurrency: c.originCurrency,
        destCountry: c.destCountry,
        destCurrency: c.destCurrency,
        active: true,
      },
    });
  }
  console.log(`  Corridors: ${CORRIDORS.length} corredores`);

  // 2. Tipos de cambio
  for (const er of EXCHANGE_RATES) {
    const corridor = CORRIDORS[er.corridorIdx];
    const rateId = `00000000-0000-0000-0052-${String(er.corridorIdx + 1).padStart(12, '0')}`;
    await prisma.exchangeRate.upsert({
      where: { id: rateId },
      update: { rate: er.rate, spreadPct: er.spreadPct },
      create: {
        id: rateId,
        corridorId: corridor.id,
        rate: er.rate,
        spreadPct: er.spreadPct,
        validFrom: new Date(),
        active: true,
      },
    });
  }
  console.log(`  ExchangeRates: ${EXCHANGE_RATES.length} tasas`);

  // 3. Canales de pago
  for (const ch of PAYMENT_CHANNELS) {
    await prisma.paymentChannel.upsert({
      where: { id: ch.id },
      update: {},
      create: {
        id: ch.id,
        country: ch.country,
        name: ch.name,
        channelType: ch.channelType as any,
        active: true,
      },
    });
  }
  console.log(`  PaymentChannels: ${PAYMENT_CHANNELS.length} canales`);

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
