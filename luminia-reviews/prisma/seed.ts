import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUIDs fijos — deben coincidir con los otros seeds
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

// ─── Reseñas de ejemplo ─────────────────────────────────────────────────────

const REVIEWS = [
  {
    id: '00000000-0000-0000-0040-000000000001',
    targetType: 'BUSINESS',
    targetId: LUMINIA_BUSINESS_ID,
    authorId: LUMINIA_PERSON_ID,
    authorName: 'Administrador Luminia',
    rating: 5,
    title: 'Excelente plataforma',
    comment: 'Luminia ha simplificado la gestión de nuestro negocio enormemente.',
    verified: true,
  },
];

async function main() {
  console.log('Seeding luminia-reviews...');

  // 1. Reseñas
  for (const r of REVIEWS) {
    await prisma.review.upsert({
      where: {
        targetType_targetId_authorId: {
          targetType: r.targetType as any,
          targetId: r.targetId,
          authorId: r.authorId,
        },
      },
      update: {},
      create: {
        id: r.id,
        targetType: r.targetType as any,
        targetId: r.targetId,
        authorId: r.authorId,
        authorName: r.authorName,
        rating: r.rating,
        title: r.title,
        comment: r.comment,
        verified: r.verified,
        visible: true,
      },
    });
  }
  console.log(`  Reviews: ${REVIEWS.length} reseñas`);

  // 2. Rating summaries
  await prisma.ratingSummary.upsert({
    where: {
      targetType_targetId: {
        targetType: 'BUSINESS',
        targetId: LUMINIA_BUSINESS_ID,
      },
    },
    update: {},
    create: {
      targetType: 'BUSINESS',
      targetId: LUMINIA_BUSINESS_ID,
      totalCount: 1,
      averageRating: 5.0,
      ratingSum: 5,
    },
  });
  console.log('  RatingSummaries: 1 resumen');

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
