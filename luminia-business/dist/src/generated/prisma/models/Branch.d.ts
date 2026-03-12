import type * as runtime from "@prisma/client/runtime/client";
import type * as Prisma from "../internal/prismaNamespace";
export type BranchModel = runtime.Types.Result.DefaultSelection<Prisma.$BranchPayload>;
export type AggregateBranch = {
    _count: BranchCountAggregateOutputType | null;
    _avg: BranchAvgAggregateOutputType | null;
    _sum: BranchSumAggregateOutputType | null;
    _min: BranchMinAggregateOutputType | null;
    _max: BranchMaxAggregateOutputType | null;
};
export type BranchAvgAggregateOutputType = {
    latitude: number | null;
    longitude: number | null;
};
export type BranchSumAggregateOutputType = {
    latitude: number | null;
    longitude: number | null;
};
export type BranchMinAggregateOutputType = {
    id: string | null;
    businessId: string | null;
    code: string | null;
    name: string | null;
    region: string | null;
    address: string | null;
    municipality: string | null;
    phone: string | null;
    latitude: number | null;
    longitude: number | null;
    openingHours: string | null;
    active: boolean | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type BranchMaxAggregateOutputType = {
    id: string | null;
    businessId: string | null;
    code: string | null;
    name: string | null;
    region: string | null;
    address: string | null;
    municipality: string | null;
    phone: string | null;
    latitude: number | null;
    longitude: number | null;
    openingHours: string | null;
    active: boolean | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type BranchCountAggregateOutputType = {
    id: number;
    businessId: number;
    code: number;
    name: number;
    region: number;
    address: number;
    municipality: number;
    phone: number;
    latitude: number;
    longitude: number;
    openingHours: number;
    active: number;
    createdAt: number;
    updatedAt: number;
    createdBy: number;
    updatedBy: number;
    _all: number;
};
export type BranchAvgAggregateInputType = {
    latitude?: true;
    longitude?: true;
};
export type BranchSumAggregateInputType = {
    latitude?: true;
    longitude?: true;
};
export type BranchMinAggregateInputType = {
    id?: true;
    businessId?: true;
    code?: true;
    name?: true;
    region?: true;
    address?: true;
    municipality?: true;
    phone?: true;
    latitude?: true;
    longitude?: true;
    openingHours?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type BranchMaxAggregateInputType = {
    id?: true;
    businessId?: true;
    code?: true;
    name?: true;
    region?: true;
    address?: true;
    municipality?: true;
    phone?: true;
    latitude?: true;
    longitude?: true;
    openingHours?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type BranchCountAggregateInputType = {
    id?: true;
    businessId?: true;
    code?: true;
    name?: true;
    region?: true;
    address?: true;
    municipality?: true;
    phone?: true;
    latitude?: true;
    longitude?: true;
    openingHours?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
    _all?: true;
};
export type BranchAggregateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BranchWhereInput;
    orderBy?: Prisma.BranchOrderByWithRelationInput | Prisma.BranchOrderByWithRelationInput[];
    cursor?: Prisma.BranchWhereUniqueInput;
    take?: number;
    skip?: number;
    _count?: true | BranchCountAggregateInputType;
    _avg?: BranchAvgAggregateInputType;
    _sum?: BranchSumAggregateInputType;
    _min?: BranchMinAggregateInputType;
    _max?: BranchMaxAggregateInputType;
};
export type GetBranchAggregateType<T extends BranchAggregateArgs> = {
    [P in keyof T & keyof AggregateBranch]: P extends '_count' | 'count' ? T[P] extends true ? number : Prisma.GetScalarType<T[P], AggregateBranch[P]> : Prisma.GetScalarType<T[P], AggregateBranch[P]>;
};
export type BranchGroupByArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BranchWhereInput;
    orderBy?: Prisma.BranchOrderByWithAggregationInput | Prisma.BranchOrderByWithAggregationInput[];
    by: Prisma.BranchScalarFieldEnum[] | Prisma.BranchScalarFieldEnum;
    having?: Prisma.BranchScalarWhereWithAggregatesInput;
    take?: number;
    skip?: number;
    _count?: BranchCountAggregateInputType | true;
    _avg?: BranchAvgAggregateInputType;
    _sum?: BranchSumAggregateInputType;
    _min?: BranchMinAggregateInputType;
    _max?: BranchMaxAggregateInputType;
};
export type BranchGroupByOutputType = {
    id: string;
    businessId: string;
    code: string | null;
    name: string;
    region: string;
    address: string | null;
    municipality: string | null;
    phone: string | null;
    latitude: number | null;
    longitude: number | null;
    openingHours: string | null;
    active: boolean;
    createdAt: Date;
    updatedAt: Date;
    createdBy: string;
    updatedBy: string | null;
    _count: BranchCountAggregateOutputType | null;
    _avg: BranchAvgAggregateOutputType | null;
    _sum: BranchSumAggregateOutputType | null;
    _min: BranchMinAggregateOutputType | null;
    _max: BranchMaxAggregateOutputType | null;
};
type GetBranchGroupByPayload<T extends BranchGroupByArgs> = Prisma.PrismaPromise<Array<Prisma.PickEnumerable<BranchGroupByOutputType, T['by']> & {
    [P in ((keyof T) & (keyof BranchGroupByOutputType))]: P extends '_count' ? T[P] extends boolean ? number : Prisma.GetScalarType<T[P], BranchGroupByOutputType[P]> : Prisma.GetScalarType<T[P], BranchGroupByOutputType[P]>;
}>>;
export type BranchWhereInput = {
    AND?: Prisma.BranchWhereInput | Prisma.BranchWhereInput[];
    OR?: Prisma.BranchWhereInput[];
    NOT?: Prisma.BranchWhereInput | Prisma.BranchWhereInput[];
    id?: Prisma.UuidFilter<"Branch"> | string;
    businessId?: Prisma.UuidFilter<"Branch"> | string;
    code?: Prisma.StringNullableFilter<"Branch"> | string | null;
    name?: Prisma.StringFilter<"Branch"> | string;
    region?: Prisma.StringFilter<"Branch"> | string;
    address?: Prisma.StringNullableFilter<"Branch"> | string | null;
    municipality?: Prisma.StringNullableFilter<"Branch"> | string | null;
    phone?: Prisma.StringNullableFilter<"Branch"> | string | null;
    latitude?: Prisma.FloatNullableFilter<"Branch"> | number | null;
    longitude?: Prisma.FloatNullableFilter<"Branch"> | number | null;
    openingHours?: Prisma.StringNullableFilter<"Branch"> | string | null;
    active?: Prisma.BoolFilter<"Branch"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"Branch"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"Branch"> | Date | string;
    createdBy?: Prisma.StringFilter<"Branch"> | string;
    updatedBy?: Prisma.StringNullableFilter<"Branch"> | string | null;
    business?: Prisma.XOR<Prisma.BusinessScalarRelationFilter, Prisma.BusinessWhereInput>;
    pointsOfSale?: Prisma.PointOfSaleListRelationFilter;
};
export type BranchOrderByWithRelationInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    code?: Prisma.SortOrderInput | Prisma.SortOrder;
    name?: Prisma.SortOrder;
    region?: Prisma.SortOrder;
    address?: Prisma.SortOrderInput | Prisma.SortOrder;
    municipality?: Prisma.SortOrderInput | Prisma.SortOrder;
    phone?: Prisma.SortOrderInput | Prisma.SortOrder;
    latitude?: Prisma.SortOrderInput | Prisma.SortOrder;
    longitude?: Prisma.SortOrderInput | Prisma.SortOrder;
    openingHours?: Prisma.SortOrderInput | Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    business?: Prisma.BusinessOrderByWithRelationInput;
    pointsOfSale?: Prisma.PointOfSaleOrderByRelationAggregateInput;
};
export type BranchWhereUniqueInput = Prisma.AtLeast<{
    id?: string;
    AND?: Prisma.BranchWhereInput | Prisma.BranchWhereInput[];
    OR?: Prisma.BranchWhereInput[];
    NOT?: Prisma.BranchWhereInput | Prisma.BranchWhereInput[];
    businessId?: Prisma.UuidFilter<"Branch"> | string;
    code?: Prisma.StringNullableFilter<"Branch"> | string | null;
    name?: Prisma.StringFilter<"Branch"> | string;
    region?: Prisma.StringFilter<"Branch"> | string;
    address?: Prisma.StringNullableFilter<"Branch"> | string | null;
    municipality?: Prisma.StringNullableFilter<"Branch"> | string | null;
    phone?: Prisma.StringNullableFilter<"Branch"> | string | null;
    latitude?: Prisma.FloatNullableFilter<"Branch"> | number | null;
    longitude?: Prisma.FloatNullableFilter<"Branch"> | number | null;
    openingHours?: Prisma.StringNullableFilter<"Branch"> | string | null;
    active?: Prisma.BoolFilter<"Branch"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"Branch"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"Branch"> | Date | string;
    createdBy?: Prisma.StringFilter<"Branch"> | string;
    updatedBy?: Prisma.StringNullableFilter<"Branch"> | string | null;
    business?: Prisma.XOR<Prisma.BusinessScalarRelationFilter, Prisma.BusinessWhereInput>;
    pointsOfSale?: Prisma.PointOfSaleListRelationFilter;
}, "id">;
export type BranchOrderByWithAggregationInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    code?: Prisma.SortOrderInput | Prisma.SortOrder;
    name?: Prisma.SortOrder;
    region?: Prisma.SortOrder;
    address?: Prisma.SortOrderInput | Prisma.SortOrder;
    municipality?: Prisma.SortOrderInput | Prisma.SortOrder;
    phone?: Prisma.SortOrderInput | Prisma.SortOrder;
    latitude?: Prisma.SortOrderInput | Prisma.SortOrder;
    longitude?: Prisma.SortOrderInput | Prisma.SortOrder;
    openingHours?: Prisma.SortOrderInput | Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    _count?: Prisma.BranchCountOrderByAggregateInput;
    _avg?: Prisma.BranchAvgOrderByAggregateInput;
    _max?: Prisma.BranchMaxOrderByAggregateInput;
    _min?: Prisma.BranchMinOrderByAggregateInput;
    _sum?: Prisma.BranchSumOrderByAggregateInput;
};
export type BranchScalarWhereWithAggregatesInput = {
    AND?: Prisma.BranchScalarWhereWithAggregatesInput | Prisma.BranchScalarWhereWithAggregatesInput[];
    OR?: Prisma.BranchScalarWhereWithAggregatesInput[];
    NOT?: Prisma.BranchScalarWhereWithAggregatesInput | Prisma.BranchScalarWhereWithAggregatesInput[];
    id?: Prisma.UuidWithAggregatesFilter<"Branch"> | string;
    businessId?: Prisma.UuidWithAggregatesFilter<"Branch"> | string;
    code?: Prisma.StringNullableWithAggregatesFilter<"Branch"> | string | null;
    name?: Prisma.StringWithAggregatesFilter<"Branch"> | string;
    region?: Prisma.StringWithAggregatesFilter<"Branch"> | string;
    address?: Prisma.StringNullableWithAggregatesFilter<"Branch"> | string | null;
    municipality?: Prisma.StringNullableWithAggregatesFilter<"Branch"> | string | null;
    phone?: Prisma.StringNullableWithAggregatesFilter<"Branch"> | string | null;
    latitude?: Prisma.FloatNullableWithAggregatesFilter<"Branch"> | number | null;
    longitude?: Prisma.FloatNullableWithAggregatesFilter<"Branch"> | number | null;
    openingHours?: Prisma.StringNullableWithAggregatesFilter<"Branch"> | string | null;
    active?: Prisma.BoolWithAggregatesFilter<"Branch"> | boolean;
    createdAt?: Prisma.DateTimeWithAggregatesFilter<"Branch"> | Date | string;
    updatedAt?: Prisma.DateTimeWithAggregatesFilter<"Branch"> | Date | string;
    createdBy?: Prisma.StringWithAggregatesFilter<"Branch"> | string;
    updatedBy?: Prisma.StringNullableWithAggregatesFilter<"Branch"> | string | null;
};
export type BranchCreateInput = {
    id?: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    business: Prisma.BusinessCreateNestedOneWithoutBranchesInput;
    pointsOfSale?: Prisma.PointOfSaleCreateNestedManyWithoutBranchInput;
};
export type BranchUncheckedCreateInput = {
    id?: string;
    businessId: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    pointsOfSale?: Prisma.PointOfSaleUncheckedCreateNestedManyWithoutBranchInput;
};
export type BranchUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    business?: Prisma.BusinessUpdateOneRequiredWithoutBranchesNestedInput;
    pointsOfSale?: Prisma.PointOfSaleUpdateManyWithoutBranchNestedInput;
};
export type BranchUncheckedUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    businessId?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    pointsOfSale?: Prisma.PointOfSaleUncheckedUpdateManyWithoutBranchNestedInput;
};
export type BranchCreateManyInput = {
    id?: string;
    businessId: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type BranchUpdateManyMutationInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type BranchUncheckedUpdateManyInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    businessId?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type BranchListRelationFilter = {
    every?: Prisma.BranchWhereInput;
    some?: Prisma.BranchWhereInput;
    none?: Prisma.BranchWhereInput;
};
export type BranchOrderByRelationAggregateInput = {
    _count?: Prisma.SortOrder;
};
export type BranchCountOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    code?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    region?: Prisma.SortOrder;
    address?: Prisma.SortOrder;
    municipality?: Prisma.SortOrder;
    phone?: Prisma.SortOrder;
    latitude?: Prisma.SortOrder;
    longitude?: Prisma.SortOrder;
    openingHours?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type BranchAvgOrderByAggregateInput = {
    latitude?: Prisma.SortOrder;
    longitude?: Prisma.SortOrder;
};
export type BranchMaxOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    code?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    region?: Prisma.SortOrder;
    address?: Prisma.SortOrder;
    municipality?: Prisma.SortOrder;
    phone?: Prisma.SortOrder;
    latitude?: Prisma.SortOrder;
    longitude?: Prisma.SortOrder;
    openingHours?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type BranchMinOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    code?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    region?: Prisma.SortOrder;
    address?: Prisma.SortOrder;
    municipality?: Prisma.SortOrder;
    phone?: Prisma.SortOrder;
    latitude?: Prisma.SortOrder;
    longitude?: Prisma.SortOrder;
    openingHours?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type BranchSumOrderByAggregateInput = {
    latitude?: Prisma.SortOrder;
    longitude?: Prisma.SortOrder;
};
export type BranchScalarRelationFilter = {
    is?: Prisma.BranchWhereInput;
    isNot?: Prisma.BranchWhereInput;
};
export type BranchCreateNestedManyWithoutBusinessInput = {
    create?: Prisma.XOR<Prisma.BranchCreateWithoutBusinessInput, Prisma.BranchUncheckedCreateWithoutBusinessInput> | Prisma.BranchCreateWithoutBusinessInput[] | Prisma.BranchUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.BranchCreateOrConnectWithoutBusinessInput | Prisma.BranchCreateOrConnectWithoutBusinessInput[];
    createMany?: Prisma.BranchCreateManyBusinessInputEnvelope;
    connect?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
};
export type BranchUncheckedCreateNestedManyWithoutBusinessInput = {
    create?: Prisma.XOR<Prisma.BranchCreateWithoutBusinessInput, Prisma.BranchUncheckedCreateWithoutBusinessInput> | Prisma.BranchCreateWithoutBusinessInput[] | Prisma.BranchUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.BranchCreateOrConnectWithoutBusinessInput | Prisma.BranchCreateOrConnectWithoutBusinessInput[];
    createMany?: Prisma.BranchCreateManyBusinessInputEnvelope;
    connect?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
};
export type BranchUpdateManyWithoutBusinessNestedInput = {
    create?: Prisma.XOR<Prisma.BranchCreateWithoutBusinessInput, Prisma.BranchUncheckedCreateWithoutBusinessInput> | Prisma.BranchCreateWithoutBusinessInput[] | Prisma.BranchUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.BranchCreateOrConnectWithoutBusinessInput | Prisma.BranchCreateOrConnectWithoutBusinessInput[];
    upsert?: Prisma.BranchUpsertWithWhereUniqueWithoutBusinessInput | Prisma.BranchUpsertWithWhereUniqueWithoutBusinessInput[];
    createMany?: Prisma.BranchCreateManyBusinessInputEnvelope;
    set?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    disconnect?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    delete?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    connect?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    update?: Prisma.BranchUpdateWithWhereUniqueWithoutBusinessInput | Prisma.BranchUpdateWithWhereUniqueWithoutBusinessInput[];
    updateMany?: Prisma.BranchUpdateManyWithWhereWithoutBusinessInput | Prisma.BranchUpdateManyWithWhereWithoutBusinessInput[];
    deleteMany?: Prisma.BranchScalarWhereInput | Prisma.BranchScalarWhereInput[];
};
export type BranchUncheckedUpdateManyWithoutBusinessNestedInput = {
    create?: Prisma.XOR<Prisma.BranchCreateWithoutBusinessInput, Prisma.BranchUncheckedCreateWithoutBusinessInput> | Prisma.BranchCreateWithoutBusinessInput[] | Prisma.BranchUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.BranchCreateOrConnectWithoutBusinessInput | Prisma.BranchCreateOrConnectWithoutBusinessInput[];
    upsert?: Prisma.BranchUpsertWithWhereUniqueWithoutBusinessInput | Prisma.BranchUpsertWithWhereUniqueWithoutBusinessInput[];
    createMany?: Prisma.BranchCreateManyBusinessInputEnvelope;
    set?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    disconnect?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    delete?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    connect?: Prisma.BranchWhereUniqueInput | Prisma.BranchWhereUniqueInput[];
    update?: Prisma.BranchUpdateWithWhereUniqueWithoutBusinessInput | Prisma.BranchUpdateWithWhereUniqueWithoutBusinessInput[];
    updateMany?: Prisma.BranchUpdateManyWithWhereWithoutBusinessInput | Prisma.BranchUpdateManyWithWhereWithoutBusinessInput[];
    deleteMany?: Prisma.BranchScalarWhereInput | Prisma.BranchScalarWhereInput[];
};
export type NullableFloatFieldUpdateOperationsInput = {
    set?: number | null;
    increment?: number;
    decrement?: number;
    multiply?: number;
    divide?: number;
};
export type BranchCreateNestedOneWithoutPointsOfSaleInput = {
    create?: Prisma.XOR<Prisma.BranchCreateWithoutPointsOfSaleInput, Prisma.BranchUncheckedCreateWithoutPointsOfSaleInput>;
    connectOrCreate?: Prisma.BranchCreateOrConnectWithoutPointsOfSaleInput;
    connect?: Prisma.BranchWhereUniqueInput;
};
export type BranchUpdateOneRequiredWithoutPointsOfSaleNestedInput = {
    create?: Prisma.XOR<Prisma.BranchCreateWithoutPointsOfSaleInput, Prisma.BranchUncheckedCreateWithoutPointsOfSaleInput>;
    connectOrCreate?: Prisma.BranchCreateOrConnectWithoutPointsOfSaleInput;
    upsert?: Prisma.BranchUpsertWithoutPointsOfSaleInput;
    connect?: Prisma.BranchWhereUniqueInput;
    update?: Prisma.XOR<Prisma.XOR<Prisma.BranchUpdateToOneWithWhereWithoutPointsOfSaleInput, Prisma.BranchUpdateWithoutPointsOfSaleInput>, Prisma.BranchUncheckedUpdateWithoutPointsOfSaleInput>;
};
export type BranchCreateWithoutBusinessInput = {
    id?: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    pointsOfSale?: Prisma.PointOfSaleCreateNestedManyWithoutBranchInput;
};
export type BranchUncheckedCreateWithoutBusinessInput = {
    id?: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    pointsOfSale?: Prisma.PointOfSaleUncheckedCreateNestedManyWithoutBranchInput;
};
export type BranchCreateOrConnectWithoutBusinessInput = {
    where: Prisma.BranchWhereUniqueInput;
    create: Prisma.XOR<Prisma.BranchCreateWithoutBusinessInput, Prisma.BranchUncheckedCreateWithoutBusinessInput>;
};
export type BranchCreateManyBusinessInputEnvelope = {
    data: Prisma.BranchCreateManyBusinessInput | Prisma.BranchCreateManyBusinessInput[];
    skipDuplicates?: boolean;
};
export type BranchUpsertWithWhereUniqueWithoutBusinessInput = {
    where: Prisma.BranchWhereUniqueInput;
    update: Prisma.XOR<Prisma.BranchUpdateWithoutBusinessInput, Prisma.BranchUncheckedUpdateWithoutBusinessInput>;
    create: Prisma.XOR<Prisma.BranchCreateWithoutBusinessInput, Prisma.BranchUncheckedCreateWithoutBusinessInput>;
};
export type BranchUpdateWithWhereUniqueWithoutBusinessInput = {
    where: Prisma.BranchWhereUniqueInput;
    data: Prisma.XOR<Prisma.BranchUpdateWithoutBusinessInput, Prisma.BranchUncheckedUpdateWithoutBusinessInput>;
};
export type BranchUpdateManyWithWhereWithoutBusinessInput = {
    where: Prisma.BranchScalarWhereInput;
    data: Prisma.XOR<Prisma.BranchUpdateManyMutationInput, Prisma.BranchUncheckedUpdateManyWithoutBusinessInput>;
};
export type BranchScalarWhereInput = {
    AND?: Prisma.BranchScalarWhereInput | Prisma.BranchScalarWhereInput[];
    OR?: Prisma.BranchScalarWhereInput[];
    NOT?: Prisma.BranchScalarWhereInput | Prisma.BranchScalarWhereInput[];
    id?: Prisma.UuidFilter<"Branch"> | string;
    businessId?: Prisma.UuidFilter<"Branch"> | string;
    code?: Prisma.StringNullableFilter<"Branch"> | string | null;
    name?: Prisma.StringFilter<"Branch"> | string;
    region?: Prisma.StringFilter<"Branch"> | string;
    address?: Prisma.StringNullableFilter<"Branch"> | string | null;
    municipality?: Prisma.StringNullableFilter<"Branch"> | string | null;
    phone?: Prisma.StringNullableFilter<"Branch"> | string | null;
    latitude?: Prisma.FloatNullableFilter<"Branch"> | number | null;
    longitude?: Prisma.FloatNullableFilter<"Branch"> | number | null;
    openingHours?: Prisma.StringNullableFilter<"Branch"> | string | null;
    active?: Prisma.BoolFilter<"Branch"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"Branch"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"Branch"> | Date | string;
    createdBy?: Prisma.StringFilter<"Branch"> | string;
    updatedBy?: Prisma.StringNullableFilter<"Branch"> | string | null;
};
export type BranchCreateWithoutPointsOfSaleInput = {
    id?: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    business: Prisma.BusinessCreateNestedOneWithoutBranchesInput;
};
export type BranchUncheckedCreateWithoutPointsOfSaleInput = {
    id?: string;
    businessId: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type BranchCreateOrConnectWithoutPointsOfSaleInput = {
    where: Prisma.BranchWhereUniqueInput;
    create: Prisma.XOR<Prisma.BranchCreateWithoutPointsOfSaleInput, Prisma.BranchUncheckedCreateWithoutPointsOfSaleInput>;
};
export type BranchUpsertWithoutPointsOfSaleInput = {
    update: Prisma.XOR<Prisma.BranchUpdateWithoutPointsOfSaleInput, Prisma.BranchUncheckedUpdateWithoutPointsOfSaleInput>;
    create: Prisma.XOR<Prisma.BranchCreateWithoutPointsOfSaleInput, Prisma.BranchUncheckedCreateWithoutPointsOfSaleInput>;
    where?: Prisma.BranchWhereInput;
};
export type BranchUpdateToOneWithWhereWithoutPointsOfSaleInput = {
    where?: Prisma.BranchWhereInput;
    data: Prisma.XOR<Prisma.BranchUpdateWithoutPointsOfSaleInput, Prisma.BranchUncheckedUpdateWithoutPointsOfSaleInput>;
};
export type BranchUpdateWithoutPointsOfSaleInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    business?: Prisma.BusinessUpdateOneRequiredWithoutBranchesNestedInput;
};
export type BranchUncheckedUpdateWithoutPointsOfSaleInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    businessId?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type BranchCreateManyBusinessInput = {
    id?: string;
    code?: string | null;
    name: string;
    region: string;
    address?: string | null;
    municipality?: string | null;
    phone?: string | null;
    latitude?: number | null;
    longitude?: number | null;
    openingHours?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type BranchUpdateWithoutBusinessInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    pointsOfSale?: Prisma.PointOfSaleUpdateManyWithoutBranchNestedInput;
};
export type BranchUncheckedUpdateWithoutBusinessInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    pointsOfSale?: Prisma.PointOfSaleUncheckedUpdateManyWithoutBranchNestedInput;
};
export type BranchUncheckedUpdateManyWithoutBusinessInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    region?: Prisma.StringFieldUpdateOperationsInput | string;
    address?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    municipality?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    phone?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    latitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    longitude?: Prisma.NullableFloatFieldUpdateOperationsInput | number | null;
    openingHours?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type BranchCountOutputType = {
    pointsOfSale: number;
};
export type BranchCountOutputTypeSelect<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    pointsOfSale?: boolean | BranchCountOutputTypeCountPointsOfSaleArgs;
};
export type BranchCountOutputTypeDefaultArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchCountOutputTypeSelect<ExtArgs> | null;
};
export type BranchCountOutputTypeCountPointsOfSaleArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.PointOfSaleWhereInput;
};
export type BranchSelect<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    businessId?: boolean;
    code?: boolean;
    name?: boolean;
    region?: boolean;
    address?: boolean;
    municipality?: boolean;
    phone?: boolean;
    latitude?: boolean;
    longitude?: boolean;
    openingHours?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
    pointsOfSale?: boolean | Prisma.Branch$pointsOfSaleArgs<ExtArgs>;
    _count?: boolean | Prisma.BranchCountOutputTypeDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["branch"]>;
