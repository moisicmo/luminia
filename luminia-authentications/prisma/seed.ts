import 'dotenv/config';
import crypto from 'crypto';
import { prisma } from '@/lib/prisma';

// UUIDs fijos — deben coincidir con luminia-users/prisma/seed.ts
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';

const LUMINIA_ADMIN_EMAIL = 'admin@luminia.com';
const LUMINIA_ADMIN_PASSWORD = 'Luminia2025*';

function hashPassword(password: string): { hash: string; salt: string } {
  const salt = crypto.randomBytes(16).toString('hex');
  const hash = crypto
    .pbkdf2Sync(password, salt, 10000, 64, 'sha512')
    .toString('hex');
  return { hash, salt };
}

async function main() {
  console.log('Seeding luminia-authentications...');

  // 1. User: credenciales del admin de Luminia
  const { hash, salt } = hashPassword(LUMINIA_ADMIN_PASSWORD);

  const user = await prisma.user.upsert({
    where: { personId: LUMINIA_PERSON_ID },
    update: {},
    create: {
      personId: LUMINIA_PERSON_ID,
      username: 'luminia_admin',
      password: hash,
      passwordSalt: salt,
      state: 'VERIFICADO',
    },
  });
  console.log(`  User: ${user.username}`);

  // 2. Authentication: email verificado del admin
  await prisma.authentication.upsert({
    where: {
      userId_typeAuth_data: {
        userId: LUMINIA_PERSON_ID,
        typeAuth: 'EMAIL',
        data: LUMINIA_ADMIN_EMAIL,
      },
    },
    update: {},
    create: {
      userId: LUMINIA_PERSON_ID,
      typeAuth: 'EMAIL',
      data: LUMINIA_ADMIN_EMAIL,
      validated: true,
      active: true,
      createdBy: LUMINIA_PERSON_ID,
    },
  });
  console.log(`  Authentication: ${LUMINIA_ADMIN_EMAIL} (EMAIL verificado)`);

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
