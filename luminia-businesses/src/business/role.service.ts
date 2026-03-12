import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';
import { ALL_PERMISSIONS, DEFAULT_ROLES } from './permissions';

@Injectable()
export class RoleService {
  private readonly logger = new Logger(RoleService.name);

  // Called internally when a business is created
  async createDefaults(businessId: string, createdBy: string) {
    for (const def of DEFAULT_ROLES) {
      await prisma.businessRole.create({
        data: {
          businessId,
          name: def.name,
          description: def.description,
          isSystem: def.isSystem,
          permissions: def.permissions,
          createdBy,
        },
      });
    }
  }

  async list(businessId: string) {
    this.logger.log(`[roles.list] business=${businessId}`);
    const roles = await prisma.businessRole.findMany({
      where: { businessId },
      include: { _count: { select: { members: { where: { active: true } } } } },
      orderBy: { createdAt: 'asc' },
    });
    return roles.map((r) => ({ ...r, memberCount: r._count.members }));
  }

  async create(businessId: string, data: { name: string; description?: string; permissions: string[] }, createdBy: string) {
    this.logger.log(`[roles.create] business=${businessId} name="${data.name}"`);
    await this.assertManageAccess(businessId, createdBy);
    const permissions = data.permissions.filter((p) => ALL_PERMISSIONS.includes(p));
    return prisma.businessRole.create({
      data: {
        businessId,
        name: data.name,
        description: data.description,
        isSystem: false,
        permissions,
        createdBy,
      },
    });
  }

  async update(roleId: string, businessId: string, data: { name?: string; description?: string; permissions?: string[] }, updatedBy: string) {
    this.logger.log(`[roles.update] role=${roleId}`);
    await this.assertManageAccess(businessId, updatedBy);
    const role = await prisma.businessRole.findFirst({ where: { id: roleId, businessId } });
    if (!role) throw new RpcException({ status: 404, message: 'Rol no encontrado' });

    const updateData: any = { updatedBy };
    if (data.name !== undefined)        updateData.name = data.name;
    if (data.description !== undefined) updateData.description = data.description;
    if (data.permissions !== undefined) updateData.permissions = data.permissions.filter((p) => ALL_PERMISSIONS.includes(p));

    return prisma.businessRole.update({ where: { id: roleId }, data: updateData });
  }

  async remove(roleId: string, businessId: string, requesterId: string) {
    this.logger.log(`[roles.remove] role=${roleId}`);
    await this.assertManageAccess(businessId, requesterId);
    const role = await prisma.businessRole.findFirst({
      where: { id: roleId, businessId },
      include: { _count: { select: { members: { where: { active: true } } } } },
    });
    if (!role) throw new RpcException({ status: 404, message: 'Rol no encontrado' });
    if (role.isSystem) throw new RpcException({ status: 403, message: 'No se puede eliminar un rol del sistema' });
    if (role._count.members > 0) throw new RpcException({ status: 409, message: 'No se puede eliminar un rol con miembros asignados' });
    await prisma.businessRole.delete({ where: { id: roleId } });
    return { deleted: true, roleId };
  }

  async assignToMember(businessId: string, memberId: string, roleId: string, requesterId: string) {
    this.logger.log(`[roles.assign] member=${memberId} role=${roleId}`);
    await this.assertManageAccess(businessId, requesterId);

    const [member, role] = await Promise.all([
      prisma.businessMember.findFirst({ where: { id: memberId, businessId, active: true } }),
      prisma.businessRole.findFirst({ where: { id: roleId, businessId } }),
    ]);
    if (!member) throw new RpcException({ status: 404, message: 'Miembro no encontrado' });
    if (!role)   throw new RpcException({ status: 404, message: 'Rol no encontrado' });
    if (member.role === 'OWNER') throw new RpcException({ status: 403, message: 'No se puede cambiar el rol del dueño' });

    return prisma.businessMember.update({
      where: { id: memberId },
      data: { roleId, updatedBy: requesterId },
      include: { businessRole: true },
    });
  }

  async listPermissions() {
    return ALL_PERMISSIONS;
  }

  // ─── Helpers ────────────────────────────────────────────────────────────────

  private async assertManageAccess(businessId: string, userId: string) {
    const business = await prisma.business.findUnique({ where: { id: businessId } });
    if (!business?.active) throw new RpcException({ status: 404, message: 'Negocio no encontrado' });
    if (business.ownerId === userId) return;

    const member = await prisma.businessMember.findUnique({
      where: { businessId_userId: { businessId, userId } },
      include: { businessRole: true },
    });
    const canManage = member?.active && member.businessRole?.permissions.includes('roles:gestionar');
    if (!canManage) throw new RpcException({ status: 403, message: 'No tienes permisos para gestionar roles' });
  }
}