export type BranchSelectCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    businessId?: boolean;
    code?: boolean;
    name?: boolean;
    region?: boolean;
    address?: boolean;
    municipality?: boolean;
    phone?: boolean;
    latitude?: boolean;
    longitude?: boolean;
    openingHours?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["branch"]>;
export type BranchSelectUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    businessId?: boolean;
    code?: boolean;
    name?: boolean;
    region?: boolean;
    address?: boolean;
    municipality?: boolean;
    phone?: boolean;
    latitude?: boolean;
    longitude?: boolean;
    openingHours?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["branch"]>;
export type BranchSelectScalar = {
    id?: boolean;
    businessId?: boolean;
    code?: boolean;
    name?: boolean;
    region?: boolean;
    address?: boolean;
    municipality?: boolean;
    phone?: boolean;
    latitude?: boolean;
    longitude?: boolean;
    openingHours?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
};
export type BranchOmit<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetOmit<"id" | "businessId" | "code" | "name" | "region" | "address" | "municipality" | "phone" | "latitude" | "longitude" | "openingHours" | "active" | "createdAt" | "updatedAt" | "createdBy" | "updatedBy", ExtArgs["result"]["branch"]>;
export type BranchInclude<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
    pointsOfSale?: boolean | Prisma.Branch$pointsOfSaleArgs<ExtArgs>;
    _count?: boolean | Prisma.BranchCountOutputTypeDefaultArgs<ExtArgs>;
};
export type BranchIncludeCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
};
export type BranchIncludeUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
};
export type $BranchPayload<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    name: "Branch";
    objects: {
        business: Prisma.$BusinessPayload<ExtArgs>;
        pointsOfSale: Prisma.$PointOfSalePayload<ExtArgs>[];
    };
    scalars: runtime.Types.Extensions.GetPayloadResult<{
        id: string;
        businessId: string;
        code: string | null;
        name: string;
        region: string;
        address: string | null;
        municipality: string | null;
        phone: string | null;
        latitude: number | null;
        longitude: number | null;
        openingHours: string | null;
        active: boolean;
        createdAt: Date;
        updatedAt: Date;
        createdBy: string;
        updatedBy: string | null;
    }, ExtArgs["result"]["branch"]>;
    composites: {};
};
export type BranchGetPayload<S extends boolean | null | undefined | BranchDefaultArgs> = runtime.Types.Result.GetResult<Prisma.$BranchPayload, S>;
export type BranchCountArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = Omit<BranchFindManyArgs, 'select' | 'include' | 'distinct' | 'omit'> & {
    select?: BranchCountAggregateInputType | true;
};
export interface BranchDelegate<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> {
    [K: symbol]: {
        types: Prisma.TypeMap<ExtArgs>['model']['Branch'];
        meta: {
            name: 'Branch';
        };
    };
    findUnique<T extends BranchFindUniqueArgs>(args: Prisma.SelectSubset<T, BranchFindUniqueArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "findUnique", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findUniqueOrThrow<T extends BranchFindUniqueOrThrowArgs>(args: Prisma.SelectSubset<T, BranchFindUniqueOrThrowArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "findUniqueOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findFirst<T extends BranchFindFirstArgs>(args?: Prisma.SelectSubset<T, BranchFindFirstArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "findFirst", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findFirstOrThrow<T extends BranchFindFirstOrThrowArgs>(args?: Prisma.SelectSubset<T, BranchFindFirstOrThrowArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "findFirstOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findMany<T extends BranchFindManyArgs>(args?: Prisma.SelectSubset<T, BranchFindManyArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "findMany", GlobalOmitOptions>>;
    create<T extends BranchCreateArgs>(args: Prisma.SelectSubset<T, BranchCreateArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "create", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    createMany<T extends BranchCreateManyArgs>(args?: Prisma.SelectSubset<T, BranchCreateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    createManyAndReturn<T extends BranchCreateManyAndReturnArgs>(args?: Prisma.SelectSubset<T, BranchCreateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "createManyAndReturn", GlobalOmitOptions>>;
    delete<T extends BranchDeleteArgs>(args: Prisma.SelectSubset<T, BranchDeleteArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "delete", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    update<T extends BranchUpdateArgs>(args: Prisma.SelectSubset<T, BranchUpdateArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "update", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    deleteMany<T extends BranchDeleteManyArgs>(args?: Prisma.SelectSubset<T, BranchDeleteManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateMany<T extends BranchUpdateManyArgs>(args: Prisma.SelectSubset<T, BranchUpdateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateManyAndReturn<T extends BranchUpdateManyAndReturnArgs>(args: Prisma.SelectSubset<T, BranchUpdateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "updateManyAndReturn", GlobalOmitOptions>>;
    upsert<T extends BranchUpsertArgs>(args: Prisma.SelectSubset<T, BranchUpsertArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "upsert", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    count<T extends BranchCountArgs>(args?: Prisma.Subset<T, BranchCountArgs>): Prisma.PrismaPromise<T extends runtime.Types.Utils.Record<'select', any> ? T['select'] extends true ? number : Prisma.GetScalarType<T['select'], BranchCountAggregateOutputType> : number>;
    aggregate<T extends BranchAggregateArgs>(args: Prisma.Subset<T, BranchAggregateArgs>): Prisma.PrismaPromise<GetBranchAggregateType<T>>;
    groupBy<T extends BranchGroupByArgs, HasSelectOrTake extends Prisma.Or<Prisma.Extends<'skip', Prisma.Keys<T>>, Prisma.Extends<'take', Prisma.Keys<T>>>, OrderByArg extends Prisma.True extends HasSelectOrTake ? {
        orderBy: BranchGroupByArgs['orderBy'];
    } : {
        orderBy?: BranchGroupByArgs['orderBy'];
    }, OrderFields extends Prisma.ExcludeUnderscoreKeys<Prisma.Keys<Prisma.MaybeTupleToUnion<T['orderBy']>>>, ByFields extends Prisma.MaybeTupleToUnion<T['by']>, ByValid extends Prisma.Has<ByFields, OrderFields>, HavingFields extends Prisma.GetHavingFields<T['having']>, HavingValid extends Prisma.Has<ByFields, HavingFields>, ByEmpty extends T['by'] extends never[] ? Prisma.True : Prisma.False, InputErrors extends ByEmpty extends Prisma.True ? `Error: "by" must not be empty.` : HavingValid extends Prisma.False ? {
        [P in HavingFields]: P extends ByFields ? never : P extends string ? `Error: Field "${P}" used in "having" needs to be provided in "by".` : [
            Error,
            'Field ',
            P,
            ` in "having" needs to be provided in "by"`
        ];
    }[HavingFields] : 'take' extends Prisma.Keys<T> ? 'orderBy' extends Prisma.Keys<T> ? ByValid extends Prisma.True ? {} : {
        [P in OrderFields]: P extends ByFields ? never : `Error: Field "${P}" in "orderBy" needs to be provided in "by"`;
    }[OrderFields] : 'Error: If you provide "take", you also need to provide "orderBy"' : 'skip' extends Prisma.Keys<T> ? 'orderBy' extends Prisma.Keys<T> ? ByValid extends Prisma.True ? {} : {
        [P in OrderFields]: P extends ByFields ? never : `Error: Field "${P}" in "orderBy" needs to be provided in "by"`;
    }[OrderFields] : 'Error: If you provide "skip", you also need to provide "orderBy"' : ByValid extends Prisma.True ? {} : {
        [P in OrderFields]: P extends ByFields ? never : `Error: Field "${P}" in "orderBy" needs to be provided in "by"`;
    }[OrderFields]>(args: Prisma.SubsetIntersection<T, BranchGroupByArgs, OrderByArg> & InputErrors): {} extends InputErrors ? GetBranchGroupByPayload<T> : Prisma.PrismaPromise<InputErrors>;
    readonly fields: BranchFieldRefs;
}
export interface Prisma__BranchClient<T, Null = never, ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> extends Prisma.PrismaPromise<T> {
    readonly [Symbol.toStringTag]: "PrismaPromise";
    business<T extends Prisma.BusinessDefaultArgs<ExtArgs> = {}>(args?: Prisma.Subset<T, Prisma.BusinessDefaultArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "findUniqueOrThrow", GlobalOmitOptions> | Null, Null, ExtArgs, GlobalOmitOptions>;
    pointsOfSale<T extends Prisma.Branch$pointsOfSaleArgs<ExtArgs> = {}>(args?: Prisma.Subset<T, Prisma.Branch$pointsOfSaleArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "findMany", GlobalOmitOptions> | Null>;
    then<TResult1 = T, TResult2 = never>(onfulfilled?: ((value: T) => TResult1 | PromiseLike<TResult1>) | undefined | null, onrejected?: ((reason: any) => TResult2 | PromiseLike<TResult2>) | undefined | null): runtime.Types.Utils.JsPromise<TResult1 | TResult2>;
    catch<TResult = never>(onrejected?: ((reason: any) => TResult | PromiseLike<TResult>) | undefined | null): runtime.Types.Utils.JsPromise<T | TResult>;
    finally(onfinally?: (() => void) | undefined | null): runtime.Types.Utils.JsPromise<T>;
}
export interface BranchFieldRefs {
    readonly id: Prisma.FieldRef<"Branch", 'String'>;
    readonly businessId: Prisma.FieldRef<"Branch", 'String'>;
    readonly code: Prisma.FieldRef<"Branch", 'String'>;
    readonly name: Prisma.FieldRef<"Branch", 'String'>;
    readonly region: Prisma.FieldRef<"Branch", 'String'>;
    readonly address: Prisma.FieldRef<"Branch", 'String'>;
    readonly municipality: Prisma.FieldRef<"Branch", 'String'>;
    readonly phone: Prisma.FieldRef<"Branch", 'String'>;
    readonly latitude: Prisma.FieldRef<"Branch", 'Float'>;
    readonly longitude: Prisma.FieldRef<"Branch", 'Float'>;
    readonly openingHours: Prisma.FieldRef<"Branch", 'String'>;
    readonly active: Prisma.FieldRef<"Branch", 'Boolean'>;
    readonly createdAt: Prisma.FieldRef<"Branch", 'DateTime'>;
    readonly updatedAt: Prisma.FieldRef<"Branch", 'DateTime'>;
    readonly createdBy: Prisma.FieldRef<"Branch", 'String'>;
    readonly updatedBy: Prisma.FieldRef<"Branch", 'String'>;
}
export type BranchFindUniqueArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    where: Prisma.BranchWhereUniqueInput;
};
export type BranchFindUniqueOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    where: Prisma.BranchWhereUniqueInput;
};
export type BranchFindFirstArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    where?: Prisma.BranchWhereInput;
    orderBy?: Prisma.BranchOrderByWithRelationInput | Prisma.BranchOrderByWithRelationInput[];
    cursor?: Prisma.BranchWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.BranchScalarFieldEnum | Prisma.BranchScalarFieldEnum[];
};
export type BranchFindFirstOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    where?: Prisma.BranchWhereInput;
    orderBy?: Prisma.BranchOrderByWithRelationInput | Prisma.BranchOrderByWithRelationInput[];
    cursor?: Prisma.BranchWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.BranchScalarFieldEnum | Prisma.BranchScalarFieldEnum[];
};
export type BranchFindManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    where?: Prisma.BranchWhereInput;
    orderBy?: Prisma.BranchOrderByWithRelationInput | Prisma.BranchOrderByWithRelationInput[];
    cursor?: Prisma.BranchWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.BranchScalarFieldEnum | Prisma.BranchScalarFieldEnum[];
};
export type BranchCreateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.BranchCreateInput, Prisma.BranchUncheckedCreateInput>;
};
export type BranchCreateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.BranchCreateManyInput | Prisma.BranchCreateManyInput[];
    skipDuplicates?: boolean;
};
export type BranchCreateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelectCreateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    data: Prisma.BranchCreateManyInput | Prisma.BranchCreateManyInput[];
    skipDuplicates?: boolean;
    include?: Prisma.BranchIncludeCreateManyAndReturn<ExtArgs> | null;
};
export type BranchUpdateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.BranchUpdateInput, Prisma.BranchUncheckedUpdateInput>;
    where: Prisma.BranchWhereUniqueInput;
};
export type BranchUpdateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.XOR<Prisma.BranchUpdateManyMutationInput, Prisma.BranchUncheckedUpdateManyInput>;
    where?: Prisma.BranchWhereInput;
    limit?: number;
};
export type BranchUpdateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelectUpdateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    data: Prisma.XOR<Prisma.BranchUpdateManyMutationInput, Prisma.BranchUncheckedUpdateManyInput>;
    where?: Prisma.BranchWhereInput;
    limit?: number;
    include?: Prisma.BranchIncludeUpdateManyAndReturn<ExtArgs> | null;
};
export type BranchUpsertArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    where: Prisma.BranchWhereUniqueInput;
    create: Prisma.XOR<Prisma.BranchCreateInput, Prisma.BranchUncheckedCreateInput>;
    update: Prisma.XOR<Prisma.BranchUpdateInput, Prisma.BranchUncheckedUpdateInput>;
};
export type BranchDeleteArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
    where: Prisma.BranchWhereUniqueInput;
};
export type BranchDeleteManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BranchWhereInput;
    limit?: number;
};
export type Branch$pointsOfSaleArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
    where?: Prisma.PointOfSaleWhereInput;
    orderBy?: Prisma.PointOfSaleOrderByWithRelationInput | Prisma.PointOfSaleOrderByWithRelationInput[];
    cursor?: Prisma.PointOfSaleWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.PointOfSaleScalarFieldEnum | Prisma.PointOfSaleScalarFieldEnum[];
};
export type BranchDefaultArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BranchSelect<ExtArgs> | null;
    omit?: Prisma.BranchOmit<ExtArgs> | null;
    include?: Prisma.BranchInclude<ExtArgs> | null;
};
export {};
