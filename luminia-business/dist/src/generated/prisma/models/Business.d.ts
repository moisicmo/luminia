import type * as runtime from "@prisma/client/runtime/client";
import type * as $Enums from "../enums";
import type * as Prisma from "../internal/prismaNamespace";
export type BusinessModel = runtime.Types.Result.DefaultSelection<Prisma.$BusinessPayload>;
export type AggregateBusiness = {
    _count: BusinessCountAggregateOutputType | null;
    _min: BusinessMinAggregateOutputType | null;
    _max: BusinessMaxAggregateOutputType | null;
};
export type BusinessMinAggregateOutputType = {
    id: string | null;
    ownerId: string | null;
    name: string | null;
    businessType: $Enums.BusinessType | null;
    url: string | null;
    taxId: string | null;
    logo: string | null;
    active: boolean | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type BusinessMaxAggregateOutputType = {
    id: string | null;
    ownerId: string | null;
    name: string | null;
    businessType: $Enums.BusinessType | null;
    url: string | null;
    taxId: string | null;
    logo: string | null;
    active: boolean | null;
    createdAt: Date | null;
    updatedAt: Date | null;
    createdBy: string | null;
    updatedBy: string | null;
};
export type BusinessCountAggregateOutputType = {
    id: number;
    ownerId: number;
    name: number;
    businessType: number;
    url: number;
    taxId: number;
    logo: number;
    active: number;
    createdAt: number;
    updatedAt: number;
    createdBy: number;
    updatedBy: number;
    _all: number;
};
export type BusinessMinAggregateInputType = {
    id?: true;
    ownerId?: true;
    name?: true;
    businessType?: true;
    url?: true;
    taxId?: true;
    logo?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type BusinessMaxAggregateInputType = {
    id?: true;
    ownerId?: true;
    name?: true;
    businessType?: true;
    url?: true;
    taxId?: true;
    logo?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
};
export type BusinessCountAggregateInputType = {
    id?: true;
    ownerId?: true;
    name?: true;
    businessType?: true;
    url?: true;
    taxId?: true;
    logo?: true;
    active?: true;
    createdAt?: true;
    updatedAt?: true;
    createdBy?: true;
    updatedBy?: true;
    _all?: true;
};
export type BusinessAggregateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BusinessWhereInput;
    orderBy?: Prisma.BusinessOrderByWithRelationInput | Prisma.BusinessOrderByWithRelationInput[];
    cursor?: Prisma.BusinessWhereUniqueInput;
    take?: number;
    skip?: number;
    _count?: true | BusinessCountAggregateInputType;
    _min?: BusinessMinAggregateInputType;
    _max?: BusinessMaxAggregateInputType;
};
export type GetBusinessAggregateType<T extends BusinessAggregateArgs> = {
    [P in keyof T & keyof AggregateBusiness]: P extends '_count' | 'count' ? T[P] extends true ? number : Prisma.GetScalarType<T[P], AggregateBusiness[P]> : Prisma.GetScalarType<T[P], AggregateBusiness[P]>;
};
export type BusinessGroupByArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BusinessWhereInput;
    orderBy?: Prisma.BusinessOrderByWithAggregationInput | Prisma.BusinessOrderByWithAggregationInput[];
    by: Prisma.BusinessScalarFieldEnum[] | Prisma.BusinessScalarFieldEnum;
    having?: Prisma.BusinessScalarWhereWithAggregatesInput;
    take?: number;
    skip?: number;
    _count?: BusinessCountAggregateInputType | true;
    _min?: BusinessMinAggregateInputType;
    _max?: BusinessMaxAggregateInputType;
};
export type BusinessGroupByOutputType = {
    id: string;
    ownerId: string;
    name: string;
    businessType: $Enums.BusinessType;
    url: string | null;
    taxId: string | null;
    logo: string | null;
    active: boolean;
    createdAt: Date;
    updatedAt: Date;
    createdBy: string;
    updatedBy: string | null;
    _count: BusinessCountAggregateOutputType | null;
    _min: BusinessMinAggregateOutputType | null;
    _max: BusinessMaxAggregateOutputType | null;
};
type GetBusinessGroupByPayload<T extends BusinessGroupByArgs> = Prisma.PrismaPromise<Array<Prisma.PickEnumerable<BusinessGroupByOutputType, T['by']> & {
    [P in ((keyof T) & (keyof BusinessGroupByOutputType))]: P extends '_count' ? T[P] extends boolean ? number : Prisma.GetScalarType<T[P], BusinessGroupByOutputType[P]> : Prisma.GetScalarType<T[P], BusinessGroupByOutputType[P]>;
}>>;
export type BusinessWhereInput = {
    AND?: Prisma.BusinessWhereInput | Prisma.BusinessWhereInput[];
    OR?: Prisma.BusinessWhereInput[];
    NOT?: Prisma.BusinessWhereInput | Prisma.BusinessWhereInput[];
    id?: Prisma.UuidFilter<"Business"> | string;
    ownerId?: Prisma.UuidFilter<"Business"> | string;
    name?: Prisma.StringFilter<"Business"> | string;
    businessType?: Prisma.EnumBusinessTypeFilter<"Business"> | $Enums.BusinessType;
    url?: Prisma.StringNullableFilter<"Business"> | string | null;
    taxId?: Prisma.StringNullableFilter<"Business"> | string | null;
    logo?: Prisma.StringNullableFilter<"Business"> | string | null;
    active?: Prisma.BoolFilter<"Business"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"Business"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"Business"> | Date | string;
    createdBy?: Prisma.UuidFilter<"Business"> | string;
    updatedBy?: Prisma.UuidNullableFilter<"Business"> | string | null;
    branches?: Prisma.BranchListRelationFilter;
    systemAssignments?: Prisma.SystemAssignmentListRelationFilter;
    members?: Prisma.BusinessMemberListRelationFilter;
};
export type BusinessOrderByWithRelationInput = {
    id?: Prisma.SortOrder;
    ownerId?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    businessType?: Prisma.SortOrder;
    url?: Prisma.SortOrderInput | Prisma.SortOrder;
    taxId?: Prisma.SortOrderInput | Prisma.SortOrder;
    logo?: Prisma.SortOrderInput | Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    branches?: Prisma.BranchOrderByRelationAggregateInput;
    systemAssignments?: Prisma.SystemAssignmentOrderByRelationAggregateInput;
    members?: Prisma.BusinessMemberOrderByRelationAggregateInput;
};
export type BusinessWhereUniqueInput = Prisma.AtLeast<{
    id?: string;
    AND?: Prisma.BusinessWhereInput | Prisma.BusinessWhereInput[];
    OR?: Prisma.BusinessWhereInput[];
    NOT?: Prisma.BusinessWhereInput | Prisma.BusinessWhereInput[];
    ownerId?: Prisma.UuidFilter<"Business"> | string;
    name?: Prisma.StringFilter<"Business"> | string;
    businessType?: Prisma.EnumBusinessTypeFilter<"Business"> | $Enums.BusinessType;
    url?: Prisma.StringNullableFilter<"Business"> | string | null;
    taxId?: Prisma.StringNullableFilter<"Business"> | string | null;
    logo?: Prisma.StringNullableFilter<"Business"> | string | null;
    active?: Prisma.BoolFilter<"Business"> | boolean;
    createdAt?: Prisma.DateTimeFilter<"Business"> | Date | string;
    updatedAt?: Prisma.DateTimeFilter<"Business"> | Date | string;
    createdBy?: Prisma.UuidFilter<"Business"> | string;
    updatedBy?: Prisma.UuidNullableFilter<"Business"> | string | null;
    branches?: Prisma.BranchListRelationFilter;
    systemAssignments?: Prisma.SystemAssignmentListRelationFilter;
    members?: Prisma.BusinessMemberListRelationFilter;
}, "id">;
export type BusinessOrderByWithAggregationInput = {
    id?: Prisma.SortOrder;
    ownerId?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    businessType?: Prisma.SortOrder;
    url?: Prisma.SortOrderInput | Prisma.SortOrder;
    taxId?: Prisma.SortOrderInput | Prisma.SortOrder;
    logo?: Prisma.SortOrderInput | Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrderInput | Prisma.SortOrder;
    _count?: Prisma.BusinessCountOrderByAggregateInput;
    _max?: Prisma.BusinessMaxOrderByAggregateInput;
    _min?: Prisma.BusinessMinOrderByAggregateInput;
};
export type BusinessScalarWhereWithAggregatesInput = {
    AND?: Prisma.BusinessScalarWhereWithAggregatesInput | Prisma.BusinessScalarWhereWithAggregatesInput[];
    OR?: Prisma.BusinessScalarWhereWithAggregatesInput[];
    NOT?: Prisma.BusinessScalarWhereWithAggregatesInput | Prisma.BusinessScalarWhereWithAggregatesInput[];
    id?: Prisma.UuidWithAggregatesFilter<"Business"> | string;
    ownerId?: Prisma.UuidWithAggregatesFilter<"Business"> | string;
    name?: Prisma.StringWithAggregatesFilter<"Business"> | string;
    businessType?: Prisma.EnumBusinessTypeWithAggregatesFilter<"Business"> | $Enums.BusinessType;
    url?: Prisma.StringNullableWithAggregatesFilter<"Business"> | string | null;
    taxId?: Prisma.StringNullableWithAggregatesFilter<"Business"> | string | null;
    logo?: Prisma.StringNullableWithAggregatesFilter<"Business"> | string | null;
    active?: Prisma.BoolWithAggregatesFilter<"Business"> | boolean;
    createdAt?: Prisma.DateTimeWithAggregatesFilter<"Business"> | Date | string;
    updatedAt?: Prisma.DateTimeWithAggregatesFilter<"Business"> | Date | string;
    createdBy?: Prisma.UuidWithAggregatesFilter<"Business"> | string;
    updatedBy?: Prisma.UuidNullableWithAggregatesFilter<"Business"> | string | null;
};
export type BusinessCreateInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    branches?: Prisma.BranchCreateNestedManyWithoutBusinessInput;
    systemAssignments?: Prisma.SystemAssignmentCreateNestedManyWithoutBusinessInput;
    members?: Prisma.BusinessMemberCreateNestedManyWithoutBusinessInput;
};
export type BusinessUncheckedCreateInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    branches?: Prisma.BranchUncheckedCreateNestedManyWithoutBusinessInput;
    systemAssignments?: Prisma.SystemAssignmentUncheckedCreateNestedManyWithoutBusinessInput;
    members?: Prisma.BusinessMemberUncheckedCreateNestedManyWithoutBusinessInput;
};
export type BusinessUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    branches?: Prisma.BranchUpdateManyWithoutBusinessNestedInput;
    systemAssignments?: Prisma.SystemAssignmentUpdateManyWithoutBusinessNestedInput;
    members?: Prisma.BusinessMemberUpdateManyWithoutBusinessNestedInput;
};
export type BusinessUncheckedUpdateInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    branches?: Prisma.BranchUncheckedUpdateManyWithoutBusinessNestedInput;
    systemAssignments?: Prisma.SystemAssignmentUncheckedUpdateManyWithoutBusinessNestedInput;
    members?: Prisma.BusinessMemberUncheckedUpdateManyWithoutBusinessNestedInput;
};
export type BusinessCreateManyInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
};
export type BusinessUpdateManyMutationInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type BusinessUncheckedUpdateManyInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
};
export type BusinessScalarRelationFilter = {
    is?: Prisma.BusinessWhereInput;
    isNot?: Prisma.BusinessWhereInput;
};
export type BusinessCountOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    ownerId?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    businessType?: Prisma.SortOrder;
    url?: Prisma.SortOrder;
    taxId?: Prisma.SortOrder;
    logo?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type BusinessMaxOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    ownerId?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    businessType?: Prisma.SortOrder;
    url?: Prisma.SortOrder;
    taxId?: Prisma.SortOrder;
    logo?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type BusinessMinOrderByAggregateInput = {
    id?: Prisma.SortOrder;
    ownerId?: Prisma.SortOrder;
    name?: Prisma.SortOrder;
    businessType?: Prisma.SortOrder;
    url?: Prisma.SortOrder;
    taxId?: Prisma.SortOrder;
    logo?: Prisma.SortOrder;
    active?: Prisma.SortOrder;
    createdAt?: Prisma.SortOrder;
    updatedAt?: Prisma.SortOrder;
    createdBy?: Prisma.SortOrder;
    updatedBy?: Prisma.SortOrder;
};
export type BusinessCreateNestedOneWithoutSystemAssignmentsInput = {
    create?: Prisma.XOR<Prisma.BusinessCreateWithoutSystemAssignmentsInput, Prisma.BusinessUncheckedCreateWithoutSystemAssignmentsInput>;
    connectOrCreate?: Prisma.BusinessCreateOrConnectWithoutSystemAssignmentsInput;
    connect?: Prisma.BusinessWhereUniqueInput;
};
export type BusinessUpdateOneRequiredWithoutSystemAssignmentsNestedInput = {
    create?: Prisma.XOR<Prisma.BusinessCreateWithoutSystemAssignmentsInput, Prisma.BusinessUncheckedCreateWithoutSystemAssignmentsInput>;
    connectOrCreate?: Prisma.BusinessCreateOrConnectWithoutSystemAssignmentsInput;
    upsert?: Prisma.BusinessUpsertWithoutSystemAssignmentsInput;
    connect?: Prisma.BusinessWhereUniqueInput;
    update?: Prisma.XOR<Prisma.XOR<Prisma.BusinessUpdateToOneWithWhereWithoutSystemAssignmentsInput, Prisma.BusinessUpdateWithoutSystemAssignmentsInput>, Prisma.BusinessUncheckedUpdateWithoutSystemAssignmentsInput>;
};
export type EnumBusinessTypeFieldUpdateOperationsInput = {
    set?: $Enums.BusinessType;
};
export type BoolFieldUpdateOperationsInput = {
    set?: boolean;
};
export type BusinessCreateNestedOneWithoutMembersInput = {
    create?: Prisma.XOR<Prisma.BusinessCreateWithoutMembersInput, Prisma.BusinessUncheckedCreateWithoutMembersInput>;
    connectOrCreate?: Prisma.BusinessCreateOrConnectWithoutMembersInput;
    connect?: Prisma.BusinessWhereUniqueInput;
};
export type BusinessUpdateOneRequiredWithoutMembersNestedInput = {
    create?: Prisma.XOR<Prisma.BusinessCreateWithoutMembersInput, Prisma.BusinessUncheckedCreateWithoutMembersInput>;
    connectOrCreate?: Prisma.BusinessCreateOrConnectWithoutMembersInput;
    upsert?: Prisma.BusinessUpsertWithoutMembersInput;
    connect?: Prisma.BusinessWhereUniqueInput;
    update?: Prisma.XOR<Prisma.XOR<Prisma.BusinessUpdateToOneWithWhereWithoutMembersInput, Prisma.BusinessUpdateWithoutMembersInput>, Prisma.BusinessUncheckedUpdateWithoutMembersInput>;
};
export type BusinessCreateNestedOneWithoutBranchesInput = {
    create?: Prisma.XOR<Prisma.BusinessCreateWithoutBranchesInput, Prisma.BusinessUncheckedCreateWithoutBranchesInput>;
    connectOrCreate?: Prisma.BusinessCreateOrConnectWithoutBranchesInput;
    connect?: Prisma.BusinessWhereUniqueInput;
};
export type BusinessUpdateOneRequiredWithoutBranchesNestedInput = {
    create?: Prisma.XOR<Prisma.BusinessCreateWithoutBranchesInput, Prisma.BusinessUncheckedCreateWithoutBranchesInput>;
    connectOrCreate?: Prisma.BusinessCreateOrConnectWithoutBranchesInput;
    upsert?: Prisma.BusinessUpsertWithoutBranchesInput;
    connect?: Prisma.BusinessWhereUniqueInput;
    update?: Prisma.XOR<Prisma.XOR<Prisma.BusinessUpdateToOneWithWhereWithoutBranchesInput, Prisma.BusinessUpdateWithoutBranchesInput>, Prisma.BusinessUncheckedUpdateWithoutBranchesInput>;
};
export type BusinessCreateWithoutSystemAssignmentsInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    branches?: Prisma.BranchCreateNestedManyWithoutBusinessInput;
    members?: Prisma.BusinessMemberCreateNestedManyWithoutBusinessInput;
};
export type BusinessUncheckedCreateWithoutSystemAssignmentsInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    branches?: Prisma.BranchUncheckedCreateNestedManyWithoutBusinessInput;
    members?: Prisma.BusinessMemberUncheckedCreateNestedManyWithoutBusinessInput;
};
export type BusinessCreateOrConnectWithoutSystemAssignmentsInput = {
    where: Prisma.BusinessWhereUniqueInput;
    create: Prisma.XOR<Prisma.BusinessCreateWithoutSystemAssignmentsInput, Prisma.BusinessUncheckedCreateWithoutSystemAssignmentsInput>;
};
export type BusinessUpsertWithoutSystemAssignmentsInput = {
    update: Prisma.XOR<Prisma.BusinessUpdateWithoutSystemAssignmentsInput, Prisma.BusinessUncheckedUpdateWithoutSystemAssignmentsInput>;
    create: Prisma.XOR<Prisma.BusinessCreateWithoutSystemAssignmentsInput, Prisma.BusinessUncheckedCreateWithoutSystemAssignmentsInput>;
    where?: Prisma.BusinessWhereInput;
};
export type BusinessUpdateToOneWithWhereWithoutSystemAssignmentsInput = {
    where?: Prisma.BusinessWhereInput;
    data: Prisma.XOR<Prisma.BusinessUpdateWithoutSystemAssignmentsInput, Prisma.BusinessUncheckedUpdateWithoutSystemAssignmentsInput>;
};
export type BusinessUpdateWithoutSystemAssignmentsInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    branches?: Prisma.BranchUpdateManyWithoutBusinessNestedInput;
    members?: Prisma.BusinessMemberUpdateManyWithoutBusinessNestedInput;
};
export type BusinessUncheckedUpdateWithoutSystemAssignmentsInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    branches?: Prisma.BranchUncheckedUpdateManyWithoutBusinessNestedInput;
    members?: Prisma.BusinessMemberUncheckedUpdateManyWithoutBusinessNestedInput;
};
export type BusinessCreateWithoutMembersInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    branches?: Prisma.BranchCreateNestedManyWithoutBusinessInput;
    systemAssignments?: Prisma.SystemAssignmentCreateNestedManyWithoutBusinessInput;
};
export type BusinessUncheckedCreateWithoutMembersInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    branches?: Prisma.BranchUncheckedCreateNestedManyWithoutBusinessInput;
    systemAssignments?: Prisma.SystemAssignmentUncheckedCreateNestedManyWithoutBusinessInput;
};
export type BusinessCreateOrConnectWithoutMembersInput = {
    where: Prisma.BusinessWhereUniqueInput;
    create: Prisma.XOR<Prisma.BusinessCreateWithoutMembersInput, Prisma.BusinessUncheckedCreateWithoutMembersInput>;
};
export type BusinessUpsertWithoutMembersInput = {
    update: Prisma.XOR<Prisma.BusinessUpdateWithoutMembersInput, Prisma.BusinessUncheckedUpdateWithoutMembersInput>;
    create: Prisma.XOR<Prisma.BusinessCreateWithoutMembersInput, Prisma.BusinessUncheckedCreateWithoutMembersInput>;
    where?: Prisma.BusinessWhereInput;
};
export type BusinessUpdateToOneWithWhereWithoutMembersInput = {
    where?: Prisma.BusinessWhereInput;
    data: Prisma.XOR<Prisma.BusinessUpdateWithoutMembersInput, Prisma.BusinessUncheckedUpdateWithoutMembersInput>;
};
export type BusinessUpdateWithoutMembersInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    branches?: Prisma.BranchUpdateManyWithoutBusinessNestedInput;
    systemAssignments?: Prisma.SystemAssignmentUpdateManyWithoutBusinessNestedInput;
};
export type BusinessUncheckedUpdateWithoutMembersInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    branches?: Prisma.BranchUncheckedUpdateManyWithoutBusinessNestedInput;
    systemAssignments?: Prisma.SystemAssignmentUncheckedUpdateManyWithoutBusinessNestedInput;
};
export type BusinessCreateWithoutBranchesInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    systemAssignments?: Prisma.SystemAssignmentCreateNestedManyWithoutBusinessInput;
    members?: Prisma.BusinessMemberCreateNestedManyWithoutBusinessInput;
};
export type BusinessUncheckedCreateWithoutBranchesInput = {
    id?: string;
    ownerId: string;
    name: string;
    businessType?: $Enums.BusinessType;
    url?: string | null;
    taxId?: string | null;
    logo?: string | null;
    active?: boolean;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    createdBy: string;
    updatedBy?: string | null;
    systemAssignments?: Prisma.SystemAssignmentUncheckedCreateNestedManyWithoutBusinessInput;
    members?: Prisma.BusinessMemberUncheckedCreateNestedManyWithoutBusinessInput;
};
export type BusinessCreateOrConnectWithoutBranchesInput = {
    where: Prisma.BusinessWhereUniqueInput;
    create: Prisma.XOR<Prisma.BusinessCreateWithoutBranchesInput, Prisma.BusinessUncheckedCreateWithoutBranchesInput>;
};
export type BusinessUpsertWithoutBranchesInput = {
    update: Prisma.XOR<Prisma.BusinessUpdateWithoutBranchesInput, Prisma.BusinessUncheckedUpdateWithoutBranchesInput>;
    create: Prisma.XOR<Prisma.BusinessCreateWithoutBranchesInput, Prisma.BusinessUncheckedCreateWithoutBranchesInput>;
    where?: Prisma.BusinessWhereInput;
};
export type BusinessUpdateToOneWithWhereWithoutBranchesInput = {
    where?: Prisma.BusinessWhereInput;
    data: Prisma.XOR<Prisma.BusinessUpdateWithoutBranchesInput, Prisma.BusinessUncheckedUpdateWithoutBranchesInput>;
};
export type BusinessUpdateWithoutBranchesInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    systemAssignments?: Prisma.SystemAssignmentUpdateManyWithoutBusinessNestedInput;
    members?: Prisma.BusinessMemberUpdateManyWithoutBusinessNestedInput;
};
export type BusinessUncheckedUpdateWithoutBranchesInput = {
    id?: Prisma.StringFieldUpdateOperationsInput | string;
    ownerId?: Prisma.StringFieldUpdateOperationsInput | string;
    name?: Prisma.StringFieldUpdateOperationsInput | string;
    businessType?: Prisma.EnumBusinessTypeFieldUpdateOperationsInput | $Enums.BusinessType;
    url?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    taxId?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    logo?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    active?: Prisma.BoolFieldUpdateOperationsInput | boolean;
    createdAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    updatedAt?: Prisma.DateTimeFieldUpdateOperationsInput | Date | string;
    createdBy?: Prisma.StringFieldUpdateOperationsInput | string;
    updatedBy?: Prisma.NullableStringFieldUpdateOperationsInput | string | null;
    systemAssignments?: Prisma.SystemAssignmentUncheckedUpdateManyWithoutBusinessNestedInput;
    members?: Prisma.BusinessMemberUncheckedUpdateManyWithoutBusinessNestedInput;
};
export type BusinessCountOutputType = {
    branches: number;
    systemAssignments: number;
    members: number;
};
export type BusinessCountOutputTypeSelect<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    branches?: boolean | BusinessCountOutputTypeCountBranchesArgs;
    systemAssignments?: boolean | BusinessCountOutputTypeCountSystemAssignmentsArgs;
    members?: boolean | BusinessCountOutputTypeCountMembersArgs;
};
export type BusinessCountOutputTypeDefaultArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessCountOutputTypeSelect<ExtArgs> | null;
};
export type BusinessCountOutputTypeCountBranchesArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BranchWhereInput;
};
export type BusinessCountOutputTypeCountSystemAssignmentsArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.SystemAssignmentWhereInput;
};
export type BusinessCountOutputTypeCountMembersArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BusinessMemberWhereInput;
};
export type BusinessSelect<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    ownerId?: boolean;
    name?: boolean;
    businessType?: boolean;
    url?: boolean;
    taxId?: boolean;
    logo?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
    branches?: boolean | Prisma.Business$branchesArgs<ExtArgs>;
    systemAssignments?: boolean | Prisma.Business$systemAssignmentsArgs<ExtArgs>;
    members?: boolean | Prisma.Business$membersArgs<ExtArgs>;
    _count?: boolean | Prisma.BusinessCountOutputTypeDefaultArgs<ExtArgs>;
}, ExtArgs["result"]["business"]>;
export type BusinessSelectCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    ownerId?: boolean;
    name?: boolean;
    businessType?: boolean;
    url?: boolean;
    taxId?: boolean;
    logo?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
}, ExtArgs["result"]["business"]>;
export type BusinessSelectUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetSelect<{
    id?: boolean;
    ownerId?: boolean;
    name?: boolean;
    businessType?: boolean;
    url?: boolean;
    taxId?: boolean;
    logo?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
}, ExtArgs["result"]["business"]>;
export type BusinessSelectScalar = {
    id?: boolean;
    ownerId?: boolean;
    name?: boolean;
    businessType?: boolean;
    url?: boolean;
    taxId?: boolean;
    logo?: boolean;
    active?: boolean;
    createdAt?: boolean;
    updatedAt?: boolean;
    createdBy?: boolean;
    updatedBy?: boolean;
};
export type BusinessOmit<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = runtime.Types.Extensions.GetOmit<"id" | "ownerId" | "name" | "businessType" | "url" | "taxId" | "logo" | "active" | "createdAt" | "updatedAt" | "createdBy" | "updatedBy", ExtArgs["result"]["business"]>;
export type BusinessInclude<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    branches?: boolean | Prisma.Business$branchesArgs<ExtArgs>;
    systemAssignments?: boolean | Prisma.Business$systemAssignmentsArgs<ExtArgs>;
    members?: boolean | Prisma.Business$membersArgs<ExtArgs>;
    _count?: boolean | Prisma.BusinessCountOutputTypeDefaultArgs<ExtArgs>;
};
export type BusinessIncludeCreateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {};
export type BusinessIncludeUpdateManyAndReturn<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {};
export type $BusinessPayload<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    name: "Business";
    objects: {
        branches: Prisma.$BranchPayload<ExtArgs>[];
        systemAssignments: Prisma.$SystemAssignmentPayload<ExtArgs>[];
        members: Prisma.$BusinessMemberPayload<ExtArgs>[];
    };
    scalars: runtime.Types.Extensions.GetPayloadResult<{
        id: string;
        ownerId: string;
        name: string;
        businessType: $Enums.BusinessType;
        url: string | null;
        taxId: string | null;
        logo: string | null;
        active: boolean;
        createdAt: Date;
        updatedAt: Date;
        createdBy: string;
        updatedBy: string | null;
    }, ExtArgs["result"]["business"]>;
    composites: {};
};
export type BusinessGetPayload<S extends boolean | null | undefined | BusinessDefaultArgs> = runtime.Types.Result.GetResult<Prisma.$BusinessPayload, S>;
export type BusinessCountArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = Omit<BusinessFindManyArgs, 'select' | 'include' | 'distinct' | 'omit'> & {
    select?: BusinessCountAggregateInputType | true;
};
export interface BusinessDelegate<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> {
    [K: symbol]: {
        types: Prisma.TypeMap<ExtArgs>['model']['Business'];
        meta: {
            name: 'Business';
        };
    };
    findUnique<T extends BusinessFindUniqueArgs>(args: Prisma.SelectSubset<T, BusinessFindUniqueArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "findUnique", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findUniqueOrThrow<T extends BusinessFindUniqueOrThrowArgs>(args: Prisma.SelectSubset<T, BusinessFindUniqueOrThrowArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "findUniqueOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findFirst<T extends BusinessFindFirstArgs>(args?: Prisma.SelectSubset<T, BusinessFindFirstArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "findFirst", GlobalOmitOptions> | null, null, ExtArgs, GlobalOmitOptions>;
    findFirstOrThrow<T extends BusinessFindFirstOrThrowArgs>(args?: Prisma.SelectSubset<T, BusinessFindFirstOrThrowArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "findFirstOrThrow", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    findMany<T extends BusinessFindManyArgs>(args?: Prisma.SelectSubset<T, BusinessFindManyArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "findMany", GlobalOmitOptions>>;
    create<T extends BusinessCreateArgs>(args: Prisma.SelectSubset<T, BusinessCreateArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "create", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    createMany<T extends BusinessCreateManyArgs>(args?: Prisma.SelectSubset<T, BusinessCreateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    createManyAndReturn<T extends BusinessCreateManyAndReturnArgs>(args?: Prisma.SelectSubset<T, BusinessCreateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "createManyAndReturn", GlobalOmitOptions>>;
    delete<T extends BusinessDeleteArgs>(args: Prisma.SelectSubset<T, BusinessDeleteArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "delete", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    update<T extends BusinessUpdateArgs>(args: Prisma.SelectSubset<T, BusinessUpdateArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "update", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    deleteMany<T extends BusinessDeleteManyArgs>(args?: Prisma.SelectSubset<T, BusinessDeleteManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateMany<T extends BusinessUpdateManyArgs>(args: Prisma.SelectSubset<T, BusinessUpdateManyArgs<ExtArgs>>): Prisma.PrismaPromise<Prisma.BatchPayload>;
    updateManyAndReturn<T extends BusinessUpdateManyAndReturnArgs>(args: Prisma.SelectSubset<T, BusinessUpdateManyAndReturnArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "updateManyAndReturn", GlobalOmitOptions>>;
    upsert<T extends BusinessUpsertArgs>(args: Prisma.SelectSubset<T, BusinessUpsertArgs<ExtArgs>>): Prisma.Prisma__BusinessClient<runtime.Types.Result.GetResult<Prisma.$BusinessPayload<ExtArgs>, T, "upsert", GlobalOmitOptions>, never, ExtArgs, GlobalOmitOptions>;
    count<T extends BusinessCountArgs>(args?: Prisma.Subset<T, BusinessCountArgs>): Prisma.PrismaPromise<T extends runtime.Types.Utils.Record<'select', any> ? T['select'] extends true ? number : Prisma.GetScalarType<T['select'], BusinessCountAggregateOutputType> : number>;
    aggregate<T extends BusinessAggregateArgs>(args: Prisma.Subset<T, BusinessAggregateArgs>): Prisma.PrismaPromise<GetBusinessAggregateType<T>>;
    groupBy<T extends BusinessGroupByArgs, HasSelectOrTake extends Prisma.Or<Prisma.Extends<'skip', Prisma.Keys<T>>, Prisma.Extends<'take', Prisma.Keys<T>>>, OrderByArg extends Prisma.True extends HasSelectOrTake ? {
        orderBy: BusinessGroupByArgs['orderBy'];
    } : {
        orderBy?: BusinessGroupByArgs['orderBy'];
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
    }[OrderFields]>(args: Prisma.SubsetIntersection<T, BusinessGroupByArgs, OrderByArg> & InputErrors): {} extends InputErrors ? GetBusinessGroupByPayload<T> : Prisma.PrismaPromise<InputErrors>;
    readonly fields: BusinessFieldRefs;
}
export interface Prisma__BusinessClient<T, Null = never, ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs, GlobalOmitOptions = {}> extends Prisma.PrismaPromise<T> {
    readonly [Symbol.toStringTag]: "PrismaPromise";
    branches<T extends Prisma.Business$branchesArgs<ExtArgs> = {}>(args?: Prisma.Subset<T, Prisma.Business$branchesArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BranchPayload<ExtArgs>, T, "findMany", GlobalOmitOptions> | Null>;
    systemAssignments<T extends Prisma.Business$systemAssignmentsArgs<ExtArgs> = {}>(args?: Prisma.Subset<T, Prisma.Business$systemAssignmentsArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$SystemAssignmentPayload<ExtArgs>, T, "findMany", GlobalOmitOptions> | Null>;
    members<T extends Prisma.Business$membersArgs<ExtArgs> = {}>(args?: Prisma.Subset<T, Prisma.Business$membersArgs<ExtArgs>>): Prisma.PrismaPromise<runtime.Types.Result.GetResult<Prisma.$BusinessMemberPayload<ExtArgs>, T, "findMany", GlobalOmitOptions> | Null>;
    then<TResult1 = T, TResult2 = never>(onfulfilled?: ((value: T) => TResult1 | PromiseLike<TResult1>) | undefined | null, onrejected?: ((reason: any) => TResult2 | PromiseLike<TResult2>) | undefined | null): runtime.Types.Utils.JsPromise<TResult1 | TResult2>;
    catch<TResult = never>(onrejected?: ((reason: any) => TResult | PromiseLike<TResult>) | undefined | null): runtime.Types.Utils.JsPromise<T | TResult>;
    finally(onfinally?: (() => void) | undefined | null): runtime.Types.Utils.JsPromise<T>;
}
export interface BusinessFieldRefs {
    readonly id: Prisma.FieldRef<"Business", 'String'>;
    readonly ownerId: Prisma.FieldRef<"Business", 'String'>;
    readonly name: Prisma.FieldRef<"Business", 'String'>;
    readonly businessType: Prisma.FieldRef<"Business", 'BusinessType'>;
    readonly url: Prisma.FieldRef<"Business", 'String'>;
    readonly taxId: Prisma.FieldRef<"Business", 'String'>;
    readonly logo: Prisma.FieldRef<"Business", 'String'>;
    readonly active: Prisma.FieldRef<"Business", 'Boolean'>;
    readonly createdAt: Prisma.FieldRef<"Business", 'DateTime'>;
    readonly updatedAt: Prisma.FieldRef<"Business", 'DateTime'>;
    readonly createdBy: Prisma.FieldRef<"Business", 'String'>;
    readonly updatedBy: Prisma.FieldRef<"Business", 'String'>;
}
export type BusinessFindUniqueArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    where: Prisma.BusinessWhereUniqueInput;
};
export type BusinessFindUniqueOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    where: Prisma.BusinessWhereUniqueInput;
};
export type BusinessFindFirstArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    where?: Prisma.BusinessWhereInput;
    orderBy?: Prisma.BusinessOrderByWithRelationInput | Prisma.BusinessOrderByWithRelationInput[];
    cursor?: Prisma.BusinessWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.BusinessScalarFieldEnum | Prisma.BusinessScalarFieldEnum[];
};
export type BusinessFindFirstOrThrowArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    where?: Prisma.BusinessWhereInput;
    orderBy?: Prisma.BusinessOrderByWithRelationInput | Prisma.BusinessOrderByWithRelationInput[];
    cursor?: Prisma.BusinessWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.BusinessScalarFieldEnum | Prisma.BusinessScalarFieldEnum[];
};
export type BusinessFindManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    where?: Prisma.BusinessWhereInput;
    orderBy?: Prisma.BusinessOrderByWithRelationInput | Prisma.BusinessOrderByWithRelationInput[];
    cursor?: Prisma.BusinessWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.BusinessScalarFieldEnum | Prisma.BusinessScalarFieldEnum[];
};
export type BusinessCreateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.BusinessCreateInput, Prisma.BusinessUncheckedCreateInput>;
};
export type BusinessCreateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.BusinessCreateManyInput | Prisma.BusinessCreateManyInput[];
    skipDuplicates?: boolean;
};
export type BusinessCreateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelectCreateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    data: Prisma.BusinessCreateManyInput | Prisma.BusinessCreateManyInput[];
    skipDuplicates?: boolean;
};
export type BusinessUpdateArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    data: Prisma.XOR<Prisma.BusinessUpdateInput, Prisma.BusinessUncheckedUpdateInput>;
    where: Prisma.BusinessWhereUniqueInput;
};
export type BusinessUpdateManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    data: Prisma.XOR<Prisma.BusinessUpdateManyMutationInput, Prisma.BusinessUncheckedUpdateManyInput>;
    where?: Prisma.BusinessWhereInput;
    limit?: number;
};
export type BusinessUpdateManyAndReturnArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelectUpdateManyAndReturn<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    data: Prisma.XOR<Prisma.BusinessUpdateManyMutationInput, Prisma.BusinessUncheckedUpdateManyInput>;
    where?: Prisma.BusinessWhereInput;
    limit?: number;
};
export type BusinessUpsertArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    where: Prisma.BusinessWhereUniqueInput;
    create: Prisma.XOR<Prisma.BusinessCreateInput, Prisma.BusinessUncheckedCreateInput>;
    update: Prisma.XOR<Prisma.BusinessUpdateInput, Prisma.BusinessUncheckedUpdateInput>;
};
export type BusinessDeleteArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
    where: Prisma.BusinessWhereUniqueInput;
};
export type BusinessDeleteManyArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    where?: Prisma.BusinessWhereInput;
    limit?: number;
};
export type Business$branchesArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
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
export type Business$systemAssignmentsArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
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
export type Business$membersArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessMemberSelect<ExtArgs> | null;
    omit?: Prisma.BusinessMemberOmit<ExtArgs> | null;
    include?: Prisma.BusinessMemberInclude<ExtArgs> | null;
    where?: Prisma.BusinessMemberWhereInput;
    orderBy?: Prisma.BusinessMemberOrderByWithRelationInput | Prisma.BusinessMemberOrderByWithRelationInput[];
    cursor?: Prisma.BusinessMemberWhereUniqueInput;
    take?: number;
    skip?: number;
    distinct?: Prisma.BusinessMemberScalarFieldEnum | Prisma.BusinessMemberScalarFieldEnum[];
};
export type BusinessDefaultArgs<ExtArgs extends runtime.Types.Extensions.InternalArgs = runtime.Types.Extensions.DefaultArgs> = {
    select?: Prisma.BusinessSelect<ExtArgs> | null;
    omit?: Prisma.BusinessOmit<ExtArgs> | null;
    include?: Prisma.BusinessInclude<ExtArgs> | null;
};
export {};
