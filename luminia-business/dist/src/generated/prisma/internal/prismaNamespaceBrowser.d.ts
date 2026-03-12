import * as runtime from "@prisma/client/runtime/index-browser";
export type * from '../models';
export type * from './prismaNamespace';
export declare const Decimal: any;
export declare const NullTypes: {
    DbNull: (new (secret: never) => typeof runtime.DbNull);
    JsonNull: (new (secret: never) => typeof runtime.JsonNull);
    AnyNull: (new (secret: never) => typeof runtime.AnyNull);
};
export declare const DbNull: any;
export declare const JsonNull: any;
export declare const AnyNull: any;
export declare const ModelName: {
    readonly SystemAssignment: "SystemAssignment";
    readonly Business: "Business";
    readonly BusinessMember: "BusinessMember";
    readonly Branch: "Branch";
    readonly PointOfSale: "PointOfSale";
};
export type ModelName = (typeof ModelName)[keyof typeof ModelName];
export declare const TransactionIsolationLevel: any;
export type TransactionIsolationLevel = (typeof TransactionIsolationLevel)[keyof typeof TransactionIsolationLevel];
export declare const SystemAssignmentScalarFieldEnum: {
    readonly id: "id";
    readonly businessId: "businessId";
    readonly systemId: "systemId";
    readonly createdAt: "createdAt";
    readonly updatedAt: "updatedAt";
    readonly createdBy: "createdBy";
    readonly updatedBy: "updatedBy";
};
export type SystemAssignmentScalarFieldEnum = (typeof SystemAssignmentScalarFieldEnum)[keyof typeof SystemAssignmentScalarFieldEnum];
export declare const BusinessScalarFieldEnum: {
    readonly id: "id";
    readonly ownerId: "ownerId";
    readonly name: "name";
    readonly businessType: "businessType";
    readonly url: "url";
    readonly taxId: "taxId";
    readonly logo: "logo";
    readonly active: "active";
    readonly createdAt: "createdAt";
    readonly updatedAt: "updatedAt";
    readonly createdBy: "createdBy";
    readonly updatedBy: "updatedBy";
};
export type BusinessScalarFieldEnum = (typeof BusinessScalarFieldEnum)[keyof typeof BusinessScalarFieldEnum];
export declare const BusinessMemberScalarFieldEnum: {
    readonly id: "id";
    readonly businessId: "businessId";
    readonly userId: "userId";
    readonly role: "role";
    readonly active: "active";
    readonly createdAt: "createdAt";
    readonly updatedAt: "updatedAt";
    readonly createdBy: "createdBy";
    readonly updatedBy: "updatedBy";
};
export type BusinessMemberScalarFieldEnum = (typeof BusinessMemberScalarFieldEnum)[keyof typeof BusinessMemberScalarFieldEnum];
export declare const BranchScalarFieldEnum: {
    readonly id: "id";
    readonly businessId: "businessId";
    readonly code: "code";
    readonly name: "name";
    readonly region: "region";
    readonly address: "address";
    readonly municipality: "municipality";
    readonly phone: "phone";
    readonly latitude: "latitude";
    readonly longitude: "longitude";
    readonly openingHours: "openingHours";
    readonly active: "active";
    readonly createdAt: "createdAt";
    readonly updatedAt: "updatedAt";
    readonly createdBy: "createdBy";
    readonly updatedBy: "updatedBy";
};
export type BranchScalarFieldEnum = (typeof BranchScalarFieldEnum)[keyof typeof BranchScalarFieldEnum];
export declare const PointOfSaleScalarFieldEnum: {
    readonly id: "id";
    readonly branchId: "branchId";
    readonly code: "code";
    readonly name: "name";
    readonly active: "active";
    readonly createdAt: "createdAt";
    readonly updatedAt: "updatedAt";
    readonly createdBy: "createdBy";
    readonly updatedBy: "updatedBy";
};
export type PointOfSaleScalarFieldEnum = (typeof PointOfSaleScalarFieldEnum)[keyof typeof PointOfSaleScalarFieldEnum];
export declare const SortOrder: {
    readonly asc: "asc";
    readonly desc: "desc";
};
export type SortOrder = (typeof SortOrder)[keyof typeof SortOrder];
export declare const QueryMode: {
    readonly default: "default";
    readonly insensitive: "insensitive";
};
export type QueryMode = (typeof QueryMode)[keyof typeof QueryMode];
export declare const NullsOrder: {
    readonly first: "first";
    readonly last: "last";
};
export type NullsOrder = (typeof NullsOrder)[keyof typeof NullsOrder];
