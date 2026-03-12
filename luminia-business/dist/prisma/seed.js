"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LUMINIA_BUSINESS_ID = void 0;
const prisma_1 = require("@/lib/prisma");
require("dotenv/config");
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';
const LUMINIA_SYSTEM_ID = '00000000-0000-0000-0000-000000000002';
exports.LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';
async function main() {
    console.log('Seeding luminia-business...');
    const business = await prisma_1.prisma.business.upsert({
        where: { id: exports.LUMINIA_BUSINESS_ID },
        update: {},
        create: {
            id: exports.LUMINIA_BUSINESS_ID,
            ownerId: LUMINIA_PERSON_ID,
            name: 'Luminia',
            businessType: 'ADMINISTRACION',
            url: 'https://luminia.com',
            active: true,
            createdBy: LUMINIA_PERSON_ID,
        },
    });
    console.log(`  Business: ${business.name}`);
    await prisma_1.prisma.systemAssignment.upsert({
        where: {
            businessId_systemId: {
                businessId: exports.LUMINIA_BUSINESS_ID,
                systemId: LUMINIA_SYSTEM_ID,
            },
        },
        update: {},
        create: {
            businessId: exports.LUMINIA_BUSINESS_ID,
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
    .finally(() => prisma_1.prisma.$disconnect());
//# sourceMappingURL=seed.js.map