import type * as runtime from "@prisma/client/runtime/client";
import type * as Prisma from "../internal/prismaNamespace";
export type SystemAssignmentModel = runtime.Types.Result.DefaultSelection<Prisma.$SystemAssignmentPayload>;
export type AggregateSystemAssignment = {
    _count: SystemAssignmentCountAggregateOutputType | null;
    _min: SystemAssignmentMinAggregateOutputType | null;
    _max: SystemAssignmentMaxAggregateOutputType | null;
};
export type SystemAssignmentMinAggregateOutputType = {
    id: string | null;
    businessId: string | null;
    systemId: string | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type SystemAssignmentMaxAggregateOutputType = {
    id: string | null;
    businessId: string | null;
    systemId: string | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type SystemAssignmentCountAggregateOutputType = {
    id: number;
    businessId: number;
    systemId: number;
    createdAt: number;
    updatedAt: number;
    createdBy: number;
    updatedBy: number;
    _all: number;
};
export type SystemAssignmentMinAggregateInputType = {
    id?: true;
    businessId?: true;
    systemId?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type SystemAssignmentMaxAggregateInputType = {
    id?: true;
    businessId?: true;
    systemId?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type SystemAssignmentCountAggregateInputType = {
    id?: true;
    businessId?: true;
    systemId?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
    _all?: true;
};
export type SystemAssignmentAggregateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.SystemAssignmentWhereInput;
    orderBy?: Prisma.SystemAssignmentOrderByWithRelationInput | Prisma.SystemAssignmentOrderByWithRelationInput[];
    cursor?: Prisma.SystemAssignmentWhereUniqueInput;
    take?: number;
    skip?: number;
    _count?: true | SystemAssignmentCountAggregateInputType;
    _min?: SystemAssignmentMinAggregateInputType;
    _max?: SystemAssignmentMaxAggregateInputType;
};
export type GetSystemAssignmentAggregateType<T extends SystemAssignmentAggregateArgs> = {
    [P in keyof T & keyof AggregateSystemAssignment]: P extends '_count' | 'count' ? T[P] extends true ? number : Prisma.GetScalarType<T[P], AggregateSystemAssignment[P]> : Prisma.GetScalarType<T[P], AggregateSystemAssignment[P]>;
};
export type SystemAssignmentGroupByArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.SystemAssignmentWhereInput;
    orderBy?: Prisma.SystemAssignmentOrderByWithAggregationInput | Prisma.SystemAssignmentOrderByWithAggregationInput[];
    by: Prisma.SystemAssignmentScalarFieldEnum[] | Prisma.SystemAssignmentScalarFieldEnum;
    having?: Prisma.SystemAssignmentScalarWhereWithAggregatesInput;
    take?: number;
    skip?: number;
    _count?: SystemAssignmentCountAggregateInputType | true;
    _min?: SystemAssignmentMinAggregateInputType;
    _max?: SystemAssignmentMaxAggregateInputType;
};
export type SystemAssignmentGroupByOutputType = {
    id: string;
    businessId: string;
    systemId: string;
    createdAt: Date;
    updatedAt: Date;
    createdBy: string;
    updatedBy: string | null;
    _count: SystemAssignmentCountAggregateOutputType | null;
    _min: SystemAssignmentMinAggregateOutputType | null;
    _max: SystemAssignmentMaxAggregateOutputType | null;
};
type GetSystemAssignmentGroupByPayload<T extends SystemAssignmentGroupByArgs> = Prisma.PrismaPromise<Array<Prisma.PickEnumerable<SystemAssignmentGroupByOutputType, T['by']> & {
    [P in ((keyof T) & (keyof SystemAssignmentGroupByOutputType))]: P extends '_count' ? T[P] extends boolean ? number : Prisma.GetScalarType<T[P], SystemAssignmentGroupByOutputType[P]> : Prisma.GetScalarType<T[P], SystemAssignmentGroupByOutputType[P]>;
}>>;
export type SystemAssignmentWhereInput = {
    AND?: Prisma.SystemAssignmentWhereInput | Prisma.SystemAssignmentWhereInput[];
    OR?: Prisma.SystemAssignmentWhereInput[];
    NOT?: Prisma.SystemAssignmentWhereInput | Prisma.SystemAssignmentWhereInput[];
    id?: Prisma.UuidFilter<"SystemAssignment"> | string;
    businessId?: Prisma.UuidFilter<"SystemAssignment"> | string;
    systemId?: Prisma.UuidFilter<"SystemAssignment"> | string;
    createdAt?: Prisma.DateTimeFilter<"SystemAssignment"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"SystemAssignment"> | Date | string;
    createdBy?: Prisma.StringFilter<"SystemAssignment"> | string;
    updatedBy?: Prisma.StringNullableFilter<"SystemAssignment"> | string | null;
    business?: Prisma.XOR<Prisma.BusinessScalarRelationFilter, Prisma.BusinessWhereInput>;
};
export type SystemAssignmentOrderByWithRelationInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    systemId?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    business?: Prisma.BusinessOrderByWithRelationInput;
};
export type SystemAssignmentWhereUniqueInput = Prisma.AtLeast<{
    id?: string;
    businessId_systemId?: Prisma.SystemAssignmentBusinessIdSystemIdCompoundUniqueInput;
    AND?: Prisma.SystemAssignmentWhereInput | Prisma.SystemAssignmentWhereInput[];
    OR?: Prisma.SystemAssignmentWhereInput[];
    NOT?: Prisma.SystemAssignmentWhereInput | Prisma.SystemAssignmentWhereInput[];
    businessId?: Prisma.UuidFilter<"SystemAssignment"> | string;
    systemId?: Prisma.UuidFilter<"SystemAssignment"> | string;
    createdAt?: Prisma.DateTimeFilter<"SystemAssignment"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"SystemAssignment"> | Date | string;
    createdBy?: Prisma.StringFilter<"SystemAssignment"> | string;
    updatedBy?: Prisma.StringNullableFilter<"SystemAssignment"> | string | null;
    business?: Prisma.XOR<Prisma.BusinessScalarRelationFilter, Prisma.BusinessWhereInput>;
}, "id" | "businessId_systemId">;
export type SystemAssignmentOrderByWithAggregationInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    systemId?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    _count?: Prisma.SystemAssignmentCountOrderByAggregateInput;
    _max?: Prisma.SystemAssignmentMaxOrderByAggregateInput;
    _min?: Prisma.SystemAssignmentMinOrderByAggregateInput;
};
export type SystemAssignmentScalarWhereWithAggregatesInput = {
    AND?: Prisma.SystemAssignmentScalarWhereWithAggregatesInput | Prisma.SystemAssignmentScalarWhereWithAggregatesInput[];
    OR?: Prisma.SystemAssignmentScalarWhereWithAggregatesInput[];
    NOT?: Prisma.SystemAssignmentScalarWhereWithAggregatesInput | Prisma.SystemAssignmentScalarWhereWithAggregatesInput[];
    id?: Prisma.UuidWithAggregatesFilter<"SystemAssignment"> | string;
    businessId?: Prisma.UuidWithAggregatesFilter<"SystemAssignment"> | string;
    systemId?: Prisma.UuidWithAggregatesFilter<"SystemAssignment"> | string;
    createdAt?: Prisma.DateTimeWithAggregatesFilter<"SystemAssignment"> | Date | string;
    updatedAt?: Prisma.DateTimeWithAggregatesFilter<"SystemAssignment"> | Date | string;
    createdBy?: Prisma.StringWithAggregatesFilter<"SystemAssignment"> | string;
    updatedBy?: Prisma.StringNullableWithAggregatesFilter<"SystemAssignment"> | string | null;
};
export type SystemAssignmentCreateInput = {
    id?: string;
    systemId: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    business: Prisma.BusinessCreateNestedOneWithoutSystemAssignmentsInput;
};
export type SystemAssignmentUncheckedCreateInput = {
    id?: string;
    businessId: string;
    systemId: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type SystemAssignmentUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    systemId?: Prisma.StringFieldUpdateOperationsInput | string;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    business?: Prisma.BusinessUpdateOneRequiredWithoutSystemAssignmentsNestedInput;
};
export type SystemAssignmentUncheckedUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    businessId?: Prisma.StringFieldUpdateOperationsInput | string;
    systemId?: Prisma.StringFieldUpdateOperationsInput | string;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type SystemAssignmentCreateManyInput = {
    id?: string;
    businessId: string;
    systemId: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type SystemAssignmentUpdateManyMutationInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    systemId?: Prisma.StringFieldUpdateOperationsInput | string;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type SystemAssignmentUncheckedUpdateManyInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    businessId?: Prisma.StringFieldUpdateOperationsInput | string;
    systemId?: Prisma.StringFieldUpdateOperationsInput | string;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type SystemAssignmentBusinessIdSystemIdCompoundUniqueInput = {
    businessId: string;
    systemId: string;
};
export type SystemAssignmentCountOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    systemId?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type SystemAssignmentMaxOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    systemId?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type SystemAssignmentMinOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    businessId?: Prisma.SortOrder;
    systemId?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type SystemAssignmentListRelationFilter = {
    every?: Prisma.SystemAssignmentWhereInput;
    some?: Prisma.SystemAssignmentWhereInput;
    none?: Prisma.SystemAssignmentWhereInput;
};
export type SystemAssignmentOrderByRelationAggregateInput = {
    _count?: Prisma.SortOrder;
};
export type StringFieldUpdateOperationsInput = {
    set?: string;
};
export type DateTimeFieldUpdateOperationsInput = {
    set?: Date | string;
};
export type NullableStringFieldUpdateOperationsInput = {
    set?: string | null;
};
export type SystemAssignmentCreateNestedManyWithoutBusinessInput = {
    create?: Prisma.XOR<Prisma.SystemAssignmentCreateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput> | Prisma.SystemAssignmentCreateWithoutBusinessInput[] | Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput | Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput[];
    createMany?: Prisma.SystemAssignmentCreateManyBusinessInputEnvelope;
    connect?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
};
export type SystemAssignmentUncheckedCreateNestedManyWithoutBusinessInput = {
    create?: Prisma.XOR<Prisma.SystemAssignmentCreateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput> | Prisma.SystemAssignmentCreateWithoutBusinessInput[] | Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput | Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput[];
    createMany?: Prisma.SystemAssignmentCreateManyBusinessInputEnvelope;
    connect?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
};
export type SystemAssignmentUpdateManyWithoutBusinessNestedInput = {
    create?: Prisma.XOR<Prisma.SystemAssignmentCreateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput> | Prisma.SystemAssignmentCreateWithoutBusinessInput[] | Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput | Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput[];
    upsert?: Prisma.SystemAssignmentUpsertWithWhereUniqueWithoutBusinessInput | Prisma.SystemAssignmentUpsertWithWhereUniqueWithoutBusinessInput[];
    createMany?: Prisma.SystemAssignmentCreateManyBusinessInputEnvelope;
    set?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    disconnect?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    delete?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    connect?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    update?: Prisma.SystemAssignmentUpdateWithWhereUniqueWithoutBusinessInput | Prisma.SystemAssignmentUpdateWithWhereUniqueWithoutBusinessInput[];
    updateMany?: Prisma.SystemAssignmentUpdateManyWithWhereWithoutBusinessInput | Prisma.SystemAssignmentUpdateManyWithWhereWithoutBusinessInput[];
    deleteMany?: Prisma.SystemAssignmentScalarWhereInput | Prisma.SystemAssignmentScalarWhereInput[];
};
export type SystemAssignmentUncheckedUpdateManyWithoutBusinessNestedInput = {
    create?: Prisma.XOR<Prisma.SystemAssignmentCreateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput> | Prisma.SystemAssignmentCreateWithoutBusinessInput[] | Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput[];
    connectOrCreate?: Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput | Prisma.SystemAssignmentCreateOrConnectWithoutBusinessInput[];
    upsert?: Prisma.SystemAssignmentUpsertWithWhereUniqueWithoutBusinessInput | Prisma.SystemAssignmentUpsertWithWhereUniqueWithoutBusinessInput[];
    createMany?: Prisma.SystemAssignmentCreateManyBusinessInputEnvelope;
    set?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    disconnect?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    delete?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    connect?: Prisma.SystemAssignmentWhereUniqueInput | Prisma.SystemAssignmentWhereUniqueInput[];
    update?: Prisma.SystemAssignmentUpdateWithWhereUniqueWithoutBusinessInput | Prisma.SystemAssignmentUpdateWithWhereUniqueWithoutBusinessInput[];
    updateMany?: Prisma.SystemAssignmentUpdateManyWithWhereWithoutBusinessInput | Prisma.SystemAssignmentUpdateManyWithWhereWithoutBusinessInput[];
    deleteMany?: Prisma.SystemAssignmentScalarWhereInput | Prisma.SystemAssignmentScalarWhereInput[];
};
export type SystemAssignmentCreateWithoutBusinessInput = {
    id?: string;
    systemId: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type SystemAssignmentUncheckedCreateWithoutBusinessInput = {
    id?: string;
    systemId: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type SystemAssignmentCreateOrConnectWithoutBusinessInput = {
    where: Prisma.SystemAssignmentWhereUniqueInput;
    create: Prisma.XOR<Prisma.SystemAssignmentCreateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput>;
};
export type SystemAssignmentCreateManyBusinessInputEnvelope = {
    data: Prisma.SystemAssignmentCreateManyBusinessInput | Prisma.SystemAssignmentCreateManyBusinessInput[];
    skipDuplicates?: boolean;
};
export type SystemAssignmentUpsertWithWhereUniqueWithoutBusinessInput = {
    where: Prisma.SystemAssignmentWhereUniqueInput;
    update: Prisma.XOR<Prisma.SystemAssignmentUpdateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedUpdateWithoutBusinessInput>;
    create: Prisma.XOR<Prisma.SystemAssignmentCreateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedCreateWithoutBusinessInput>;
};
export type SystemAssignmentUpdateWithWhereUniqueWithoutBusinessInput = {
    where: Prisma.SystemAssignmentWhereUniqueInput;
    data: Prisma.XOR<Prisma.SystemAssignmentUpdateWithoutBusinessInput, Prisma.SystemAssignmentUncheckedUpdateWithoutBusinessInput>;
};
export type SystemAssignmentUpdateManyWithWhereWithoutBusinessInput = {
    where: Prisma.SystemAssignmentScalarWhereInput;
    data: Prisma.XOR<Prisma.SystemAssignmentUpdateManyMutationInput, Prisma.SystemAssignmentUncheckedUpdateManyWithoutBusinessInput>;
};
export type SystemAssignmentScalarWhereInput = {
    AND?: Prisma.SystemAssignmentScalarWhereInput | Prisma.SystemAssignmentScalarWhereInput[];
    OR?: Prisma.SystemAssignmentScalarWhereInput[];
    NOT?: Prisma.SystemAssignmentScalarWhereInput | Prisma.SystemAssignmentScalarWhereInput[];
    id?: Prisma.UuidFilter<"SystemAssignment"> | string;
    businessId?: Prisma.UuidFilter<"SystemAssignment"> | string;
    systemId?: Prisma.UuidFilter<"SystemAssignment"> | string;
    createdAt?: Prisma.DateTimeFilter<"SystemAssignment"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"SystemAssignment"> | Date | string;
    createdBy?: Prisma.StringFilter<"SystemAssignment"> | string;
    updatedBy?: Prisma.StringNullableFilter<"SystemAssignment"> | string | null;
};
export type SystemAssignmentCreateManyBusinessInput = {
    id?: string;
    systemId: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type SystemAssignmentUpdateWithoutBusinessInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    systemId?: Prisma.StringFieldUpdateOperationsInput | string;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type SystemAssignmentUncheckedUpdateWithoutBusinessInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    systemId?: Prisma.StringFieldUpdateOperationsInput | string;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type SystemAssignmentUncheckedUpdateManyWithoutBusinessInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    systemId?: Prisma.StringFieldUpdateOperationsInput | string;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type SystemAssignmentSelect<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    businessId?: boolean;
    systemId?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["systemAssignment"]>;
export type SystemAssignmentSelectCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    businessId?: boolean;
    systemId?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["systemAssignment"]>;
export type SystemAssignmentSelectUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    businessId?: boolean;
    systemId?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["systemAssignment"]>;
export type SystemAssignmentSelectScalar = {
    id?: boolean;
    businessId?: boolean;
    systemId?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
};
export type SystemAssignmentOmit<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetOmit<"id" | "businessId" | "systemId" | "createdAt" | "updatedAt" | "createdBy" | "updatedBy", ExtArgs["result"]["systemAssignment"]>;
export type SystemAssignmentInclude<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
};
export type SystemAssignmentIncludeCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
};
export type SystemAssignmentIncludeUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    business?: boolean | Prisma.BusinessDefaultArgs<ExtArgs>;
};
export type $SystemAssignmentPayload<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    name: "SystemAssignment";
    objects: {
        business: Prisma.$BusinessPayload<ExtArgs>;
    };
    scalars: runtime.Types.Extensions.GetPayloadResult<{
        id: string;
        businessId: string;
        systemId: string;
        createdAt: Date;
        updatedAt: Date;
        createdBy: string;
        updatedBy: string | null;
    }, ExtArgs["result"]["systemAssignment"]>;
    composites: {};
};
export type SystemAssignmentGetPayload<S extends boolean | null | undefined | SystemAssignmentDefaultArgs> = runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload, S>;
export type SystemAssignmentCountArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = Omit<SystemAssignmentFindManyArgs, 'select' | 'include' | 'distinct' | 'omit'> & {
    select?: SystemAssignmentCountAggregateInputType | true;
};
export interface SystemAssignmentDelegate<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> {
    [K: symbol]: {
        types: Prisma.TypeMap<ExtArgs>['model']['SystemAssignment'];
        meta: {
            name: 'SystemAssignment';
        };
    };
    findUnique<T extends SystemAssignmentFindUniqueArgs>(args: Prisma.SelectSubset<T, SystemAssignmentFindUniqueArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "findUnique", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findUniqueOrThrow<T extends SystemAssignmentFindUniqueOrThrowArgs>(args: Prisma.SelectSubset<T, SystemAssignmentFindUniqueOrThrowArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "findUniqueOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findFirst<T extends SystemAssignmentFindFirstArgs>(args?: Prisma.SelectSubset<T, SystemAssignmentFindFirstArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "findFirst", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findFirstOrThrow<T extends SystemAssignmentFindFirstOrThrowArgs>(args?: Prisma.SelectSubset<T, SystemAssignmentFindFirstOrThrowArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "findFirstOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findMany<T extends SystemAssignmentFindManyArgs>(args?: Prisma.SelectSubset<T, SystemAssignmentFindManyArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "findMany", GlobalOmitOptions>>;
    create<T extends SystemAssignmentCreateArgs>(args: Prisma.SelectSubset<T, SystemAssignmentCreateArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "create", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    createMany<T extends SystemAssignmentCreateManyArgs>(args?: Prisma.SelectSubset<T, SystemAssignmentCreateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    createManyAndReturn<T extends SystemAssignmentCreateManyAndReturnArgs>(args?: Prisma.SelectSubset<T, SystemAssignmentCreateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "createManyAndReturn", GlobalOmitOptions>>;
    delete<T extends SystemAssignmentDeleteArgs>(args: Prisma.SelectSubset<T, SystemAssignmentDeleteArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "delete", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    update<T extends SystemAssignmentUpdateArgs>(args: Prisma.SelectSubset<T, SystemAssignmentUpdateArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "update", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    deleteMany<T extends SystemAssignmentDeleteManyArgs>(args?: Prisma.SelectSubset<T, SystemAssignmentDeleteManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateMany<T extends SystemAssignmentUpdateManyArgs>(args: Prisma.SelectSubset<T, SystemAssignmentUpdateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateManyAndReturn<T extends SystemAssignmentUpdateManyAndReturnArgs>(args: Prisma.SelectSubset<T, SystemAssignmentUpdateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "updateManyAndReturn", GlobalOmitOptions>>;
    upsert<T extends SystemAssignmentUpsertArgs>(args: Prisma.SelectSubset<T, SystemAssignmentUpsertArgs<ExtArgs>>): Prisma.Prisma__SystemAssignmentClient<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "upsert", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    count<T extends SystemAssignmentCountArgs>(args?: Prisma.Subset<T, SystemAssignmentCountArgs>): Prisma.PrismaPromise<T extends runtime.Types.Utils.Record<'select', any> ? T['select'] extends true ? number : Prisma.GetScalarType<T['select'], SystemAssignmentCountAggregateOutputType> : number>;
    aggregate<T extends SystemAssignmentAggregateArgs>(args: Prisma.Subset<T, SystemAssignmentAggregateArgs>): Prisma.PrismaPromise<GetSystemAssignmentAggregateType<T>>;
    groupBy<T extends SystemAssignmentGroupByArgs, HasSelectOrTake extends Prisma.Or<Prisma.Extends<'skip', Prisma.Keys<T>>, Prisma.Extends<'take', Prisma.Keys<T>>>, OrderByArg extends Prisma.True extends HasSelectOrTake ? {
        orderBy: SystemAssignmentGroupByArgs['orderBy'];
    } : {
        orderBy?: SystemAssignmentGroupByArgs['orderBy'];
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
    }[OrderFields]>(args: Prisma.SubsetIntersection<T, SystemAssignmentGroupByArgs, OrderByArg> & InputErrors): {} extends InputErrors ? GetSystemAssignmentGroupByPayload<T> : Prisma.PrismaPromise<InputErrors>;
    readonly fields: SystemAssignmentFieldRefs;
}
export interface Prisma__SystemAssignmentClient<T, Null = never, ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> extends Prisma.PrismaPromise<T> {
    readonly [Symbol.toStringTag]: "PrismaPromise";
    business<T extends Prisma.BusinessDefaultArgs<ExtArgs> = {}>(args?: Prisma.Subset<T, Prisma.BusinessDefaultArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "findUniqueOrThrow", GlobalOmitOptions> | Null, Null, ExtArgs, GlobalOmitOptions>;
    then<TResult1 = T, TResult2 = never>(onfulfilled?: ((value: T) => TResult1 | PromiseLike<TResult1>) | undefined | null, onrejected?: ((reason: any) => TResult2 | PromiseLike<TResult2>) | undefined | null): runtime.Types.Utils.JsPromise<TResult1 | TResult2>;
    catch<TResult = never>(onrejected?: ((reason: any) => TResult | PromiseLike<TResult>) | undefined | null): runtime.Types.Utils.JsPromise<T | TResult>;
    finally(onfinally?: (() => void) | undefined | null): runtime.Types.Utils.JsPromise<T>;
}
export interface SystemAssignmentFieldRefs {
    readonly id: Prisma.FieldRef<"SystemAssignment", 'String'>;
    readonly businessId: Prisma.FieldRef<"SystemAssignment", 'String'>;
    readonly systemId: Prisma.FieldRef<"SystemAssignment", 'String'>;
    readonly createdAt: Prisma.FieldRef<"SystemAssignment", 'DateTime'>;
    readonly updatedAt: Prisma.FieldRef<"SystemAssignment", 'DateTime'>;
    readonly createdBy: Prisma.FieldRef<"SystemAssignment", 'String'>;
    readonly updatedBy: Prisma.FieldRef<"SystemAssignment", 'String'>;
}
export type SystemAssignmentFindUniqueArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    where: Prisma.SystemAssignmentWhereUniqueInput;
};
export type SystemAssignmentFindUniqueOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    where: Prisma.SystemAssignmentWhereUniqueInput;
};
export type SystemAssignmentFindFirstArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    where?: Prisma.SystemAssignmentWhereInput;
    orderBy?: Prisma.SystemAssignmentOrderByWithRelationInput | Prisma.SystemAssignmentOrderByWithRelationInput[];
    cursor?: Prisma.SystemAssignmentWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.SystemAssignmentScalarFieldEnum | Prisma.SystemAssignmentScalarFieldEnum[];
};
export type SystemAssignmentFindFirstOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    where?: Prisma.SystemAssignmentWhereInput;
    orderBy?: Prisma.SystemAssignmentOrderByWithRelationInput | Prisma.SystemAssignmentOrderByWithRelationInput[];
    cursor?: Prisma.SystemAssignmentWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.SystemAssignmentScalarFieldEnum | Prisma.SystemAssignmentScalarFieldEnum[];
};
export type SystemAssignmentFindManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    where?: Prisma.SystemAssignmentWhereInput;
    orderBy?: Prisma.SystemAssignmentOrderByWithRelationInput | Prisma.SystemAssignmentOrderByWithRelationInput[];
    cursor?: Prisma.SystemAssignmentWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.SystemAssignmentScalarFieldEnum | Prisma.SystemAssignmentScalarFieldEnum[];
};
export type SystemAssignmentCreateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.SystemAssignmentCreateInput, Prisma.SystemAssignmentUncheckedCreateInput>;
};
export type SystemAssignmentCreateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.SystemAssignmentCreateManyInput | Prisma.SystemAssignmentCreateManyInput[];
    skipDuplicates?: boolean;
};
export type SystemAssignmentCreateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelectCreateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    data: Prisma.SystemAssignmentCreateManyInput | Prisma.SystemAssignmentCreateManyInput[];
    skipDuplicates?: boolean;
    include?: Prisma.SystemAssignmentIncludeCreateManyAndReturn<ExtArgs> | null;
};
export type SystemAssignmentUpdateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.SystemAssignmentUpdateInput, Prisma.SystemAssignmentUncheckedUpdateInput>;
    where: Prisma.SystemAssignmentWhereUniqueInput;
};
export type SystemAssignmentUpdateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.XOR<Prisma.SystemAssignmentUpdateManyMutationInput, Prisma.SystemAssignmentUncheckedUpdateManyInput>;
    where?: Prisma.SystemAssignmentWhereInput;
    limit?: number;
};
export type SystemAssignmentUpdateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelectUpdateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    data: Prisma.XOR<Prisma.SystemAssignmentUpdateManyMutationInput, Prisma.SystemAssignmentUncheckedUpdateManyInput>;
    where?: Prisma.SystemAssignmentWhereInput;
    limit?: number;
    include?: Prisma.SystemAssignmentIncludeUpdateManyAndReturn<ExtArgs> | null;
};
export type SystemAssignmentUpsertArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    where: Prisma.SystemAssignmentWhereUniqueInput;
    create: Prisma.XOR<Prisma.SystemAssignmentCreateInput, Prisma.SystemAssignmentUncheckedCreateInput>;
    update: Prisma.XOR<Prisma.SystemAssignmentUpdateInput, Prisma.SystemAssignmentUncheckedUpdateInput>;
};
export type SystemAssignmentDeleteArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
    where: Prisma.SystemAssignmentWhereUniqueInput;
};
export type SystemAssignmentDeleteManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.SystemAssignmentWhereInput;
    limit?: number;
};
export type SystemAssignmentDefaultArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.SystemAssignmentSelect<ExtArgs> | null;
    omit?: Prisma.SystemAssignmentOmit<ExtArgs> | null;
    include?: Prisma.SystemAssignmentInclude<ExtArgs> | null;
};
export {};
