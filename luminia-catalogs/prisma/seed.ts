import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUIDs fijos — deben coincidir con los otros seeds
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

// ─── Perfiles públicos de ejemplo para cada vertical ────────────────────────

const PROFILES = [
  {
    id: '00000000-0000-0000-0030-000000000001',
    vertical: 'FITNESS',
    name: 'Iron Gym',
    slug: 'iron-gym',
    description: 'Gimnasio de musculación y crossfit en el centro de La Paz.',
    shortDesc: 'Gym & Crossfit',
    city: 'La Paz',
    country: 'BO',
    phone: '+59172000001',
  },
  {
    id: '00000000-0000-0000-0030-000000000002',
    vertical: 'HEALTH',
    name: 'Clínica Dental Sonrisa',
    slug: 'clinica-dental-sonrisa',
    description: 'Clínica dental especializada en ortodoncia y estética dental.',
    shortDesc: 'Odontología integral',
    city: 'Cochabamba',
    country: 'BO',
    phone: '+59144000002',
  },
  {
    id: '00000000-0000-0000-0030-000000000003',
    vertical: 'BEAUTY',
    name: 'Barbería El Patrón',
    slug: 'barberia-el-patron',
    description: 'Barbería moderna con cortes clásicos y contemporáneos.',
    shortDesc: 'Barbería & Grooming',
    city: 'Santa Cruz',
    country: 'BO',
    phone: '+59133000003',
  },
  {
    id: '00000000-0000-0000-0030-000000000004',
    vertical: 'TRAVEL',
    name: 'Bolivia Aventura Tours',
    slug: 'bolivia-aventura-tours',
    description: 'Agencia de viajes especializada en tours al Salar de Uyuni y Amazonia.',
    shortDesc: 'Tours & Aventura',
    city: 'La Paz',
    country: 'BO',
    phone: '+59172000004',
  },
  {
    id: '00000000-0000-0000-0030-000000000005',
    vertical: 'FOOD',
    name: 'La Casona Restaurant',
    slug: 'la-casona-restaurant',
    description: 'Restaurante de comida boliviana tradicional con ingredientes orgánicos.',
    shortDesc: 'Cocina boliviana',
    city: 'Sucre',
    country: 'BO',
    phone: '+59164000005',
  },
] as const;

const TAGS = [
  { profileIdx: 0, tags: ['wifi', 'estacionamiento', 'duchas', 'crossfit'] },
  { profileIdx: 1, tags: ['ortodoncia', 'blanqueamiento', 'emergencias'] },
  { profileIdx: 2, tags: ['corte-clasico', 'barba', 'afeitado'] },
  { profileIdx: 3, tags: ['salar-uyuni', 'amazonia', 'trekking', 'mountain-bike'] },
  { profileIdx: 4, tags: ['comida-tipica', 'organico', 'delivery'] },
];

async function main() {
  console.log('Seeding luminia-catalogs...');

  // 1. Perfiles públicos
  for (const p of PROFILES) {
    await prisma.publicProfile.upsert({
      where: { vertical_slug: { vertical: p.vertical, slug: p.slug } },
      update: {},
      create: {
        id: p.id,
        businessId: LUMINIA_BUSINESS_ID,
        vertical: p.vertical,
        name: p.name,
        slug: p.slug,
        description: p.description,
        shortDesc: p.shortDesc,
        city: p.city,
        country: p.country,
        phone: p.phone,
        active: true,
      },
    });
  }
  console.log(`  PublicProfiles: ${PROFILES.length} perfiles`);

  // 2. Tags por perfil
  let tagCount = 0;
  for (const entry of TAGS) {
    const profileId = PROFILES[entry.profileIdx].id;
    for (const tag of entry.tags) {
      await prisma.profileTag.upsert({
        where: { profileId_tag: { profileId, tag } },
        update: {},
        create: { profileId, tag },
      });
      tagCount++;
    }
  }
  console.log(`  ProfileTags: ${tagCount} tags`);

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
