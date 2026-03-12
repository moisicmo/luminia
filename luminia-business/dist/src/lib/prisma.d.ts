import 'dotenv/config';
import { PrismaClient } from '@/generated/prisma/client';
declare const adapter: any;
declare const prisma: import("../generated/prisma/internal/class").PrismaClient<never, import("../generated/prisma/internal/prismaNamespace").GlobalOmitConfig | undefined, runtime.Types.Extensions.DefaultArgs>;
export { prisma, adapter, PrismaClient };
