import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { BusinessService } from './business.service';
import { MemberService } from './member.service';
import { RoleService } from './role.service';
import { BranchService } from './branch.service';
import { PointOfSaleService } from './point-of-sale.service';
import { CashRegisterService } from './cash-register.service';
import { CustomerService } from './customer.service';
import { InvitationService } from './invitation.service';
import { CreateBusinessDto } from './dto/create-business.dto';

@Controller()
export class BusinessController {
  constructor(
    private readonly businessService: BusinessService,
    private readonly memberService: MemberService,
    private readonly roleService: RoleService,
    private readonly branchService: BranchService,
    private readonly posService: PointOfSaleService,
    private readonly cashService: CashRegisterService,
    private readonly customerService: CustomerService,
    private readonly invitationService: InvitationService,
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

  @MessagePattern('business.members.updateAssignment')
  updateMemberAssignment(@Payload() data: { businessId: string; memberId: string; dto: any; requesterId: string }) {
    return this.memberService.updateAssignment(data.businessId, data.memberId, data.dto, data.requesterId);
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

  // ─── Branches ──────────────────────────────────────────────────────────────

  @MessagePattern('business.branches.list')
  listBranches(@Payload() data: { businessId: string }) {
    return this.branchService.list(data.businessId);
  }

  @MessagePattern('business.branches.findOne')
  findOneBranch(@Payload() data: { id: string; businessId: string }) {
    return this.branchService.findOne(data.id, data.businessId);
  }

  @MessagePattern('business.branches.create')
  createBranch(@Payload() data: { dto: any; businessId: string; createdBy: string }) {
    return this.branchService.create(data.dto, data.businessId, data.createdBy);
  }

  @MessagePattern('business.branches.update')
  updateBranch(@Payload() data: { id: string; dto: any; businessId: string; updatedBy: string }) {
    return this.branchService.update(data.id, data.dto, data.businessId, data.updatedBy);
  }

  @MessagePattern('business.branches.deactivate')
  deactivateBranch(@Payload() data: { id: string; businessId: string; updatedBy: string }) {
    return this.branchService.deactivate(data.id, data.businessId, data.updatedBy);
  }

  // ─── Points of Sale ────────────────────────────────────────────────────────

  @MessagePattern('business.pos.list')
  listPos(@Payload() data: { branchId: string }) {
    return this.posService.list(data.branchId);
  }

  @MessagePattern('business.pos.create')
  createPos(@Payload() data: { dto: { name: string }; branchId: string; createdBy: string }) {
    return this.posService.create(data.dto, data.branchId, data.createdBy);
  }

  @MessagePattern('business.pos.update')
  updatePos(@Payload() data: { id: string; dto: { name?: string }; updatedBy: string }) {
    return this.posService.update(data.id, data.dto, data.updatedBy);
  }

  @MessagePattern('business.pos.deactivate')
  deactivatePos(@Payload() data: { id: string; updatedBy: string }) {
    return this.posService.deactivate(data.id, data.updatedBy);
  }

  // ─── Cash Register ─────────────────────────────────────────────────────────

  @MessagePattern('business.cash.open')
  openCash(@Payload() data: { dto: any; businessId: string; userId: string }) {
    return this.cashService.open(data.dto, data.businessId, data.userId);
  }

  @MessagePattern('business.cash.close')
  closeCash(@Payload() data: { sessionId: string; dto: any; userId: string }) {
    return this.cashService.close(data.sessionId, data.dto, data.userId);
  }

  @MessagePattern('business.cash.active')
  activeCash(@Payload() data: { businessId: string; userId: string }) {
    return this.cashService.getActive(data.businessId, data.userId);
  }

  @MessagePattern('business.cash.list')
  listCash(@Payload() data: { businessId: string; filters?: any }) {
    return this.cashService.list(data.businessId, data.filters);
  }

  @MessagePattern('business.cash.findOne')
  findOneCash(@Payload() data: { sessionId: string }) {
    return this.cashService.findOne(data.sessionId);
  }

  @MessagePattern('business.cash.addSaleAmount')
  addSaleAmount(@Payload() data: { sessionId: string; amount: number }) {
    return this.cashService.addSaleAmount(data.sessionId, data.amount);
  }

  // ─── Customers ──────────────────────────────────────────────────────────────

  @MessagePattern('business.customers.create')
  createCustomer(@Payload() data: { businessId: string; dto: any; createdBy: string }) {
    return this.customerService.create(data.businessId, data.dto, data.createdBy);
  }

  @MessagePattern('business.customers.list')
  listCustomers(@Payload() data: { businessId: string; filters?: any }) {
    return this.customerService.list(data.businessId, data.filters);
  }

  @MessagePattern('business.customers.findOne')
  findOneCustomer(@Payload() data: { id: string; businessId: string }) {
    return this.customerService.findOne(data.id, data.businessId);
  }

  @MessagePattern('business.customers.update')
  updateCustomer(@Payload() data: { id: string; businessId: string; dto: any; updatedBy: string }) {
    return this.customerService.update(data.id, data.businessId, data.dto, data.updatedBy);
  }

  @MessagePattern('business.customers.remove')
  removeCustomer(@Payload() data: { id: string; businessId: string; updatedBy: string }) {
    return this.customerService.remove(data.id, data.businessId, data.updatedBy);
  }

  @MessagePattern('business.customers.linkPerson')
  linkCustomerPerson(@Payload() data: { personId: string; identifiers: { taxId?: string; phone?: string } }) {
    return this.customerService.linkPerson(data.personId, data.identifiers);
  }

  @MessagePattern('business.customers.byPerson')
  listCustomersByPerson(@Payload() data: { personId: string }) {
    return this.customerService.listByPerson(data.personId);
  }

  // ─── Invitations ────────────────────────────────────────────────────────────

  @MessagePattern('business.invitations.create')
  createInvitation(@Payload() data: { businessId: string; dto: any; createdBy: string }) {
    return this.invitationService.create(data.businessId, data.dto, data.createdBy);
  }

  @MessagePattern('business.invitations.list')
  listInvitations(@Payload() data: { businessId: string }) {
    return this.invitationService.list(data.businessId);
  }

  @MessagePattern('business.invitations.cancel')
  cancelInvitation(@Payload() data: { id: string; businessId: string }) {
    return this.invitationService.cancel(data.id, data.businessId);
  }

  @MessagePattern('business.invitations.accept')
  acceptInvitation(@Payload() data: { token: string; userId: string }) {
    return this.invitationService.accept(data.token, data.userId);
  }

  @MessagePattern('business.invitations.autoAccept')
  autoAcceptInvitations(@Payload() data: { userId: string; identifiers: { email?: string; phone?: string } }) {
    return this.invitationService.autoAcceptForUser(data.userId, data.identifiers);
  }
}
