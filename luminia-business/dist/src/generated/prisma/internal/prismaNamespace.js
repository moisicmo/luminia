"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.defineExtension = exports.NullsOrder = exports.QueryMode = exports.SortOrder = exports.PointOfSaleScalarFieldEnum = exports.BranchScalarFieldEnum = exports.BusinessMemberScalarFieldEnum = exports.BusinessScalarFieldEnum = exports.SystemAssignmentScalarFieldEnum = exports.TransactionIsolationLevel = exports.ModelName = exports.AnyNull = exports.JsonNull = exports.DbNull = exports.NullTypes = exports.prismaVersion = exports.getExtensionContext = exports.Decimal = exports.Sql = exports.raw = exports.join = exports.empty = exports.sql = exports.PrismaClientValidationError = exports.PrismaClientInitializationError = exports.PrismaClientRustPanicError = exports.PrismaClientUnknownRequestError = exports.PrismaClientKnownRequestError = void 0;
const runtime = require("@prisma/client/runtime/client");
exports.PrismaClientKnownRequestError = runtime.PrismaClientKnownRequestError;
exports.PrismaClientUnknownRequestError = runtime.PrismaClientUnknownRequestError;
exports.PrismaClientRustPanicError = runtime.PrismaClientRustPanicError;
exports.PrismaClientInitializationError = runtime.PrismaClientInitializationError;
exports.PrismaClientValidationError = runtime.PrismaClientValidationError;
exports.sql = runtime.sqltag;
exports.empty = runtime.empty;
exports.join = runtime.join;
exports.raw = runtime.raw;
exports.Sql = runtime.Sql;
exports.Decimal = runtime.Decimal;
exports.getExtensionContext = runtime.Extensions.getExtensionContext;
exports.prismaVersion = {
    client: "7.4.2",
    engine: "94a226be1cf2967af2541cca5529f0f7ba866919"
};
exports.NullTypes = {
    DbNull: runtime.NullTypes.DbNull,
    JsonNull: runtime.NullTypes.JsonNull,
    AnyNull: runtime.NullTypes.AnyNull,
};
exports.DbNull = runtime.DbNull;
exports.JsonNull = runtime.JsonNull;
exports.AnyNull = runtime.AnyNull;
exports.ModelName = {
    SystemAssignment: 'SystemAssignment',
    Business: 'Business',
    BusinessMember: 'BusinessMember',
    Branch: 'Branch',
    PointOfSale: 'PointOfSale'
};
exports.TransactionIsolationLevel = runtime.makeStrictEnum({
    ReadUncommitted: 'ReadUncommitted',
    ReadCommitted: 'ReadCommitted',
    RepeatableRead: 'RepeatableRead',
    Serializable: 'Serializable'
});
exports.SystemAssignmentScalarFieldEnum = {
    id: 'id',
    businessId: 'businessId',
    systemId: 'systemId',
    createdAt: 'createdAt',
    updatedAt: 'updatedAt',
    createdBy: 'createdBy',
    updatedBy: 'updatedBy'
};
exports.BusinessScalarFieldEnum = {
    id: 'id',
    ownerId: 'ownerId',
    name: 'name',
    businessType: 'businessType',
    url: 'url',
    taxId: 'taxId',
    logo: 'logo',
    active: 'active',
    createdAt: 'createdAt',
    updatedAt: 'updatedAt',
    createdBy: 'createdBy',
    updatedBy: 'updatedBy'
};
exports.BusinessMemberScalarFieldEnum = {
    id: 'id',
    businessId: 'businessId',
    userId: 'userId',
    role: 'role',
    active: 'active',
    createdAt: 'createdAt',
    updatedAt: 'updatedAt',
    createdBy: 'createdBy',
    updatedBy: 'updatedBy'
};
exports.BranchScalarFieldEnum = {
    id: 'id',
    businessId: 'businessId',
    code: 'code',
    name: 'name',
    region: 'region',
    address: 'address',
    municipality: 'municipality',
    phone: 'phone',
    latitude: 'latitude',
    longitude: 'longitude',
    openingHours: 'openingHours',
    active: 'active',
    createdAt: 'createdAt',
    updatedAt: 'updatedAt',
    createdBy: 'createdBy',
    updatedBy: 'updatedBy'
};
exports.PointOfSaleScalarFieldEnum = {
    id: 'id',
    branchId: 'branchId',
    code: 'code',
    name: 'name',
    active: 'active',
    createdAt: 'createdAt',
    updatedAt: 'updatedAt',
    createdBy: 'createdBy',
    updatedBy: 'updatedBy'
};
exports.SortOrder = {
    asc: 'asc',
    desc: 'desc'
};
exports.QueryMode = {
    default: 'default',
    insensitive: 'insensitive'
};
exports.NullsOrder = {
    first: 'first',
    last: 'last'
};
exports.defineExtension = runtime.Extensions.defineExtension;
//# sourceMappingURL=prismaNamespace.js.map