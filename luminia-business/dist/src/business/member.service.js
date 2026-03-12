"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var MemberService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.MemberService = void 0;
const common_1 = require("@nestjs/common");
const microservices_1 = require("@nestjs/microservices");
const prisma_1 = require("@/lib/prisma");
let MemberService = MemberService_1 = class MemberService {
    logger = new common_1.Logger(MemberService_1.name);
    async add(businessId, userId, role, invitedBy) {
        this.logger.log(`[members.add] business=${businessId} user=${userId} role=${role}`);
        try {
            const requester = await this.assertAdminAccess(businessId, invitedBy);
            if (!requester)
                return;
            const business = await prisma_1.prisma.business.findUnique({ where: { id: businessId } });
            if (business?.ownerId === userId) {
                throw new microservices_1.RpcException({ status: 409, message: 'Este usuario ya es el dueño del negocio' });
            }
            const existing = await prisma_1.prisma.businessMember.findUnique({
                where: { businessId_userId: { businessId, userId } },
            });
            if (existing) {
                if (existing.active) {
                    throw new microservices_1.RpcException({ status: 409, message: 'El usuario ya es miembro del negocio' });
                }
                const updated = await prisma_1.prisma.businessMember.update({
                    where: { id: existing.id },
                    data: { active: true, role: role, updatedBy: invitedBy },
                });
                return updated;
            }
            const member = await prisma_1.prisma.businessMember.create({
                data: {
                    businessId,
                    userId,
                    role: role,
                    active: true,
                    createdBy: invitedBy,
                },
            });
            this.logger.log(`[members.add] miembro añadido: ${member.id}`);
            return member;
        }
        catch (err) {
            if (err instanceof microservices_1.RpcException)
                throw err;
            this.logger.error(`[members.add] error: ${err.message}`);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al añadir miembro' });
        }
    }
    async list(businessId, requesterId) {
        this.logger.log(`[members.list] business=${businessId}`);
        try {
            await this.assertMemberAccess(businessId, requesterId);
            const business = await prisma_1.prisma.business.findUnique({
                where: { id: businessId },
                select: { ownerId: true, id: true },
            });
            const members = await prisma_1.prisma.businessMember.findMany({
                where: { businessId, active: true },
                orderBy: { createdAt: 'asc' },
            });
            return {
                ownerId: business?.ownerId,
                members,
            };
        }
        catch (err) {
            if (err instanceof microservices_1.RpcException)
                throw err;
            this.logger.error(`[members.list] error: ${err.message}`);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al listar miembros' });
        }
    }
    async updateRole(businessId, memberId, role, requesterId) {
        this.logger.log(`[members.updateRole] member=${memberId} role=${role}`);
        try {
            await this.assertAdminAccess(businessId, requesterId);
            const member = await prisma_1.prisma.businessMember.findFirst({
                where: { id: memberId, businessId, active: true },
            });
            if (!member) {
                throw new microservices_1.RpcException({ status: 404, message: 'Miembro no encontrado' });
            }
            if (member.role === 'OWNER') {
                throw new microservices_1.RpcException({ status: 403, message: 'No se puede cambiar el rol del dueño' });
            }
            return prisma_1.prisma.businessMember.update({
                where: { id: memberId },
                data: { role: role, updatedBy: requesterId },
            });
        }
        catch (err) {
            if (err instanceof microservices_1.RpcException)
                throw err;
            this.logger.error(`[members.updateRole] error: ${err.message}`);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al actualizar rol' });
        }
    }
    async remove(businessId, memberId, requesterId) {
        this.logger.log(`[members.remove] member=${memberId}`);
        try {
            await this.assertAdminAccess(businessId, requesterId);
            const member = await prisma_1.prisma.businessMember.findFirst({
                where: { id: memberId, businessId, active: true },
            });
            if (!member) {
                throw new microservices_1.RpcException({ status: 404, message: 'Miembro no encontrado' });
            }
            if (member.role === 'OWNER') {
                throw new microservices_1.RpcException({ status: 403, message: 'No se puede eliminar al dueño' });
            }
            if (member.userId === requesterId) {
                throw new microservices_1.RpcException({ status: 403, message: 'No puedes removerte a ti mismo' });
            }
            await prisma_1.prisma.businessMember.update({
                where: { id: memberId },
                data: { active: false, updatedBy: requesterId },
            });
            return { removed: true, memberId };
        }
        catch (err) {
            if (err instanceof microservices_1.RpcException)
                throw err;
            this.logger.error(`[members.remove] error: ${err.message}`);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al remover miembro' });
        }
    }
    async checkAccess(businessId, userId) {
        try {
            const business = await prisma_1.prisma.business.findUnique({
                where: { id: businessId, active: true },
                select: { ownerId: true },
            });
            if (!business)
                return { hasAccess: false, role: null, isOwner: false };
            if (business.ownerId === userId) {
                return { hasAccess: true, role: 'OWNER', isOwner: true };
            }
            const member = await prisma_1.prisma.businessMember.findUnique({
                where: { businessId_userId: { businessId, userId } },
                select: { role: true, active: true },
            });
            if (!member?.active)
                return { hasAccess: false, role: null, isOwner: false };
            return { hasAccess: true, role: member.role, isOwner: false };
        }
        catch (err) {
            this.logger.error(`[members.checkAccess] error: ${err.message}`);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al verificar acceso' });
        }
    }
    async assertAdminAccess(businessId, userId) {
        const business = await prisma_1.prisma.business.findUnique({ where: { id: businessId } });
        if (!business || !business.active) {
            throw new microservices_1.RpcException({ status: 404, message: 'Negocio no encontrado' });
        }
        if (business.ownerId === userId)
            return business;
        const member = await prisma_1.prisma.businessMember.findUnique({
            where: { businessId_userId: { businessId, userId } },
        });
        if (!member?.active || !['ADMIN'].includes(member.role)) {
            throw new microservices_1.RpcException({ status: 403, message: 'No tienes permisos para gestionar miembros' });
        }
        return business;
    }
    async assertMemberAccess(businessId, userId) {
        const business = await prisma_1.prisma.business.findUnique({ where: { id: businessId } });
        if (!business || !business.active) {
            throw new microservices_1.RpcException({ status: 404, message: 'Negocio no encontrado' });
        }
        if (business.ownerId === userId)
            return;
        const member = await prisma_1.prisma.businessMember.findUnique({
            where: { businessId_userId: { businessId, userId } },
        });
        if (!member?.active) {
            throw new microservices_1.RpcException({ status: 403, message: 'No eres miembro de este negocio' });
        }
    }
};
exports.MemberService = MemberService;
exports.MemberService = MemberService = MemberService_1 = __decorate([
    (0, common_1.Injectable)()
], MemberService);
//# sourceMappingURL=member.service.js.map