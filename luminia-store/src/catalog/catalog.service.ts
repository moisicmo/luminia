import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class CatalogService {
  private readonly logger = new Logger(CatalogService.name);

  // ─── Categories ───────────────────────────────────────────────────────────

  async createCategory(data: { businessId: string; name: string; description?: string; imageUrl?: string; parentId?: string }, createdBy: string) {
    try {
      return prisma.category.create({
        data: { ...data, createdBy, active: true },
        include: { parent: { select: { id: true, name: true } } },
      });
    } catch (err) {
      this.logger.error(`[category.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear categoría' });
    }
  }

  async listCategories(businessId: string) {
    try {
      return prisma.category.findMany({
        where: { businessId, active: true },
        include: {
          parent: { select: { id: true, name: true } },
          children: { where: { active: true }, select: { id: true, name: true } },
          _count: { select: { products: true } },
        },
        orderBy: [{ parentId: 'asc' }, { name: 'asc' }],
      });
    } catch (err) {
      this.logger.error(`[category.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar categorías' });
    }
  }

  async updateCategory(id: string, businessId: string, data: { name?: string; description?: string; imageUrl?: string; parentId?: string }, updatedBy: string) {
    try {
      const existing = await prisma.category.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Categoría no encontrada' });
      return prisma.category.update({ where: { id }, data: { ...data, updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar categoría' });
    }
  }

  async removeCategory(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.category.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Categoría no encontrada' });
      await prisma.category.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar categoría' });
    }
  }

  // ─── Brands ───────────────────────────────────────────────────────────────

  async createBrand(data: { businessId: string; name: string; description?: string }, createdBy: string) {
    try {
      return prisma.brand.create({ data: { ...data, createdBy, active: true } });
    } catch (err) {
      this.logger.error(`[brand.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear marca' });
    }
  }

  async listBrands(businessId: string) {
    try {
      return prisma.brand.findMany({
        where: { businessId, active: true },
        include: { _count: { select: { products: true } } },
        orderBy: { name: 'asc' },
      });
    } catch (err) {
      throw new RpcException({ status: 500, message: 'Error al listar marcas' });
    }
  }

  async updateBrand(id: string, businessId: string, data: { name?: string; description?: string }, updatedBy: string) {
    try {
      const existing = await prisma.brand.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Marca no encontrada' });
      return prisma.brand.update({ where: { id }, data: { ...data, updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar marca' });
    }
  }

  async removeBrand(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.brand.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Marca no encontrada' });
      await prisma.brand.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar marca' });
    }
  }

  // ─── Suppliers ────────────────────────────────────────────────────────────

  async createSupplier(data: { businessId: string; name: string; taxId?: string; contactName?: string; phone?: string; email?: string; address?: string }, createdBy: string) {
    try {
      return prisma.supplier.create({ data: { ...data, createdBy, active: true } });
    } catch (err) {
      this.logger.error(`[supplier.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear proveedor' });
    }
  }

  async listSuppliers(businessId: string) {
    try {
      return prisma.supplier.findMany({
        where: { businessId, active: true },
        orderBy: { name: 'asc' },
      });
    } catch (err) {
      throw new RpcException({ status: 500, message: 'Error al listar proveedores' });
    }
  }

  async updateSupplier(id: string, businessId: string, data: Record<string, any>, updatedBy: string) {
    try {
      const existing = await prisma.supplier.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Proveedor no encontrado' });
      return prisma.supplier.update({ where: { id }, data: { ...data, updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar proveedor' });
    }
  }

  async removeSupplier(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.supplier.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Proveedor no encontrado' });
      await prisma.supplier.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar proveedor' });
    }
  }

  // ─── Units ────────────────────────────────────────────────────────────────

  async createUnit(data: { businessId: string; name: string; abbreviation: string }, createdBy: string) {
    try {
      return prisma.unit.create({ data: { ...data, createdBy, active: true } });
    } catch (err) {
      this.logger.error(`[unit.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear unidad' });
    }
  }

  async listUnits(businessId: string) {
    try {
      return prisma.unit.findMany({
        where: { businessId, active: true },
        orderBy: { name: 'asc' },
      });
    } catch (err) {
      throw new RpcException({ status: 500, message: 'Error al listar unidades' });
    }
  }

  async updateUnit(id: string, businessId: string, data: { name?: string; abbreviation?: string }, updatedBy: string) {
    try {
      const existing = await prisma.unit.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Unidad no encontrada' });
      return prisma.unit.update({ where: { id }, data: { ...data, updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar unidad' });
    }
  }
}
