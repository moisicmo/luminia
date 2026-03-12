export declare class MemberService {
    private readonly logger;
    add(businessId: string, userId: string, role: string, invitedBy: string): Promise<any>;
    list(businessId: string, requesterId: string): Promise<{
        ownerId: any;
        members: runtime.Types.Public.PrismaPromise<T>;
    }>;
    updateRole(businessId: string, memberId: string, role: string, requesterId: string): Promise<runtime.Types.Result.GetResult<import("../generated/prisma/models").$BusinessMemberPayload<ExtArgs>, T, "update", GlobalOmitOptions>>;
    remove(businessId: string, memberId: string, requesterId: string): Promise<{
        removed: boolean;
        memberId: string;
    }>;
    checkAccess(businessId: string, userId: string): Promise<{
        hasAccess: boolean;
        role: any;
        isOwner: boolean;
    }>;
    private assertAdminAccess;
    private assertMemberAccess;
}
