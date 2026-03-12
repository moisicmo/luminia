import type * as runtime from "@prisma/client/runtime/client";
import type * as Prisma from "../internal/prismaNamespace";
export type PointOfSaleModel = runtime.Types.Result.DefaultSelection<Prisma.$PointOfSalePayload>;
export type AggregatePointOfSale = {
    _count: PointOfSaleCountAggregateOutputType | null;
    _min: PointOfSaleMinAggregateOutputType | null;
    _max: PointOfSaleMaxAggregateOutputType | null;
};
export type PointOfSaleMinAggregateOutputType = {
    id: string | null;
    branchId: string | null;
    code: string | null;
    name: string | null;
    active: boolean | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type PointOfSaleMaxAggregateOutputType = {
    id: string | null;
    branchId: string | null;
    code: string | null;
    name: string | null;
    active: boolean | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type PointOfSaleCountAggregateOutputType = {
    id: number;
    branchId: number;
    code: number;
    name: number;
    active: number;
    createdAt: number;
    updatedAt: number;
    createdBy: number;
    updatedBy: number;
    _all: number;
};
export type PointOfSaleMinAggregateInputType = {
    id?: true;
    branchId?: true;
    code?: true;
    name?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type PointOfSaleMaxAggregateInputType = {
    id?: true;
    branchId?: true;
    code?: true;
    name?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type PointOfSaleCountAggregateInputType = {
    id?: true;
    branchId?: true;
    code?: true;
    name?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
    _all?: true;
};
export type PointOfSaleAggregateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.PointOfSaleWhereInput;
    orderBy?: Prisma.PointOfSaleOrderByWithRelationInput | Prisma.PointOfSaleOrderByWithRelationInput[];
    cursor?: Prisma.PointOfSaleWhereUniqueInput;
    take?: number;
    skip?: number;
    _count?: true | PointOfSaleCountAggregateInputType;
    _min?: PointOfSaleMinAggregateInputType;
    _max?: PointOfSaleMaxAggregateInputType;
};
export type GetPointOfSaleAggregateType<T extends PointOfSaleAggregateArgs> = {
    [P in keyof T & keyof AggregatePointOfSale]: P extends '_count' | 'count' ? T[P] extends true ? number : Prisma.GetScalarType<T[P], AggregatePointOfSale[P]> : Prisma.GetScalarType<T[P], AggregatePointOfSale[P]>;
};
export type PointOfSaleGroupByArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.PointOfSaleWhereInput;
    orderBy?: Prisma.PointOfSaleOrderByWithAggregationInput | Prisma.PointOfSaleOrderByWithAggregationInput[];
    by: Prisma.PointOfSaleScalarFieldEnum[] | Prisma.PointOfSaleScalarFieldEnum;
    having?: Prisma.PointOfSaleScalarWhereWithAggregatesInput;
    take?: number;
    skip?: number;
    _count?: PointOfSaleCountAggregateInputType | true;
    _min?: PointOfSaleMinAggregateInputType;
    _max?: PointOfSaleMaxAggregateInputType;
};
export type PointOfSaleGroupByOutputType = {
    id: string;
    branchId: string;
    code: string | null;
    name: string;
    active: boolean;
    createdAt: Date;
    updatedAt: Date;
    createdBy: string;
    updatedBy: string | null;
    _count: PointOfSaleCountAggregateOutputType | null;
    _min: PointOfSaleMinAggregateOutputType | null;
    _max: PointOfSaleMaxAggregateOutputType | null;
};
type GetPointOfSaleGroupByPayload<T extends PointOfSaleGroupByArgs> = Prisma.PrismaPromise<Array<Prisma.PickEnumerable<PointOfSaleGroupByOutputType, T['by']> & {
    [P in ((keyof T) & (keyof PointOfSaleGroupByOutputType))]: P extends '_count' ? T[P] extends boolean ? number : Prisma.GetScalarType<T[P], PointOfSaleGroupByOutputType[P]> : Prisma.GetScalarType<T[P], PointOfSaleGroupByOutputType[P]>;
}>>;
export type PointOfSaleWhereInput = {
    AND?: Prisma.PointOfSaleWhereInput | Prisma.PointOfSaleWhereInput[];
    OR?: Prisma.PointOfSaleWhereInput[];
    NOT?: Prisma.PointOfSaleWhereInput | Prisma.PointOfSaleWhereInput[];
    id?: Prisma.UuidFilter<"PointOfSale"> | string;
    branchId?: Prisma.UuidFilter<"PointOfSale"> | string;
    code?: Prisma.StringNullableFilter<"PointOfSale"> | string | null;
    name?: Prisma.StringFilter<"PointOfSale"> | string;
    active?: Prisma.BoolFilter<"PointOfSale"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"PointOfSale"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"PointOfSale"> | Date | string;
    createdBy?: Prisma.StringFilter<"PointOfSale"> | string;
    updatedBy?: Prisma.StringNullableFilter<"PointOfSale"> | string | null;
    branch?: Prisma.XOR<Prisma.BranchScalarRelationFilter, Prisma.BranchWhereInput>;
};
export type PointOfSaleOrderByWithRelationInput = {
    id?: Prisma.SortOrder;
    branchId?: Prisma.SortOrder;
    code?: Prisma.SortOrderInput | Prisma.SortOrder;
    name?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    branch?: Prisma.BranchOrderByWithRelationInput;
};
export type PointOfSaleWhereUniqueInput = Prisma.AtLeast<{
    id?: string;
    AND?: Prisma.PointOfSaleWhereInput | Prisma.PointOfSaleWhereInput[];
    OR?: Prisma.PointOfSaleWhereInput[];
    NOT?: Prisma.PointOfSaleWhereInput | Prisma.PointOfSaleWhereInput[];
    branchId?: Prisma.UuidFilter<"PointOfSale"> | string;
    code?: Prisma.StringNullableFilter<"PointOfSale"> | string | null;
    name?: Prisma.StringFilter<"PointOfSale"> | string;
    active?: Prisma.BoolFilter<"PointOfSale"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"PointOfSale"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"PointOfSale"> | Date | string;
    createdBy?: Prisma.StringFilter<"PointOfSale"> | string;
    updatedBy?: Prisma.StringNullableFilter<"PointOfSale"> | string | null;
    branch?: Prisma.XOR<Prisma.BranchScalarRelationFilter, Prisma.BranchWhereInput>;
}, "id">;
export type PointOfSaleOrderByWithAggregationInput = {
    id?: Prisma.SortOrder;
    branchId?: Prisma.SortOrder;
    code?: Prisma.SortOrderInput | Prisma.SortOrder;
    name?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    _count?: Prisma.PointOfSaleCountOrderByAggregateInput;
    _max?: Prisma.PointOfSaleMaxOrderByAggregateInput;
    _min?: Prisma.PointOfSaleMinOrderByAggregateInput;
};
export type PointOfSaleScalarWhereWithAggregatesInput = {
    AND?: Prisma.PointOfSaleScalarWhereWithAggregatesInput | Prisma.PointOfSaleScalarWhereWithAggregatesInput[];
    OR?: Prisma.PointOfSaleScalarWhereWithAggregatesInput[];
    NOT?: Prisma.PointOfSaleScalarWhereWithAggregatesInput | Prisma.PointOfSaleScalarWhereWithAggregatesInput[];
    id?: Prisma.UuidWithAggregatesFilter<"PointOfSale"> | string;
    branchId?: Prisma.UuidWithAggregatesFilter<"PointOfSale"> | string;
    code?: Prisma.StringNullableWithAggregatesFilter<"PointOfSale"> | string | null;
    name?: Prisma.StringWithAggregatesFilter<"PointOfSale"> | string;
    active?: Prisma.BoolWithAggregatesFilter<"PointOfSale"> | boolean;
    createdAt?: Prisma.DateTimeWithAggregatesFilter<"PointOfSale"> | Date | string;
    updatedAt?: Prisma.DateTimeWithAggregatesFilter<"PointOfSale"> | Date | string;
    createdBy?: Prisma.StringWithAggregatesFilter<"PointOfSale"> | string;
    updatedBy?: Prisma.StringNullableWithAggregatesFilter<"PointOfSale"> | string | null;
};
export type PointOfSaleCreateInput = {
    id?: string;
    code?: string | null;
    name: string;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    branch: Prisma.BranchCreateNestedOneWithoutPointsOfSaleInput;
};
export type PointOfSaleUncheckedCreateInput = {
    id?: string;
    branchId: string;
    code?: string | null;
    name: string;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type PointOfSaleUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    branch?: Prisma.BranchUpdateOneRequiredWithoutPointsOfSaleNestedInput;
};
export type PointOfSaleUncheckedUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    branchId?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type PointOfSaleCreateManyInput = {
    id?: string;
    branchId: string;
    code?: string | null;
    name: string;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type PointOfSaleUpdateManyMutationInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type PointOfSaleUncheckedUpdateManyInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    branchId?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type PointOfSaleListRelationFilter = {
    every?: Prisma.PointOfSaleWhereInput;
    some?: Prisma.PointOfSaleWhereInput;
    none?: Prisma.PointOfSaleWhereInput;
};
export type PointOfSaleOrderByRelationAggregateInput = {
    _count?: Prisma.SortOrder;
};
export type PointOfSaleCountOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    branchId?: Prisma.SortOrder;
    code?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type PointOfSaleMaxOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    branchId?: Prisma.SortOrder;
    code?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type PointOfSaleMinOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    branchId?: Prisma.SortOrder;
    code?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type PointOfSaleCreateNestedManyWithoutBranchInput = {
    create?: Prisma.XOR<Prisma.PointOfSaleCreateWithoutBranchInput, Prisma.PointOfSaleUncheckedCreateWithoutBranchInput> | Prisma.PointOfSaleCreateWithoutBranchInput[] | Prisma.PointOfSaleUncheckedCreateWithoutBranchInput[];
    connectOrCreate?: Prisma.PointOfSaleCreateOrConnectWithoutBranchInput | Prisma.PointOfSaleCreateOrConnectWithoutBranchInput[];
    createMany?: Prisma.PointOfSaleCreateManyBranchInputEnvelope;
    connect?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
};
export type PointOfSaleUncheckedCreateNestedManyWithoutBranchInput = {
    create?: Prisma.XOR<Prisma.PointOfSaleCreateWithoutBranchInput, Prisma.PointOfSaleUncheckedCreateWithoutBranchInput> | Prisma.PointOfSaleCreateWithoutBranchInput[] | Prisma.PointOfSaleUncheckedCreateWithoutBranchInput[];
    connectOrCreate?: Prisma.PointOfSaleCreateOrConnectWithoutBranchInput | Prisma.PointOfSaleCreateOrConnectWithoutBranchInput[];
    createMany?: Prisma.PointOfSaleCreateManyBranchInputEnvelope;
    connect?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
};
export type PointOfSaleUpdateManyWithoutBranchNestedInput = {
    create?: Prisma.XOR<Prisma.PointOfSaleCreateWithoutBranchInput, Prisma.PointOfSaleUncheckedCreateWithoutBranchInput> | Prisma.PointOfSaleCreateWithoutBranchInput[] | Prisma.PointOfSaleUncheckedCreateWithoutBranchInput[];
    connectOrCreate?: Prisma.PointOfSaleCreateOrConnectWithoutBranchInput | Prisma.PointOfSaleCreateOrConnectWithoutBranchInput[];
    upsert?: Prisma.PointOfSaleUpsertWithWhereUniqueWithoutBranchInput | Prisma.PointOfSaleUpsertWithWhereUniqueWithoutBranchInput[];
    createMany?: Prisma.PointOfSaleCreateManyBranchInputEnvelope;
    set?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    disconnect?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    delete?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    connect?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    update?: Prisma.PointOfSaleUpdateWithWhereUniqueWithoutBranchInput | Prisma.PointOfSaleUpdateWithWhereUniqueWithoutBranchInput[];
    updateMany?: Prisma.PointOfSaleUpdateManyWithWhereWithoutBranchInput | Prisma.PointOfSaleUpdateManyWithWhereWithoutBranchInput[];
    deleteMany?: Prisma.PointOfSaleScalarWhereInput | Prisma.PointOfSaleScalarWhereInput[];
};
export type PointOfSaleUncheckedUpdateManyWithoutBranchNestedInput = {
    create?: Prisma.XOR<Prisma.PointOfSaleCreateWithoutBranchInput, Prisma.PointOfSaleUncheckedCreateWithoutBranchInput> | Prisma.PointOfSaleCreateWithoutBranchInput[] | Prisma.PointOfSaleUncheckedCreateWithoutBranchInput[];
    connectOrCreate?: Prisma.PointOfSaleCreateOrConnectWithoutBranchInput | Prisma.PointOfSaleCreateOrConnectWithoutBranchInput[];
    upsert?: Prisma.PointOfSaleUpsertWithWhereUniqueWithoutBranchInput | Prisma.PointOfSaleUpsertWithWhereUniqueWithoutBranchInput[];
    createMany?: Prisma.PointOfSaleCreateManyBranchInputEnvelope;
    set?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    disconnect?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    delete?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    connect?: Prisma.PointOfSaleWhereUniqueInput | Prisma.PointOfSaleWhereUniqueInput[];
    update?: Prisma.PointOfSaleUpdateWithWhereUniqueWithoutBranchInput | Prisma.PointOfSaleUpdateWithWhereUniqueWithoutBranchInput[];
    updateMany?: Prisma.PointOfSaleUpdateManyWithWhereWithoutBranchInput | Prisma.PointOfSaleUpdateManyWithWhereWithoutBranchInput[];
    deleteMany?: Prisma.PointOfSaleScalarWhereInput | Prisma.PointOfSaleScalarWhereInput[];
};
export type PointOfSaleCreateWithoutBranchInput = {
    id?: string;
    code?: string | null;
    name: string;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type PointOfSaleUncheckedCreateWithoutBranchInput = {
    id?: string;
    code?: string | null;
    name: string;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type PointOfSaleCreateOrConnectWithoutBranchInput = {
    where: Prisma.PointOfSaleWhereUniqueInput;
    create: Prisma.XOR<Prisma.PointOfSaleCreateWithoutBranchInput, Prisma.PointOfSaleUncheckedCreateWithoutBranchInput>;
};
export type PointOfSaleCreateManyBranchInputEnvelope = {
    data: Prisma.PointOfSaleCreateManyBranchInput | Prisma.PointOfSaleCreateManyBranchInput[];
    skipDuplicates?: boolean;
};
export type PointOfSaleUpsertWithWhereUniqueWithoutBranchInput = {
    where: Prisma.PointOfSaleWhereUniqueInput;
    update: Prisma.XOR<Prisma.PointOfSaleUpdateWithoutBranchInput, Prisma.PointOfSaleUncheckedUpdateWithoutBranchInput>;
    create: Prisma.XOR<Prisma.PointOfSaleCreateWithoutBranchInput, Prisma.PointOfSaleUncheckedCreateWithoutBranchInput>;
};
export type PointOfSaleUpdateWithWhereUniqueWithoutBranchInput = {
    where: Prisma.PointOfSaleWhereUniqueInput;
    data: Prisma.XOR<Prisma.PointOfSaleUpdateWithoutBranchInput, Prisma.PointOfSaleUncheckedUpdateWithoutBranchInput>;
};
export type PointOfSaleUpdateManyWithWhereWithoutBranchInput = {
    where: Prisma.PointOfSaleScalarWhereInput;
    data: Prisma.XOR<Prisma.PointOfSaleUpdateManyMutationInput, Prisma.PointOfSaleUncheckedUpdateManyWithoutBranchInput>;
};
export type PointOfSaleScalarWhereInput = {
    AND?: Prisma.PointOfSaleScalarWhereInput | Prisma.PointOfSaleScalarWhereInput[];
    OR?: Prisma.PointOfSaleScalarWhereInput[];
    NOT?: Prisma.PointOfSaleScalarWhereInput | Prisma.PointOfSaleScalarWhereInput[];
    id?: Prisma.UuidFilter<"PointOfSale"> | string;
    branchId?: Prisma.UuidFilter<"PointOfSale"> | string;
    code?: Prisma.StringNullableFilter<"PointOfSale"> | string | null;
    name?: Prisma.StringFilter<"PointOfSale"> | string;
    active?: Prisma.BoolFilter<"PointOfSale"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"PointOfSale"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"PointOfSale"> | Date | string;
    createdBy?: Prisma.StringFilter<"PointOfSale"> | string;
    updatedBy?: Prisma.StringNullableFilter<"PointOfSale"> | string | null;
};
export type PointOfSaleCreateManyBranchInput = {
    id?: string;
    code?: string | null;
    name: string;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type PointOfSaleUpdateWithoutBranchInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type PointOfSaleUncheckedUpdateWithoutBranchInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type PointOfSaleUncheckedUpdateManyWithoutBranchInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    code?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type PointOfSaleSelect<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    branchId?: boolean;
    code?: boolean;
    name?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    branch?: boolean | Prisma.BranchDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["pointOfSale"]>;
export type PointOfSaleSelectCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    branchId?: boolean;
    code?: boolean;
    name?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    branch?: boolean | Prisma.BranchDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["pointOfSale"]>;
export type PointOfSaleSelectUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    branchId?: boolean;
    code?: boolean;
    name?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    branch?: boolean | Prisma.BranchDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["pointOfSale"]>;
export type PointOfSaleSelectScalar = {
    id?: boolean;
    branchId?: boolean;
    code?: boolean;
    name?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
};
export type PointOfSaleOmit<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetOmit<"id" | "branchId" | "code" | "name" | "active" | "createdAt" | "updatedAt" | "createdBy" | "updatedBy", ExtArgs["result"]["pointOfSale"]>;
export type PointOfSaleInclude<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    branch?: boolean | Prisma.BranchDefaultArgs<ExtArgs>;
};
export type PointOfSaleIncludeCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    branch?: boolean | Prisma.BranchDefaultArgs<ExtArgs>;
};
export type PointOfSaleIncludeUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    branch?: boolean | Prisma.BranchDefaultArgs<ExtArgs>;
};
export type $PointOfSalePayload<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    name: "PointOfSale";
    objects: {
        branch: Prisma.$BranchPayload<ExtArgs>;
    };
    scalars: runtime.Types.Extensions.GetPayloadResult<{
        id: string;
        branchId: string;
        code: string | null;
        name: string;
        active: boolean;
        createdAt: Date;
        updatedAt: Date;
        createdBy: string;
        updatedBy: string | null;
    }, ExtArgs["result"]["pointOfSale"]>;
    composites: {};
};
export type PointOfSaleGetPayload<S extends boolean | null | undefined | PointOfSaleDefaultArgs> = runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload, S>;
export type PointOfSaleCountArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = Omit<PointOfSaleFindManyArgs, 'select' | 'include' | 'distinct' | 'omit'> & {
    select?: PointOfSaleCountAggregateInputType | true;
};
export interface PointOfSaleDelegate<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> {
    [K: symbol]: {
        types: Prisma.TypeMap<ExtArgs>['model']['PointOfSale'];
        meta: {
            name: 'PointOfSale';
        };
    };
    findUnique<T extends PointOfSaleFindUniqueArgs>(args: Prisma.SelectSubset<T, PointOfSaleFindUniqueArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "findUnique", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findUniqueOrThrow<T extends PointOfSaleFindUniqueOrThrowArgs>(args: Prisma.SelectSubset<T, PointOfSaleFindUniqueOrThrowArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "findUniqueOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findFirst<T extends PointOfSaleFindFirstArgs>(args?: Prisma.SelectSubset<T, PointOfSaleFindFirstArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "findFirst", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findFirstOrThrow<T extends PointOfSaleFindFirstOrThrowArgs>(args?: Prisma.SelectSubset<T, PointOfSaleFindFirstOrThrowArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "findFirstOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findMany<T extends PointOfSaleFindManyArgs>(args?: Prisma.SelectSubset<T, PointOfSaleFindManyArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "findMany", GlobalOmitOptions>>;
    create<T extends PointOfSaleCreateArgs>(args: Prisma.SelectSubset<T, PointOfSaleCreateArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "create", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    createMany<T extends PointOfSaleCreateManyArgs>(args?: Prisma.SelectSubset<T, PointOfSaleCreateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    createManyAndReturn<T extends PointOfSaleCreateManyAndReturnArgs>(args?: Prisma.SelectSubset<T, PointOfSaleCreateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "createManyAndReturn", GlobalOmitOptions>>;
    delete<T extends PointOfSaleDeleteArgs>(args: Prisma.SelectSubset<T, PointOfSaleDeleteArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "delete", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    update<T extends PointOfSaleUpdateArgs>(args: Prisma.SelectSubset<T, PointOfSaleUpdateArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "update", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    deleteMany<T extends PointOfSaleDeleteManyArgs>(args?: Prisma.SelectSubset<T, PointOfSaleDeleteManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateMany<T extends PointOfSaleUpdateManyArgs>(args: Prisma.SelectSubset<T, PointOfSaleUpdateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateManyAndReturn<T extends PointOfSaleUpdateManyAndReturnArgs>(args: Prisma.SelectSubset<T, PointOfSaleUpdateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "updateManyAndReturn", GlobalOmitOptions>>;
    upsert<T extends PointOfSaleUpsertArgs>(args: Prisma.SelectSubset<T, PointOfSaleUpsertArgs<ExtArgs>>): Prisma.Prisma__PointOfSaleClient<runtime.Types.Result.GetResult<Prisma.$PointOfSalePayload<ExtArgs>, T, "upsert", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    count<T extends PointOfSaleCountArgs>(args?: Prisma.Subset<T, PointOfSaleCountArgs>): Prisma.PrismaPromise<T extends runtime.Types.Utils.Record<'select', any> ? T['select'] extends true ? number : Prisma.GetScalarType<T['select'], PointOfSaleCountAggregateOutputType> : number>;
    aggregate<T extends PointOfSaleAggregateArgs>(args: Prisma.Subset<T, PointOfSaleAggregateArgs>): Prisma.PrismaPromise<GetPointOfSaleAggregateType<T>>;
    groupBy<T extends PointOfSaleGroupByArgs, HasSelectOrTake extends Prisma.Or<Prisma.Extends<'skip', Prisma.Keys<T>>, Prisma.Extends<'take', Prisma.Keys<T>>>, OrderByArg extends Prisma.True extends HasSelectOrTake ? {
        orderBy: PointOfSaleGroupByArgs['orderBy'];
    } : {
        orderBy?: PointOfSaleGroupByArgs['orderBy'];
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
    }[OrderFields]>(args: Prisma.SubsetIntersection<T, PointOfSaleGroupByArgs, OrderByArg> & InputErrors): {} extends InputErrors ? GetPointOfSaleGroupByPayload<T> : Prisma.PrismaPromise<InputErrors>;
    readonly fields: PointOfSaleFieldRefs;
}
export interface Prisma__PointOfSaleClient<T, Null = never, ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> extends Prisma.PrismaPromise<T> {
    readonly [Symbol.toStringTag]: "PrismaPromise";
    branch<T extends Prisma.BranchDefaultArgs<ExtArgs> = {}>(args?: Prisma.Subset<T, Prisma.BranchDefaultArgs<ExtArgs>>): Prisma.Prisma__BranchClient<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "findUniqueOrThrow", GlobalOmitOptions> | Null, Null, ExtArgs, GlobalOmitOptions>;
    then<TResult1 = T, TResult2 = never>(onfulfilled?: ((value: T) => TResult1 | PromiseLike<TResult1>) | undefined | null, onrejected?: ((reason: any) => TResult2 | PromiseLike<TResult2>) | undefined | null): runtime.Types.Utils.JsPromise<TResult1 | TResult2>;
    catch<TResult = never>(onrejected?: ((reason: any) => TResult | PromiseLike<TResult>) | undefined | null): runtime.Types.Utils.JsPromise<T | TResult>;
    finally(onfinally?: (() => void) | undefined | null): runtime.Types.Utils.JsPromise<T>;
}
export interface PointOfSaleFieldRefs {
    readonly id: Prisma.FieldRef<"PointOfSale", 'String'>;
    readonly branchId: Prisma.FieldRef<"PointOfSale", 'String'>;
    readonly code: Prisma.FieldRef<"PointOfSale", 'String'>;
    readonly name: Prisma.FieldRef<"PointOfSale", 'String'>;
    readonly active: Prisma.FieldRef<"PointOfSale", 'Boolean'>;
    readonly createdAt: Prisma.FieldRef<"PointOfSale", 'DateTime'>;
    readonly updatedAt: Prisma.FieldRef<"PointOfSale", 'DateTime'>;
    readonly createdBy: Prisma.FieldRef<"PointOfSale", 'String'>;
    readonly updatedBy: Prisma.FieldRef<"PointOfSale", 'String'>;
}
export type PointOfSaleFindUniqueArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
    where: Prisma.PointOfSaleWhereUniqueInput;
};
export type PointOfSaleFindUniqueOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
    where: Prisma.PointOfSaleWhereUniqueInput;
};
export type PointOfSaleFindFirstArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
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
export type PointOfSaleFindFirstOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
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
export type PointOfSaleFindManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
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
export type PointOfSaleCreateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.PointOfSaleCreateInput, Prisma.PointOfSaleUncheckedCreateInput>;
};
export type PointOfSaleCreateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.PointOfSaleCreateManyInput | Prisma.PointOfSaleCreateManyInput[];
    skipDuplicates?: boolean;
};
export type PointOfSaleCreateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelectCreateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    data: Prisma.PointOfSaleCreateManyInput | Prisma.PointOfSaleCreateManyInput[];
    skipDuplicates?: boolean;
    include?: Prisma.PointOfSaleIncludeCreateManyAndReturn<ExtArgs> | null;
};
export type PointOfSaleUpdateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.PointOfSaleUpdateInput, Prisma.PointOfSaleUncheckedUpdateInput>;
    where: Prisma.PointOfSaleWhereUniqueInput;
};
export type PointOfSaleUpdateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.XOR<Prisma.PointOfSaleUpdateManyMutationInput, Prisma.PointOfSaleUncheckedUpdateManyInput>;
    where?: Prisma.PointOfSaleWhereInput;
    limit?: number;
};
export type PointOfSaleUpdateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelectUpdateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    data: Prisma.XOR<Prisma.PointOfSaleUpdateManyMutationInput, Prisma.PointOfSaleUncheckedUpdateManyInput>;
    where?: Prisma.PointOfSaleWhereInput;
    limit?: number;
    include?: Prisma.PointOfSaleIncludeUpdateManyAndReturn<ExtArgs> | null;
};
export type PointOfSaleUpsertArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
    where: Prisma.PointOfSaleWhereUniqueInput;
    create: Prisma.XOR<Prisma.PointOfSaleCreateInput, Prisma.PointOfSaleUncheckedCreateInput>;
    update: Prisma.XOR<Prisma.PointOfSaleUpdateInput, Prisma.PointOfSaleUncheckedUpdateInput>;
};
export type PointOfSaleDeleteArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
    where: Prisma.PointOfSaleWhereUniqueInput;
};
export type PointOfSaleDeleteManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.PointOfSaleWhereInput;
    limit?: number;
};
export type PointOfSaleDefaultArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.PointOfSaleSelect<ExtArgs> | null;
    omit?: Prisma.PointOfSaleOmit<ExtArgs> | null;
    include?: Prisma.PointOfSaleInclude<ExtArgs> | null;
};
export {};
