import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceBusinesses } from '@/config';
import { CreateBusinessDto } from './dto/create-business.dto';

const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class BusinessService {
  private readonly logger = new Logger(BusinessService.name);

  constructor(
    @Inject(RMQServiceBusinesses.getName())
    private readonly businessClient: ClientProxy,
  ) {}

  private async send<T>(pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] enviando → ${pattern}`);
    try {
      const result = await firstValueFrom(
        this.businessClient.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      this.logger.log(`[RMQ] respuesta OK ← ${pattern}`);
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        this.logger.error(`[RMQ] TIMEOUT: ${pattern}`);
        throw new HttpException(
          `El servicio no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      this.logger.error(`[RMQ] ERROR en ${pattern}: ${JSON.stringify(err)}`);
      throw err;
    }
  }

  listPublic() {
    return this.send('business.list.public', {});
  }

  resolveByUrl(url: string) {
    return this.send<{ businessId: string; name: string; url: string; systemId: string | null }>(
      'business.findByUrl',
      { url },
    );
  }

  createBusiness(dto: CreateBusinessDto, ownerId: string) {
    return this.send('business.create', { dto, ownerId });
  }

  getMyBusinesses(ownerId: string) {
    return this.send('business.list', { ownerId });
  }

  getBusinessById(businessId: string, requesterId: string) {
    return this.send('business.findOne', { businessId, requesterId });
  }

  // ─── Members ──────────────────────────────────────────────────────────────

  addMember(businessId: string, userId: string, invitedBy: string, roleId?: string) {
    return this.send('business.members.add', { businessId, userId, role: 'MEMBER', invitedBy, roleId });
  }

  checkAccess(businessId: string, userId: string) {
    return this.send('business.access.check', { businessId, userId });
  }

  listMembers(businessId: string, requesterId: string) {
    return this.send('business.members.list', { businessId, requesterId });
  }

  updateMemberRole(businessId: string, memberId: string, roleId: string, requesterId: string) {
    return this.send('business.roles.assign', { businessId, memberId, roleId, requesterId });
  }

  removeMember(businessId: string, memberId: string, requesterId: string) {
    return this.send('business.members.remove', { businessId, memberId, requesterId });
  }

  // ─── Roles ──────────────────────────────────────────────────────────────

  listRoles(businessId: string) {
    return this.send('business.roles.list', { businessId });
  }

  listPermissions() {
    return this.send('business.roles.permissions', {});
  }

  createRole(businessId: string, data: { name: string; description?: string; permissions: string[] }, createdBy: string) {
    return this.send('business.roles.create', { businessId, ...data, createdBy });
  }

  updateRole(businessId: string, roleId: string, data: { name?: string; description?: string; permissions?: string[] }, requesterId: string) {
    return this.send('business.roles.update', { businessId, roleId, ...data, requesterId });
  }

  removeRole(businessId: string, roleId: string, requesterId: string) {
    return this.send('business.roles.remove', { businessId, roleId, requesterId });
  }

  assignRole(businessId: string, memberId: string, roleId: string, requesterId: string) {
    return this.send('business.roles.assign', { businessId, memberId, roleId, requesterId });
  }

  // ─── Branches ──────────────────────────────────────────────────────────────

  listBranches(businessId: string) {
    return this.send('business.branches.list', { businessId });
  }

  findOneBranch(id: string, businessId: string) {
    return this.send('business.branches.findOne', { id, businessId });
  }

  createBranch(dto: any, businessId: string, createdBy: string) {
    return this.send('business.branches.create', { dto, businessId, createdBy });
  }

  updateBranch(id: string, dto: any, businessId: string, updatedBy: string) {
    return this.send('business.branches.update', { id, dto, businessId, updatedBy });
  }

  deactivateBranch(id: string, businessId: string, updatedBy: string) {
    return this.send('business.branches.deactivate', { id, businessId, updatedBy });
  }

  // ─── Points of Sale ────────────────────────────────────────────────────────

  listPos(branchId: string) {
    return this.send('business.pos.list', { branchId });
  }

  createPos(dto: { name: string; paperSize?: string }, branchId: string, createdBy: string) {
    return this.send('business.pos.create', { dto, branchId, createdBy });
  }

  updatePos(id: string, dto: { name?: string; paperSize?: string }, updatedBy: string) {
    return this.send('business.pos.update', { id, dto, updatedBy });
  }

  deactivatePos(id: string, updatedBy: string) {
    return this.send('business.pos.deactivate', { id, updatedBy });
  }

  // ─── Cash Register ─────────────────────────────────────────────────────────

  openCashRegister(dto: any, businessId: string, userId: string) {
    return this.send('business.cash.open', { dto, businessId, userId });
  }

  closeCashRegister(sessionId: string, dto: any, userId: string) {
    return this.send('business.cash.close', { sessionId, dto, userId });
  }

  getActiveCashRegister(businessId: string, userId: string) {
    return this.send('business.cash.active', { businessId, userId });
  }

  listCashRegisterSessions(businessId: string, filters?: any) {
    return this.send('business.cash.list', { businessId, filters });
  }

  findOneCashRegisterSession(sessionId: string) {
    return this.send('business.cash.findOne', { sessionId });
  }

  addSaleAmount(sessionId: string, amount: number) {
    return this.send('business.cash.addSaleAmount', { sessionId, amount });
  }

  // ─── Members assignment (branches/POS) ─────────────────────────────────────

  updateMemberAssignment(businessId: string, memberId: string, dto: any, requesterId: string) {
    return this.send('business.members.updateAssignment', { businessId, memberId, dto, requesterId });
  }

  // ─── Customers ────────────────────────────────────────────────────────────

  createCustomer(businessId: string, dto: any, createdBy: string) {
    return this.send('business.customers.create', { businessId, dto, createdBy });
  }

  listCustomers(businessId: string, filters?: any) {
    return this.send('business.customers.list', { businessId, filters });
  }

  findOneCustomer(id: string, businessId: string) {
    return this.send('business.customers.findOne', { id, businessId });
  }

  updateCustomer(id: string, businessId: string, dto: any, updatedBy: string) {
    return this.send('business.customers.update', { id, businessId, dto, updatedBy });
  }

  removeCustomer(id: string, businessId: string, updatedBy: string) {
    return this.send('business.customers.remove', { id, businessId, updatedBy });
  }

  listCustomersByPerson(personId: string) {
    return this.send('business.customers.byPerson', { personId });
  }

  // ─── Invitations ──────────────────────────────────────────────────────────

  createInvitation(businessId: string, dto: any, createdBy: string) {
    return this.send('business.invitations.create', { businessId, dto, createdBy });
  }

  listInvitations(businessId: string) {
    return this.send('business.invitations.list', { businessId });
  }

  cancelInvitation(id: string, businessId: string) {
    return this.send('business.invitations.cancel', { id, businessId });
  }

  acceptInvitation(token: string, userId: string) {
    return this.send('business.invitations.accept', { token, userId });
  }
}
