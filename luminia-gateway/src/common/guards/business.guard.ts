import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
  BadRequestException,
  Logger,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { Inject } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout } from 'rxjs';
import { RMQServiceBusinesses } from '@/config';
import { MIN_ROLE_KEY, MemberRole, ROLE_WEIGHT } from '../decorators/min-role.decorator';

const RMQ_TIMEOUT_MS = 8_000;

@Injectable()
export class BusinessGuard implements CanActivate {
  private readonly logger = new Logger(BusinessGuard.name);

  constructor(
    private readonly reflector: Reflector,
    @Inject(RMQServiceBusinesses.getName())
    private readonly businessClient: ClientProxy,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const req = context.switchToHttp().getRequest();
    const businessId = req.headers['x-business-id'] as string;

    if (!businessId) {
      throw new BadRequestException('Header X-Business-Id requerido');
    }

    const userId: string = req['user']?.sub;
    if (!userId) {
      throw new ForbiddenException('Usuario no autenticado');
    }

    // Call luminia-business to check membership
    let result: { hasAccess: boolean; role: string | null; isOwner: boolean; branchIds?: string[]; pointOfSaleId?: string | null };
    try {
      result = await firstValueFrom(
        this.businessClient
          .send('business.access.check', { businessId, userId })
          .pipe(timeout(RMQ_TIMEOUT_MS)),
      );
    } catch {
      this.logger.error(`[BusinessGuard] No se pudo verificar acceso al negocio ${businessId}`);
      throw new ForbiddenException('No se pudo verificar acceso al negocio');
    }

    if (!result.hasAccess) {
      throw new ForbiddenException('No tienes acceso a este negocio');
    }

    // Check minimum role if required
    const minRole = this.reflector.getAllAndOverride<MemberRole>(MIN_ROLE_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (minRole && result.role) {
      const userWeight = ROLE_WEIGHT[result.role as MemberRole] ?? 0;
      const requiredWeight = ROLE_WEIGHT[minRole];
      if (userWeight < requiredWeight) {
        throw new ForbiddenException(
          `Se requiere el rol ${minRole} o superior para esta acción`,
        );
      }
    }

    // Inject into request for downstream use
    req['businessId'] = businessId;
    req['memberRole'] = result.role;
    req['branchIds'] = result.branchIds ?? [];
    req['pointOfSaleId'] = result.pointOfSaleId ?? null;

    return true;
  }
}
