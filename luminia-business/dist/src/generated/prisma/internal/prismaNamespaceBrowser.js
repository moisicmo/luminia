"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.NullsOrder = exports.QueryMode = exports.SortOrder = exports.PointOfSaleScalarFieldEnum = exports.BranchScalarFieldEnum = exports.BusinessMemberScalarFieldEnum = exports.BusinessScalarFieldEnum = exports.SystemAssignmentScalarFieldEnum = exports.TransactionIsolationLevel = exports.ModelName = exports.AnyNull = exports.JsonNull = exports.DbNull = exports.NullTypes = exports.Decimal = void 0;
const runtime = require("@prisma/client/runtime/index-browser");
exports.Decimal = runtime.Decimal;
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
//# sourceMappingURL=prismaNamespaceBrowser.js.map