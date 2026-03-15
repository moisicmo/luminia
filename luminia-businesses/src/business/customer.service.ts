import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class CustomerService {
  private readonly logger = new Logger(CustomerService.name);

  async create(
    businessId: string,
    data: { name: string; lastName?: string; taxId?: string; phone?: string; email?: string; address?: string; personId?: string },
    createdBy: string,
  ) {
    try {
      return await prisma.businessCustomer.create({
        data: {
          businessId,
          personId: data.personId ?? null,
          name: data.name,
          lastName: data.lastName,
          taxId: data.taxId,
          phone: data.phone,
          email: data.email,
          address: data.address,
          createdBy,
        },
      });
    } catch (err) {
      this.logger.error(`[customer.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear cliente' });
    }
  }

  async list(businessId: string, filters?: { search?: string }) {
    try {
      const where: any = { businessId, active: true };

      if (filters?.search) {
        const s = filters.search;
        where.OR = [
          { name: { contains: s, mode: 'insensitive' } },
          { lastName: { contains: s, mode: 'insensitive' } },
          { taxId: { contains: s } },
          { phone: { contains: s } },
          { email: { contains: s, mode: 'insensitive' } },
        ];
      }

      return await prisma.businessCustomer.findMany({
        where,
        orderBy: { name: 'asc' },
      });
    } catch (err) {
      this.logger.error(`[customer.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar clientes' });
    }
  }

  async findOne(id: string, businessId: string) {
    try {
      const customer = await prisma.businessCustomer.findFirst({
        where: { id, businessId, active: true },
      });
      if (!customer) throw new RpcException({ status: 404, message: 'Cliente no encontrado' });
      return customer;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al obtener cliente' });
    }
  }

  async update(id: string, businessId: string, data: Record<string, any>, updatedBy: string) {
    try {
      const existing = await prisma.businessCustomer.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Cliente no encontrado' });

      return await prisma.businessCustomer.update({
        where: { id },
        data: { ...data, updatedBy },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[customer.update] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar cliente' });
    }
  }

  async remove(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.businessCustomer.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Cliente no encontrado' });

      await prisma.businessCustomer.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[customer.remove] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al eliminar cliente' });
    }
  }

  // ─── Vinculación retroactiva ──────────────────────────────────────────────
  // Cuando un usuario se registra, buscar clientes sin personId que coincidan
  // por taxId o phone y vincularlos.

  async linkPerson(personId: string, identifiers: { taxId?: string; phone?: string }) {
    try {
      const conditions: any[] = [];
      if (identifiers.taxId) conditions.push({ taxId: identifiers.taxId });
      if (identifiers.phone) conditions.push({ phone: identifiers.phone });
      if (conditions.length === 0) return { linked: 0 };

      const result = await prisma.businessCustomer.updateMany({
        where: {
          personId: null,
          active: true,
          OR: conditions,
        },
        data: { personId },
      });

      return { linked: result.count };
    } catch (err) {
      this.logger.error(`[customer.linkPerson] ${(err as Error).message}`);
      return { linked: 0 };
    }
  }

  // ─── Perfil del cliente: todos los negocios donde ha comprado ─────────────

  async listByPerson(personId: string) {
    try {
      return await prisma.businessCustomer.findMany({
        where: { personId, active: true },
        include: {
          business: { select: { id: true, name: true, logo: true, businessType: true } },
        },
        orderBy: { createdAt: 'desc' },
      });
    } catch (err) {
      this.logger.error(`[customer.listByPerson] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar negocios del cliente' });
    }
  }
}
