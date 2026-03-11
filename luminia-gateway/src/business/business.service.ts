import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceBusiness } from '@/config';
import { CreateBusinessDto } from './dto/create-business.dto';

const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class BusinessService {
  private readonly logger = new Logger(BusinessService.name);

  constructor(
    @Inject(RMQServiceBusiness.getName())
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

  addMember(businessId: string, userId: string, role: string, invitedBy: string) {
    return this.send('business.members.add', { businessId, userId, role, invitedBy });
  }

  listMembers(businessId: string, requesterId: string) {
    return this.send('business.members.list', { businessId, requesterId });
  }

  updateMemberRole(businessId: string, memberId: string, role: string, requesterId: string) {
    return this.send('business.members.updateRole', { businessId, memberId, role, requesterId });
  }

  removeMember(businessId: string, memberId: string, requesterId: string) {
    return this.send('business.members.remove', { businessId, memberId, requesterId });
  }
}
