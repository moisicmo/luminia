import 'dotenv/config';
import { prisma } from '@/lib/prisma';


// UUIDs fijos compartidos entre microservicios
export const SEED_IDS = {
  LUMINIA_PERSON_ID: '00000000-0000-0000-0000-000000000001',
  LUMINIA_SYSTEM_ID: '00000000-0000-0000-0000-000000000002',
  LUMINIA_SUPER_ADMIN_ROLE_ID: '00000000-0000-0000-0000-000000000010',
  LUMINIA_BUSINESS_ADMIN_ROLE_ID: '00000000-0000-0000-0000-000000000011',
};

const SUBJECTS = [
  { name: 'Negocio', description: 'Gestión de negocios registrados en la plataforma' },
  { name: 'Sucursal', description: 'Gestión de sucursales de un negocio' },
  { name: 'PuntoDeVenta', description: 'Gestión de puntos de venta' },
  { name: 'Plan', description: 'Gestión de planes de suscripción' },
  { name: 'Suscripcion', description: 'Gestión de suscripciones activas' },
  { name: 'Promocion', description: 'Gestión de promociones y descuentos' },
  { name: 'Usuario', description: 'Gestión de personas y usuarios del sistema' },
  { name: 'Rol', description: 'Gestión de roles y asignaciones' },
  { name: 'Permiso', description: 'Gestión de permisos por rol' },
  { name: 'Sistema', description: 'Gestión de sistemas registrados en la plataforma' },
];

async function main() {
  console.log('Seeding luminia-users...');

  // 1. Person: administrador raíz de Luminia
  const person = await prisma.person.upsert({
    where: { id: SEED_IDS.LUMINIA_PERSON_ID },
    update: {},
    create: {
      id: SEED_IDS.LUMINIA_PERSON_ID,
      numberDocument: '00000000',
      typeDocument: 'nit',
      name: 'Luminia',
      lastName: 'Platform',
      createdBy: SEED_IDS.LUMINIA_PERSON_ID,
    },
  });
  console.log(`  Person: ${person.name} ${person.lastName}`);

  // 2. System: sistema principal de Luminia
  const system = await prisma.system.upsert({
    where: { id: SEED_IDS.LUMINIA_SYSTEM_ID },
    update: {},
    create: {
      id: SEED_IDS.LUMINIA_SYSTEM_ID,
      name: 'Luminia Platform',
      description: 'Sistema central de administración de la plataforma Luminia',
      createdBy: SEED_IDS.LUMINIA_PERSON_ID,
    },
  });
  console.log(`  System: ${system.name}`);

  // 3. Subjects: recursos sobre los que se definen permisos
  const subjects = await Promise.all(
    SUBJECTS.map((s: { name: string; description: string }) =>
      prisma.subject.upsert({
        where: { systemId_name: { systemId: SEED_IDS.LUMINIA_SYSTEM_ID, name: s.name } },
        update: {},
        create: {
          systemId: SEED_IDS.LUMINIA_SYSTEM_ID,
          name: s.name,
          description: s.description,
          createdBy: SEED_IDS.LUMINIA_PERSON_ID,
        },
      }),
    ),
  );
  console.log(`  Subjects: ${subjects.map((s) => s.name).join(', ')}`);

  // 4. Roles
  const superAdminRole = await prisma.role.upsert({
    where: { id: SEED_IDS.LUMINIA_SUPER_ADMIN_ROLE_ID },
    update: {},
    create: {
      id: SEED_IDS.LUMINIA_SUPER_ADMIN_ROLE_ID,
      systemId: SEED_IDS.LUMINIA_SYSTEM_ID,
      name: 'Super Admin',
      createdBy: SEED_IDS.LUMINIA_PERSON_ID,
    },
  });

  // Rol plantilla que se asigna cuando un cliente crea su negocio
  const businessAdminRole = await prisma.role.upsert({
    where: { id: SEED_IDS.LUMINIA_BUSINESS_ADMIN_ROLE_ID },
    update: {},
    create: {
      id: SEED_IDS.LUMINIA_BUSINESS_ADMIN_ROLE_ID,
      systemId: SEED_IDS.LUMINIA_SYSTEM_ID,
      name: 'Business Admin',
      createdBy: SEED_IDS.LUMINIA_PERSON_ID,
    },
  });
  console.log(`  Roles: ${superAdminRole.name}, ${businessAdminRole.name}`);

  // 5. Permissions: Super Admin puede hacer todo sobre todos los subjects
  const actions = ['CREAR', 'LEER', 'EDITAR', 'ELIMINAR'] as const;
  for (const subject of subjects) {
    for (const action of actions) {
      await prisma.permission.upsert({
        where: {
          roleId_action_subjectId: {
            roleId: SEED_IDS.LUMINIA_SUPER_ADMIN_ROLE_ID,
            action,
            subjectId: subject.id,
          },
        },
        update: {},
        create: {
          roleId: SEED_IDS.LUMINIA_SUPER_ADMIN_ROLE_ID,
          action,
          subjectId: subject.id,
          createdBy: SEED_IDS.LUMINIA_PERSON_ID,
        },
      });
    }
  }
  console.log(`  Permissions: ${subjects.length * actions.length} permisos para Super Admin`);

  // 6. Assignment: Luminia admin → Super Admin
  await prisma.assignment.upsert({
    where: { personId_roleId: { personId: SEED_IDS.LUMINIA_PERSON_ID, roleId: SEED_IDS.LUMINIA_SUPER_ADMIN_ROLE_ID } },
    update: {},
    create: {
      personId: SEED_IDS.LUMINIA_PERSON_ID,
      roleId: SEED_IDS.LUMINIA_SUPER_ADMIN_ROLE_ID,
      description: 'Administrador raíz de la plataforma Luminia',
      createdBy: SEED_IDS.LUMINIA_PERSON_ID,
    },
  });
  console.log('  Assignment: Luminia admin → Super Admin');

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
