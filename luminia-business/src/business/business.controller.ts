import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { BusinessService } from './business.service';
import { MemberService } from './member.service';
import { CreateBusinessDto } from './dto/create-business.dto';

@Controller()
export class BusinessController {
  constructor(
    private readonly businessService: BusinessService,
    private readonly memberService: MemberService,
  ) {}

  // ─── Business ─────────────────────────────────────────────────────────────

  @MessagePattern('business.create')
  create(@Payload() data: { dto: CreateBusinessDto; ownerId: string }) {
    return this.businessService.create(data.dto, data.ownerId);
  }

  @MessagePattern('business.list')
  findAll(@Payload() data: { ownerId: string }) {
    return this.businessService.findByOwner(data.ownerId);
  }

  @MessagePattern('business.findOne')
  findOne(@Payload() data: { businessId: string; requesterId: string }) {
    return this.businessService.findOne(data.businessId, data.requesterId);
  }

  // ─── Members ──────────────────────────────────────────────────────────────

  @MessagePattern('business.members.add')
  addMember(@Payload() data: { businessId: string; userId: string; role: string; invitedBy: string }) {
    return this.memberService.add(data.businessId, data.userId, data.role, data.invitedBy);
  }

  @MessagePattern('business.members.list')
  listMembers(@Payload() data: { businessId: string; requesterId: string }) {
    return this.memberService.list(data.businessId, data.requesterId);
  }

  @MessagePattern('business.members.updateRole')
  updateMemberRole(@Payload() data: { businessId: string; memberId: string; role: string; requesterId: string }) {
    return this.memberService.updateRole(data.businessId, data.memberId, data.role, data.requesterId);
  }

  @MessagePattern('business.members.remove')
  removeMember(@Payload() data: { businessId: string; memberId: string; requesterId: string }) {
    return this.memberService.remove(data.businessId, data.memberId, data.requesterId);
  }

  @MessagePattern('business.access.check')
  checkAccess(@Payload() data: { businessId: string; userId: string }) {
    return this.memberService.checkAccess(data.businessId, data.userId);
  }
}
