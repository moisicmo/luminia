import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { randomBytes } from 'crypto';
import { prisma } from '@/lib/prisma';

const INVITATION_EXPIRY_DAYS = 7;

@Injectable()
export class InvitationService {
  private readonly logger = new Logger(InvitationService.name);

  async create(
    businessId: string,
    data: { email?: string; phone?: string; roleId: string; branchIds?: string[] },
    createdBy: string,
  ) {
    try {
      if (!data.email && !data.phone) {
        throw new RpcException({ status: 400, message: 'Debe indicar email o teléfono' });
      }

      // Check if role exists and belongs to this business
      const role = await prisma.businessRole.findFirst({
        where: { id: data.roleId, businessId },
      });
      if (!role) throw new RpcException({ status: 404, message: 'Rol no encontrado' });

      // Check if there's already a pending invitation
      const existing = await prisma.businessInvitation.findFirst({
        where: {
          businessId,
          status: 'PENDING',
          ...(data.email ? { email: data.email } : { phone: data.phone }),
        },
      });
      if (existing) {
        throw new RpcException({ status: 409, message: 'Ya existe una invitación pendiente para este contacto' });
      }

      const token = randomBytes(32).toString('hex');
      const expiresAt = new Date();
      expiresAt.setDate(expiresAt.getDate() + INVITATION_EXPIRY_DAYS);

      const invitation = await prisma.businessInvitation.create({
        data: {
          businessId,
          email: data.email,
          phone: data.phone,
          roleId: data.roleId,
          branchIds: data.branchIds ?? [],
          token,
          expiresAt,
          createdBy,
        },
        include: {
          business: { select: { id: true, name: true } },
          businessRole: { select: { id: true, name: true } },
        },
      });

      return invitation;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[invitation.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear invitación' });
    }
  }

  async list(businessId: string) {
    try {
      return await prisma.businessInvitation.findMany({
        where: { businessId },
        include: {
          businessRole: { select: { id: true, name: true } },
        },
        orderBy: { createdAt: 'desc' },
      });
    } catch (err) {
      this.logger.error(`[invitation.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar invitaciones' });
    }
  }

  async cancel(id: string, businessId: string) {
    try {
      const invitation = await prisma.businessInvitation.findFirst({
        where: { id, businessId, status: 'PENDING' },
      });
      if (!invitation) throw new RpcException({ status: 404, message: 'Invitación no encontrada' });

      await prisma.businessInvitation.update({
        where: { id },
        data: { status: 'CANCELLED' },
      });

      return { cancelled: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[invitation.cancel] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al cancelar invitación' });
    }
  }

  // ─── Aceptar invitación por token ─────────────────────────────────────────
  // El usuario ya se registró/logueó y acepta con el token.

  async accept(token: string, userId: string) {
    try {
      const invitation = await prisma.businessInvitation.findUnique({
        where: { token },
      });

      if (!invitation) throw new RpcException({ status: 404, message: 'Invitación no encontrada' });
      if (invitation.status !== 'PENDING') {
        throw new RpcException({ status: 400, message: `Invitación ya ${invitation.status === 'ACCEPTED' ? 'aceptada' : invitation.status === 'EXPIRED' ? 'expirada' : 'cancelada'}` });
      }
      if (invitation.expiresAt < new Date()) {
        await prisma.businessInvitation.update({ where: { id: invitation.id }, data: { status: 'EXPIRED' } });
        throw new RpcException({ status: 400, message: 'Invitación expirada' });
      }

      // Check if already a member
      const existing = await prisma.businessMember.findUnique({
        where: { businessId_userId: { businessId: invitation.businessId, userId } },
      });

      if (existing?.active) {
        // Already a member, just mark invitation as accepted
        await prisma.businessInvitation.update({ where: { id: invitation.id }, data: { status: 'ACCEPTED' } });
        return { accepted: true, alreadyMember: true, memberId: existing.id };
      }

      // Create or reactivate member
      const member = existing
        ? await prisma.businessMember.update({
            where: { id: existing.id },
            data: {
              active: true,
              role: 'MEMBER',
              roleId: invitation.roleId,
              branchIds: invitation.branchIds,
              updatedBy: userId,
            },
          })
        : await prisma.businessMember.create({
            data: {
              businessId: invitation.businessId,
              userId,
              role: 'MEMBER',
              roleId: invitation.roleId,
              branchIds: invitation.branchIds,
              createdBy: invitation.createdBy,
            },
          });

      await prisma.businessInvitation.update({ where: { id: invitation.id }, data: { status: 'ACCEPTED' } });

      return { accepted: true, alreadyMember: false, memberId: member.id };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[invitation.accept] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al aceptar invitación' });
    }
  }

  // ─── Auto-accept al registrarse ──────────────────────────────────────────
  // Cuando un usuario se registra, buscar invitaciones PENDING que coincidan
  // por email/phone y auto-aceptarlas.

  async autoAcceptForUser(userId: string, identifiers: { email?: string; phone?: string }) {
    try {
      const conditions: any[] = [];
      if (identifiers.email) conditions.push({ email: identifiers.email });
      if (identifiers.phone) conditions.push({ phone: identifiers.phone });
      if (conditions.length === 0) return { accepted: 0 };

      const pending = await prisma.businessInvitation.findMany({
        where: {
          status: 'PENDING',
          expiresAt: { gt: new Date() },
          OR: conditions,
        },
      });

      let accepted = 0;
      for (const inv of pending) {
        try {
          await this.accept(inv.token, userId);
          accepted++;
        } catch {
          // Skip individual failures
        }
      }

      return { accepted };
    } catch (err) {
      this.logger.error(`[invitation.autoAccept] ${(err as Error).message}`);
      return { accepted: 0 };
    }
  }
}
