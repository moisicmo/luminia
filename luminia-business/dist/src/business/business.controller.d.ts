import { BusinessService } from './business.service';
import { MemberService } from './member.service';
import { CreateBusinessDto } from './dto/create-business.dto';
export declare class BusinessController {
    private readonly businessService;
    private readonly memberService;
    constructor(businessService: BusinessService, memberService: MemberService);
    create(data: {
        dto: CreateBusinessDto;
        ownerId: string;
    }): any;
    findAll(data: {
        ownerId: string;
    }): any;
    findOne(data: {
        businessId: string;
        requesterId: string;
    }): any;
    addMember(data: {
        businessId: string;
        userId: string;
        role: string;
        invitedBy: string;
    }): any;
    listMembers(data: {
        businessId: string;
        requesterId: string;
    }): any;
    updateMemberRole(data: {
        businessId: string;
        memberId: string;
        role: string;
        requesterId: string;
    }): any;
    removeMember(data: {
        businessId: string;
        memberId: string;
        requesterId: string;
    }): any;
    checkAccess(data: {
        businessId: string;
        userId: string;
    }): any;
}
