import * as runtime from "@prisma/client/runtime/client";
import type * as Prisma from "../models";
import { type PrismaClient } from "./class";
export type * from '../models';
export type DMMF = typeof runtime.DMMF;
export type PrismaPromise<T> = runtime.Types.Public.PrismaPromise<T>;
export declare const PrismaClientKnownRequestError: any;
export type PrismaClientKnownRequestError = runtime.PrismaClientKnownRequestError;
export declare const PrismaClientUnknownRequestError: any;
export type PrismaClientUnknownRequestError = runtime.PrismaClientUnknownRequestError;
export declare const PrismaClientRustPanicError: any;
export type PrismaClientRustPanicError = runtime.PrismaClientRustPanicError;
export declare const PrismaClientInitializationError: any;
export type PrismaClientInitializationError = runtime.PrismaClientInitializationError;
export declare const PrismaClientValidationError: any;
export type PrismaClientValidationError = runtime.PrismaClientValidationError;
export declare const sql: any;
export declare const empty: any;
export declare const join: any;
export declare const raw: any;
export declare const Sql: any;
export type Sql = runtime.Sql;
export declare const Decimal: any;
export type Decimal = runtime.Decimal;
export type DecimalJsLike = runtime.DecimalJsLike;
export type Extension = runtime.Types.Extensions.UserArgs;
export declare const getExtensionContext: any;
export type Args<T, F extends runtime.Operation> = runtime.Types.Public.Args<T, F>;
export type Payload<T, F extends runtime.Operation = never> = runtime.Types.Public.Payload<T, F>;
export type Result<T, A, F extends runtime.Operation> = runtime.Types.Public.Result<T, A, F>;
export type Exact<A, W> = runtime.Types.Public.Exact<A, W>;
export type PrismaVersion = {
    client: string;
    engine: string;
};
export declare const prismaVersion: PrismaVersion;
export type Bytes = runtime.Bytes;
export type JsonObject = runtime.JsonObject;
export type JsonArray = runtime.JsonArray;
export type JsonValue = runtime.JsonValue;
export type InputJsonObject = runtime.InputJsonObject;
export type InputJsonArray = runtime.InputJsonArray;
export type InputJsonValue = runtime.InputJsonValue;
export declare const NullTypes: {
    DbNull: (new (secret: never) => typeof runtime.DbNull);
    JsonNull: (new (secret: never) => typeof runtime.JsonNull);
    AnyNull: (new (secret: never) => typeof runtime.AnyNull);
};
export declare const DbNull: any;
export declare const JsonNull: any;
export declare const AnyNull: any;
type SelectAndInclude = {
    select: any;
    include: any;
};
type SelectAndOmit = {
    select: any;
    omit: any;
};
type Prisma__Pick<T, K extends keyof T> = {
    [P in K]: T[P];
};
export type Enumerable<T> = T | Array<T>;
export type Subset<T, U> = {
    [key in keyof T]: key extends keyof U ? T[key] : never;
};
export type SelectSubset<T, U> = {
    [key in keyof T]: key extends keyof U ? T[key] : never;
} & (T extends SelectAndInclude ? 'Please either choose `select` or `include`.' : T extends SelectAndOmit ? 'Please either choose `select` or `omit`.' : {});
export type SubsetIntersection<T, U, K> = {
    [key in keyof T]: key extends keyof U ? T[key] : never;
} & K;
type Without<T, U> = {
    [P in Exclude<keyof T, keyof U>]?: never;
};
export type XOR<T, U> = T extends object ? U extends object ? (Without<T, U> & U) | (Without<U, T> & T) : U : T;
type IsObject<T extends any> = T extends Array<any> ? False : T extends Date ? False : T extends Uint8Array ? False : T extends BigInt ? False : T extends object ? True : False;
export type UnEnumerate<T extends unknown> = T extends Array<infer U> ? U : T;
type __Either<O extends object, K extends Key> = Omit<O, K> & {
    [P in K]: Prisma__Pick<O, P & keyof O>;
}[K];
type EitherStrict<O extends object, K extends Key> = Strict<__Either<O, K>>;
type EitherLoose<O extends object, K extends Key> = ComputeRaw<__Either<O, K>>;
type _Either<O extends object, K extends Key, strict extends Boolean> = {
    1: EitherStrict<O, K>;
    0: EitherLoose<O, K>;
}[strict];
export type Either<O extends object, K extends Key, strict extends Boolean = 1> = O extends unknown ? _Either<O, K, strict> : never;
export type Union = any;
export type PatchUndefined<O extends object, O1 extends object> = {
    [K in keyof O]: O[K] extends undefined ? At<O1, K> : O[K];
} & {};
export type IntersectOf<U extends Union> = (U extends unknown ? (k: U) => void : never) extends (k: infer I) => void ? I : never;
export type Overwrite<O extends object, O1 extends object> = {
    [K in keyof O]: K extends keyof O1 ? O1[K] : O[K];
} & {};
type _Merge<U extends object> = IntersectOf<Overwrite<U, {
    [K in keyof U]-?: At<U, K>;
}>>;
type Key = string | number | symbol;
type AtStrict<O extends object, K extends Key> = O[K & keyof O];
type AtLoose<O extends object, K extends Key> = O extends unknown ? AtStrict<O, K> : never;
export type At<O extends object, K extends Key, strict extends Boolean = 1> = {
    1: AtStrict<O, K>;
    0: AtLoose<O, K>;
}[strict];
export type ComputeRaw<A extends any> = A extends Function ? A : {
    [K in keyof A]: A[K];
} & {};
export type OptionalFlat<O> = {
    [K in keyof O]?: O[K];
} & {};
type _Record<K extends keyof any, T> = {
    [P in K]: T;
};
type NoExpand<T> = T extends unknown ? T : never;
export type AtLeast<O extends object, K extends string> = NoExpand<O extends unknown ? (K extends keyof O ? {
    [P in K]: O[P];
} & O : O) | {
    [P in keyof O as P extends K ? P : never]-?: O[P];
} & O : never>;
type _Strict<U, _U = U> = U extends unknown ? U & OptionalFlat<_Record<Exclude<Keys<_U>, keyof U>, never>> : never;
export type Strict<U extends object> = ComputeRaw<_Strict<U>>;
export type Merge<U extends object> = ComputeRaw<_Merge<Strict<U>>>;
export type Boolean = True | False;
export type True = 1;
export type False = 0;
export type Not<B extends Boolean> = {
    0: 1;
    1: 0;
}[B];
export type Extends<A1 extends any, A2 extends any> = [A1] extends [never] ? 0 : A1 extends A2 ? 1 : 0;
export type Has<U extends Union, U1 extends Union> = Not<Extends<Exclude<U1, U>, U1>>;
export type Or<B1 extends Boolean, B2 extends Boolean> = {
    0: {
        0: 0;
        1: 1;
    };
    1: {
        0: 1;
        1: 1;
    };
}[B1][B2];
export type Keys<U extends Union> = U extends unknown ? keyof U : never;
export type GetScalarType<T, O> = O extends object ? {
    [P in keyof T]: P extends keyof O ? O[P] : never;
} : never;
type FieldPaths<T, U = Omit<T, '_avg' | '_sum' | '_count' | '_min' | '_max'>> = IsObject<T> extends True ? U : T;
export type GetHavingFields<T> = {
    [K in keyof T]: Or<Or<Extends<'OR', K>, Extends<'AND', K>>, Extends<'NOT', K>> extends True ? T[K] extends infer TK ? GetHavingFields<UnEnumerate<TK> extends object ? Merge<UnEnumerate<TK>> : never> : never : {} extends FieldPaths<T[K]> ? never : K;
}[keyof T];
type _TupleToUnion<T> = T extends (infer E)[] ? E : never;
type TupleToUnion<K extends readonly any[]> = _TupleToUnion<K>;
export type MaybeTupleToUnion<T> = T extends any[] ? TupleToUnion<T> : T;
export type PickEnumerable<T, K extends Enumerable<keyof T> | keyof T> = Prisma__Pick<T, MaybeTupleToUnion<K>>;
export type ExcludeUnderscoreKeys<T extends string> = T extends `_${string}` ? never : T;
export type FieldRef<Model, FieldType> = runtime.FieldRef<Model, FieldType>;
type FieldRefInputType<Model, FieldType> = Model extends never ? never : FieldRef<Model, FieldType>;
export declare const ModelName: {
    readonly SystemAssignment: "SystemAssignment";
    readonly Business: "Business";
    readonly BusinessMember: "BusinessMember";
    readonly Branch: "Branch";
    readonly PointOfSale: "PointOfSale";
};
export type ModelName = (typeof ModelName)[keyof typeof ModelName];
export interface TypeMapCb<GlobalOmitOptions = {}> extends runtime.Types.Utils.Fn<{
    extArgs: runtime.Types.Extensions.InternalArgs;
}, runtime.Types.Utils.Record<string, any>> {
    returns: TypeMap<this['params']['extArgs'], GlobalOmitOptions>;
}
export type TypeMap<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> = {
    globalOmitOptions: {
        omit: GlobalOmitOptions;
    };
    meta: {
        modelProps: "systemAssignment" | "business" | "businessMember" | "branch" | "pointOfSale";
        txIsolationLevel: TransactionIsolationLevel;
    };
    model: {
        SystemAssignment: {
            payload: Prisma.$SystemAssignmentPayload<ExtArgs>;
            fields: Prisma.SystemAssignmentFieldRefs;
            operations: {
                findUnique: {
                    args: Prisma.SystemAssignmentFindUniqueArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload> | null;
                };
                findUniqueOrThrow: {
                    args: Prisma.SystemAssignmentFindUniqueOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>;
                };
                findFirst: {
                    args: Prisma.SystemAssignmentFindFirstArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload> | null;
                };
                findFirstOrThrow: {
                    args: Prisma.SystemAssignmentFindFirstOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>;
                };
                findMany: {
                    args: Prisma.SystemAssignmentFindManyArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>[];
                };
                create: {
                    args: Prisma.SystemAssignmentCreateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>;
                };
                createMany: {
                    args: Prisma.SystemAssignmentCreateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                createManyAndReturn: {
                    args: Prisma.SystemAssignmentCreateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>[];
                };
                delete: {
                    args: Prisma.SystemAssignmentDeleteArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>;
                };
                update: {
                    args: Prisma.SystemAssignmentUpdateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>;
                };
                deleteMany: {
                    args: Prisma.SystemAssignmentDeleteManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateMany: {
                    args: Prisma.SystemAssignmentUpdateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateManyAndReturn: {
                    args: Prisma.SystemAssignmentUpdateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>[];
                };
                upsert: {
                    args: Prisma.SystemAssignmentUpsertArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$SystemAssignmentPayload>;
                };
                aggregate: {
                    args: Prisma.SystemAssignmentAggregateArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.AggregateSystemAssignment>;
                };
                groupBy: {
                    args: Prisma.SystemAssignmentGroupByArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.SystemAssignmentGroupByOutputType>[];
                };
                count: {
                    args: Prisma.SystemAssignmentCountArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.SystemAssignmentCountAggregateOutputType> | number;
                };
            };
        };
        Business: {
            payload: Prisma.$BusinessPayload<ExtArgs>;
            fields: Prisma.BusinessFieldRefs;
            operations: {
                findUnique: {
                    args: Prisma.BusinessFindUniqueArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload> | null;
                };
                findUniqueOrThrow: {
                    args: Prisma.BusinessFindUniqueOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>;
                };
                findFirst: {
                    args: Prisma.BusinessFindFirstArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload> | null;
                };
                findFirstOrThrow: {
                    args: Prisma.BusinessFindFirstOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>;
                };
                findMany: {
                    args: Prisma.BusinessFindManyArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>[];
                };
                create: {
                    args: Prisma.BusinessCreateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>;
                };
                createMany: {
                    args: Prisma.BusinessCreateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                createManyAndReturn: {
                    args: Prisma.BusinessCreateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>[];
                };
                delete: {
                    args: Prisma.BusinessDeleteArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>;
                };
                update: {
                    args: Prisma.BusinessUpdateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>;
                };
                deleteMany: {
                    args: Prisma.BusinessDeleteManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateMany: {
                    args: Prisma.BusinessUpdateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateManyAndReturn: {
                    args: Prisma.BusinessUpdateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>[];
                };
                upsert: {
                    args: Prisma.BusinessUpsertArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessPayload>;
                };
                aggregate: {
                    args: Prisma.BusinessAggregateArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.AggregateBusiness>;
                };
                groupBy: {
                    args: Prisma.BusinessGroupByArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.BusinessGroupByOutputType>[];
                };
                count: {
                    args: Prisma.BusinessCountArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.BusinessCountAggregateOutputType> | number;
                };
            };
        };
        BusinessMember: {
            payload: Prisma.$BusinessMemberPayload<ExtArgs>;
            fields: Prisma.BusinessMemberFieldRefs;
            operations: {
                findUnique: {
                    args: Prisma.BusinessMemberFindUniqueArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload> | null;
                };
                findUniqueOrThrow: {
                    args: Prisma.BusinessMemberFindUniqueOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>;
                };
                findFirst: {
                    args: Prisma.BusinessMemberFindFirstArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload> | null;
                };
                findFirstOrThrow: {
                    args: Prisma.BusinessMemberFindFirstOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>;
                };
                findMany: {
                    args: Prisma.BusinessMemberFindManyArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>[];
                };
                create: {
                    args: Prisma.BusinessMemberCreateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>;
                };
                createMany: {
                    args: Prisma.BusinessMemberCreateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                createManyAndReturn: {
                    args: Prisma.BusinessMemberCreateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>[];
                };
                delete: {
                    args: Prisma.BusinessMemberDeleteArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>;
                };
                update: {
                    args: Prisma.BusinessMemberUpdateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>;
                };
                deleteMany: {
                    args: Prisma.BusinessMemberDeleteManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateMany: {
                    args: Prisma.BusinessMemberUpdateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateManyAndReturn: {
                    args: Prisma.BusinessMemberUpdateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>[];
                };
                upsert: {
                    args: Prisma.BusinessMemberUpsertArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BusinessMemberPayload>;
                };
                aggregate: {
                    args: Prisma.BusinessMemberAggregateArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.AggregateBusinessMember>;
                };
                groupBy: {
                    args: Prisma.BusinessMemberGroupByArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.BusinessMemberGroupByOutputType>[];
                };
                count: {
                    args: Prisma.BusinessMemberCountArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.BusinessMemberCountAggregateOutputType> | number;
                };
            };
        };
        Branch: {
            payload: Prisma.$BranchPayload<ExtArgs>;
            fields: Prisma.BranchFieldRefs;
            operations: {
                findUnique: {
                    args: Prisma.BranchFindUniqueArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload> | null;
                };
                findUniqueOrThrow: {
                    args: Prisma.BranchFindUniqueOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>;
                };
                findFirst: {
                    args: Prisma.BranchFindFirstArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload> | null;
                };
                findFirstOrThrow: {
                    args: Prisma.BranchFindFirstOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>;
                };
                findMany: {
                    args: Prisma.BranchFindManyArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>[];
                };
                create: {
                    args: Prisma.BranchCreateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>;
                };
                createMany: {
                    args: Prisma.BranchCreateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                createManyAndReturn: {
                    args: Prisma.BranchCreateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>[];
                };
                delete: {
                    args: Prisma.BranchDeleteArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>;
                };
                update: {
                    args: Prisma.BranchUpdateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>;
                };
                deleteMany: {
                    args: Prisma.BranchDeleteManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateMany: {
                    args: Prisma.BranchUpdateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateManyAndReturn: {
                    args: Prisma.BranchUpdateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>[];
                };
                upsert: {
                    args: Prisma.BranchUpsertArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$BranchPayload>;
                };
                aggregate: {
                    args: Prisma.BranchAggregateArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.AggregateBranch>;
                };
                groupBy: {
                    args: Prisma.BranchGroupByArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.BranchGroupByOutputType>[];
                };
                count: {
                    args: Prisma.BranchCountArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.BranchCountAggregateOutputType> | number;
                };
            };
        };
        PointOfSale: {
            payload: Prisma.$PointOfSalePayload<ExtArgs>;
            fields: Prisma.PointOfSaleFieldRefs;
            operations: {
                findUnique: {
                    args: Prisma.PointOfSaleFindUniqueArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload> | null;
                };
                findUniqueOrThrow: {
                    args: Prisma.PointOfSaleFindUniqueOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>;
                };
                findFirst: {
                    args: Prisma.PointOfSaleFindFirstArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload> | null;
                };
                findFirstOrThrow: {
                    args: Prisma.PointOfSaleFindFirstOrThrowArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>;
                };
                findMany: {
                    args: Prisma.PointOfSaleFindManyArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>[];
                };
                create: {
                    args: Prisma.PointOfSaleCreateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>;
                };
                createMany: {
                    args: Prisma.PointOfSaleCreateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                createManyAndReturn: {
                    args: Prisma.PointOfSaleCreateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>[];
                };
                delete: {
                    args: Prisma.PointOfSaleDeleteArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>;
                };
                update: {
                    args: Prisma.PointOfSaleUpdateArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>;
                };
                deleteMany: {
                    args: Prisma.PointOfSaleDeleteManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateMany: {
                    args: Prisma.PointOfSaleUpdateManyArgs<ExtArgs>;
                    result: BatchPayload;
                };
                updateManyAndReturn: {
                    args: Prisma.PointOfSaleUpdateManyAndReturnArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>[];
                };
                upsert: {
                    args: Prisma.PointOfSaleUpsertArgs<ExtArgs>;
                    result: runtime.Types.Utils.PayloadToResult<Prisma.$PointOfSalePayload>;
                };
                aggregate: {
                    args: Prisma.PointOfSaleAggregateArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.AggregatePointOfSale>;
                };
                groupBy: {
                    args: Prisma.PointOfSaleGroupByArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.PointOfSaleGroupByOutputType>[];
                };
                count: {
                    args: Prisma.PointOfSaleCountArgs<ExtArgs>;
                    result: runtime.Types.Utils.Optional<Prisma.PointOfSaleCountAggregateOutputType> | number;
                };
            };
        };
    };
} & {
    other: {
        payload: any;
        operations: {
            $executeRaw: {
                args: [query: TemplateStringsArray | Sql, ...values: any[]];
                result: any;
            };
            $executeRawUnsafe: {
                args: [query: string, ...values: any[]];
                result: any;
            };
            $queryRaw: {
                args: [query: TemplateStringsArray | Sql, ...values: any[]];
                result: any;
            };
            $queryRawUnsafe: {
                args: [query: string, ...values: any[]];
                result: any;
            };
        };
    };
};
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
export type StringFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'String'>;
export type ListStringFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'String[]'>;
export type DateTimeFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'DateTime'>;
export type ListDateTimeFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'DateTime[]'>;
export type EnumBusinessTypeFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'BusinessType'>;
export type ListEnumBusinessTypeFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'BusinessType[]'>;
export type BooleanFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'Boolean'>;
export type EnumMemberRoleFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'MemberRole'>;
export type ListEnumMemberRoleFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'MemberRole[]'>;
export type FloatFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'Float'>;
export type ListFloatFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'Float[]'>;
export type IntFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'Int'>;
export type ListIntFieldRefInput<$PrismaModel> = FieldRefInputType<$PrismaModel, 'Int[]'>;
export type BatchPayload = {
    count: number;
};
export declare const defineExtension: runtime.Types.Extensions.ExtendsHook<"define", TypeMapCb, runtime.Types.Extensions.DefaultArgs>;
export type DefaultPrismaClient = PrismaClient;
export type ErrorFormat = 'pretty' | 'colorless' | 'minimal';
export type PrismaClientOptions = ({
    adapter: runtime.SqlDriverAdapterFactory;
    accelerateUrl?: never;
} | {
    accelerateUrl: string;
    adapter?: never;
}) & {
    errorFormat?: ErrorFormat;
    log?: (LogLevel | LogDefinition)[];
    transactionOptions?: {
        maxWait?: number;
        timeout?: number;
        isolationLevel?: TransactionIsolationLevel;
    };
    omit?: GlobalOmitConfig;
    comments?: runtime.SqlCommenterPlugin[];
};
export type GlobalOmitConfig = {
    systemAssignment?: Prisma.SystemAssignmentOmit;
    business?: Prisma.BusinessOmit;
    businessMember?: Prisma.BusinessMemberOmit;
    branch?: Prisma.BranchOmit;
    pointOfSale?: Prisma.PointOfSaleOmit;
};
export type LogLevel = 'info' | 'query' | 'warn' | 'error';
export type LogDefinition = {
    level: LogLevel;
    emit: 'stdout' | 'event';
};
export type CheckIsLogLevel<T> = T extends LogLevel ? T : never;
export type GetLogType<T> = CheckIsLogLevel<T extends LogDefinition ? T['level'] : T>;
export type GetEvents<T extends any[]> = T extends Array<LogLevel | LogDefinition> ? GetLogType<T[number]> : never;
export type QueryEvent = {
    timestamp: Date;
    query: string;
    params: string;
    duration: number;
    target: string;
};
export type LogEvent = {
    timestamp: Date;
    message: string;
    target: string;
};
export type PrismaAction = 'findUnique' | 'findUniqueOrThrow' | 'findMany' | 'findFirst' | 'findFirstOrThrow' | 'create' | 'createMany' | 'createManyAndReturn' | 'update' | 'updateMany' | 'updateManyAndReturn' | 'upsert' | 'delete' | 'deleteMany' | 'executeRaw' | 'queryRaw' | 'aggregate' | 'count' | 'runCommandRaw' | 'findRaw' | 'groupBy';
export type TransactionClient = Omit<DefaultPrismaClient, runtime.ITXClientDenyList>;
