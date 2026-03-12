"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.PrismaClient = exports.adapter = exports.prisma = void 0;
require("dotenv/config");
const adapter_pg_1 = require("@prisma/adapter-pg");
const pg_1 = require("pg");
const client_1 = require("@/generated/prisma/client");
Object.defineProperty(exports, "PrismaClient", { enumerable: true, get: function () { return client_1.PrismaClient; } });
const pool = new pg_1.Pool({
    connectionString: process.env.DATABASE_URL,
});
const adapter = new adapter_pg_1.PrismaPg(pool);
exports.adapter = adapter;
const prisma = new client_1.PrismaClient({ adapter });
exports.prisma = prisma;
//# sourceMappingURL=prisma.js.map