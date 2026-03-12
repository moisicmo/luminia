import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { BusinessService } from './business.service';
import { MemberService } from './member.service';
import { RoleService } from './role.service';
import { CreateBusinessDto } from './dto/create-business.dto';

@Controller()
export class BusinessController {
  constructor(
    private readonly businessService: BusinessService,
    private readonly memberService: MemberService,
    private readonly roleService: RoleService,
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

  @MessagePattern('business.list.public')
  findPublic() {
    return this.businessService.findPublic();
  }

  @MessagePattern('business.findByUrl')
  findByUrl(@Payload() data: { url: string }) {
    return this.businessService.findByUrl(data.url);
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

  // ─── Roles ────────────────────────────────────────────────────────────────

  @MessagePattern('business.roles.list')
  listRoles(@Payload() data: { businessId: string }) {
    return this.roleService.list(data.businessId);
  }

  @MessagePattern('business.roles.permissions')
  listPermissions() {
    return this.roleService.listPermissions();
  }

  @MessagePattern('business.roles.create')
  createRole(@Payload() data: { businessId: string; name: string; description?: string; permissions: string[]; createdBy: string }) {
    return this.roleService.create(data.businessId, { name: data.name, description: data.description, permissions: data.permissions }, data.createdBy);
  }

  @MessagePattern('business.roles.update')
  updateRole(@Payload() data: { roleId: string; businessId: string; name?: string; description?: string; permissions?: string[]; requesterId: string }) {
    return this.roleService.update(data.roleId, data.businessId, { name: data.name, description: data.description, permissions: data.permissions }, data.requesterId);
  }

  @MessagePattern('business.roles.remove')
  removeRole(@Payload() data: { roleId: string; businessId: string; requesterId: string }) {
    return this.roleService.remove(data.roleId, data.businessId, data.requesterId);
  }

  @MessagePattern('business.roles.assign')
  assignRole(@Payload() data: { businessId: string; memberId: string; roleId: string; requesterId: string }) {
    return this.roleService.assignToMember(data.businessId, data.memberId, data.roleId, data.requesterId);
  }
}
