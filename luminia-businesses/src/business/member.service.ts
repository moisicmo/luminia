import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class MemberService {
  private readonly logger = new Logger(MemberService.name);

  async add(businessId: string, userId: string, role: string, invitedBy: string) {
    this.logger.log(`[members.add] business=${businessId} user=${userId} role=${role}`);
    try {
      // Verify the business exists and the requester is owner or admin
      const requester = await this.assertAdminAccess(businessId, invitedBy);
      if (!requester) return; // throws inside

      // Cannot add the owner again
      const business = await prisma.business.findUnique({ where: { id: businessId } });
      if (business?.ownerId === userId) {
        throw new RpcException({ status: 409, message: 'Este usuario ya es el dueño del negocio' });
      }

      // Upsert: if previously removed, reactivate
      const existing = await prisma.businessMember.findUnique({
        where: { businessId_userId: { businessId, userId } },
      });

      if (existing) {
        if (existing.active) {
          throw new RpcException({ status: 409, message: 'El usuario ya es miembro del negocio' });
        }
        const updated = await prisma.businessMember.update({
          where: { id: existing.id },
          data: { active: true, role: role as any, updatedBy: invitedBy },
        });
        return updated;
      }

      const member = await prisma.businessMember.create({
        data: {
          businessId,
          userId,
          role: role as any,
          active: true,
          createdBy: invitedBy,
        },
      });

      this.logger.log(`[members.add] miembro añadido: ${member.id}`);
      return member;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[members.add] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al añadir miembro' });
    }
  }

  async list(businessId: string, requesterId: string) {
    this.logger.log(`[members.list] business=${businessId}`);
    try {
      await this.assertMemberAccess(businessId, requesterId);

      const business = await prisma.business.findUnique({
        where: { id: businessId },
        select: { ownerId: true, id: true },
      });

      const members = await prisma.businessMember.findMany({
        where: { businessId, active: true },
        orderBy: { createdAt: 'asc' },
      });

      // Include the owner as a virtual OWNER entry at the top
      return {
        ownerId: business?.ownerId,
        members,
      };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[members.list] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar miembros' });
    }
  }

  async updateRole(businessId: string, memberId: string, role: string, requesterId: string) {
    this.logger.log(`[members.updateRole] member=${memberId} role=${role}`);
    try {
      await this.assertAdminAccess(businessId, requesterId);

      const member = await prisma.businessMember.findFirst({
        where: { id: memberId, businessId, active: true },
      });
      if (!member) {
        throw new RpcException({ status: 404, message: 'Miembro no encontrado' });
      }
      if (member.role === 'OWNER') {
        throw new RpcException({ status: 403, message: 'No se puede cambiar el rol del dueño' });
      }

      return prisma.businessMember.update({
        where: { id: memberId },
        data: { role: role as any, updatedBy: requesterId },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[members.updateRole] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar rol' });
    }
  }

  async remove(businessId: string, memberId: string, requesterId: string) {
    this.logger.log(`[members.remove] member=${memberId}`);
    try {
      await this.assertAdminAccess(businessId, requesterId);

      const member = await prisma.businessMember.findFirst({
        where: { id: memberId, businessId, active: true },
      });
      if (!member) {
        throw new RpcException({ status: 404, message: 'Miembro no encontrado' });
      }
      if (member.role === 'OWNER') {
        throw new RpcException({ status: 403, message: 'No se puede eliminar al dueño' });
      }
      // Prevent removing yourself unless you're owner
      if (member.userId === requesterId) {
        throw new RpcException({ status: 403, message: 'No puedes removerte a ti mismo' });
      }

      await prisma.businessMember.update({
        where: { id: memberId },
        data: { active: false, updatedBy: requesterId },
      });

      return { removed: true, memberId };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[members.remove] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al remover miembro' });
    }
  }

  async updateAssignment(businessId: string, memberId: string, data: { roleId?: string; branchIds?: string[]; pointOfSaleId?: string | null }, requesterId: string) {
    this.logger.log(`[members.updateAssignment] member=${memberId}`);
    try {
      await this.assertAdminAccess(businessId, requesterId);

      const member = await prisma.businessMember.findFirst({
        where: { id: memberId, businessId, active: true },
      });
      if (!member) throw new RpcException({ status: 404, message: 'Miembro no encontrado' });
      if (member.role === 'OWNER') throw new RpcException({ status: 403, message: 'No se puede modificar al dueño' });

      const updateData: any = { updatedBy: requesterId };
      if (data.roleId !== undefined) updateData.roleId = data.roleId;
      if (data.branchIds !== undefined) updateData.branchIds = data.branchIds;
      if (data.pointOfSaleId !== undefined) updateData.pointOfSaleId = data.pointOfSaleId;

      return prisma.businessMember.update({
        where: { id: memberId },
        data: updateData,
        include: { businessRole: { select: { id: true, name: true } } },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[members.updateAssignment] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar asignación' });
    }
  }

  // ─── Access check (called by gateway guard) ───────────────────────────────

  async checkAccess(businessId: string, userId: string) {
    try {
      const business = await prisma.business.findUnique({
        where: { id: businessId, active: true },
        select: { ownerId: true },
      });

      if (!business) return { hasAccess: false, role: null, isOwner: false, permissions: [], branchIds: [], pointOfSaleId: null };

      if (business.ownerId === userId) {
        // Owner has all permissions implicitly — access to all branches
        return { hasAccess: true, role: 'OWNER', isOwner: true, permissions: ['*'], branchIds: [], pointOfSaleId: null };
      }

      const member = await prisma.businessMember.findUnique({
        where: { businessId_userId: { businessId, userId } },
        include: { businessRole: true },
      });

      if (!member?.active) return { hasAccess: false, role: null, isOwner: false, permissions: [], branchIds: [], pointOfSaleId: null };

      const permissions = member.businessRole?.permissions ?? [];
      return {
        hasAccess: true,
        role: member.businessRole?.name ?? member.role,
        roleId: member.roleId,
        isOwner: false,
        permissions,
        branchIds: member.branchIds ?? [],
        pointOfSaleId: member.pointOfSaleId ?? null,
      };
    } catch (err) {
      this.logger.error(`[members.checkAccess] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al verificar acceso' });
    }
  }

  // ─── Access helpers ────────────────────────────────────────────────────────

  private async assertAdminAccess(businessId: string, userId: string) {
    const business = await prisma.business.findUnique({ where: { id: businessId } });
    if (!business || !business.active) {
      throw new RpcException({ status: 404, message: 'Negocio no encontrado' });
    }
    if (business.ownerId === userId) return business; // owner always has access

    const member = await prisma.businessMember.findUnique({
      where: { businessId_userId: { businessId, userId } },
      include: { businessRole: true },
    });
    const canManage = member?.active && member.businessRole?.permissions.includes('miembros:gestionar');
    if (!canManage) {
      throw new RpcException({ status: 403, message: 'No tienes permisos para gestionar miembros' });
    }
    return business;
  }

  private async assertMemberAccess(businessId: string, userId: string) {
    const business = await prisma.business.findUnique({ where: { id: businessId } });
    if (!business || !business.active) {
      throw new RpcException({ status: 404, message: 'Negocio no encontrado' });
    }
    if (business.ownerId === userId) return;

    const member = await prisma.businessMember.findUnique({
      where: { businessId_userId: { businessId, userId } },
    });
    if (!member?.active) {
      throw new RpcException({ status: 403, message: 'No eres miembro de este negocio' });
    }
  }
}
