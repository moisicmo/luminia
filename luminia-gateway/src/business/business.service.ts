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
}
