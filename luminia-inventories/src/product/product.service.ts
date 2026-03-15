import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class ProductService {
  private readonly logger = new Logger(ProductService.name);

  // ─── Products ─────────────────────────────────────────────────────────────

  async createProduct(
    data: {
      businessId: string;
      sku?: string;
      name: string;
      baseUnitId?: string;
      categoryId?: string;
      brandId?: string;
      barcode?: string;
      description?: string;
      imageUrl?: string;
      minimumStock?: number;
      maximumStock?: number;
      reorderPoint?: number;
      salePrice?: number;
      purchasePrice?: number;
      price?: number;
      cost?: number;
      isTaxable?: boolean;
      taxRate?: number;
      siatActivityCode?: number;
      siatProductServiceCode?: number;
      siatMeasurementUnitId?: number;
      active?: boolean;
    },
    createdBy: string,
  ) {
    try {
      // Auto-generar SKU si no se envía
      let sku = data.sku;
      if (!sku) {
        const count = await prisma.product.count({ where: { businessId: data.businessId } });
        sku = `PROD-${String(count + 1).padStart(4, '0')}`;
      }

      // Buscar o crear unidad base por defecto ("Unidad")
      let baseUnitId = data.baseUnitId;
      if (!baseUnitId) {
        let defaultUnit = await prisma.unit.findFirst({
          where: { businessId: data.businessId, active: true },
          orderBy: { createdAt: 'asc' },
        });
        if (!defaultUnit) {
          defaultUnit = await prisma.unit.create({
            data: {
              businessId: data.businessId,
              name: 'Unidad',
              abbreviation: 'und',
              createdBy,
              active: true,
            },
          });
        }
        baseUnitId = defaultUnit.id;
      }

      // Mapear price/cost a salePrice/purchasePrice
      const salePrice = data.salePrice ?? data.price;
      const purchasePrice = data.purchasePrice ?? data.cost;

      const { price: _p, cost: _c, active: _a, ...rest } = data;

      return await prisma.product.create({
        data: {
          ...rest,
          sku,
          baseUnitId,
          salePrice,
          purchasePrice,
          createdBy,
          active: data.active !== false,
        },
        include: {
          category: { select: { id: true, name: true } },
          brand: { select: { id: true, name: true } },
          baseUnit: { select: { id: true, name: true, abbreviation: true } },
        },
      });
    } catch (err) {
      this.logger.error(`[product.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear producto' });
    }
  }

  async listProducts(businessId: string, filters: { categoryId?: string; brandId?: string; search?: string }) {
    try {
      const where: any = { businessId, active: true };
      if (filters.categoryId) where.categoryId = filters.categoryId;
      if (filters.brandId) where.brandId = filters.brandId;
      if (filters.search) {
        where.OR = [
          { name: { contains: filters.search, mode: 'insensitive' } },
          { sku: { contains: filters.search, mode: 'insensitive' } },
          { barcode: { contains: filters.search, mode: 'insensitive' } },
        ];
      }

      return await prisma.product.findMany({
        where,
        include: {
          category: { select: { id: true, name: true } },
          brand: { select: { id: true, name: true } },
          baseUnit: { select: { id: true, name: true, abbreviation: true } },
          _count: { select: { productUnits: true, stocks: true } },
        },
        orderBy: { name: 'asc' },
      });
    } catch (err) {
      this.logger.error(`[product.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar productos' });
    }
  }

  async findProduct(id: string, businessId: string) {
    try {
      const product = await prisma.product.findFirst({
        where: { id, businessId, active: true },
        include: {
          category: { select: { id: true, name: true } },
          brand: { select: { id: true, name: true } },
          baseUnit: { select: { id: true, name: true, abbreviation: true } },
          productUnits: {
            where: { active: true },
            include: { unit: { select: { id: true, name: true, abbreviation: true } } },
          },
        },
      });
      if (!product) throw new RpcException({ status: 404, message: 'Producto no encontrado' });
      return product;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al obtener producto' });
    }
  }

  async updateProduct(id: string, businessId: string, data: Record<string, any>, updatedBy: string) {
    try {
      const existing = await prisma.product.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Producto no encontrado' });

      // Mapear aliases
      if (data.price !== undefined && data.salePrice === undefined) { data.salePrice = data.price; delete data.price; }
      if (data.cost !== undefined && data.purchasePrice === undefined) { data.purchasePrice = data.cost; delete data.cost; }

      return await prisma.product.update({
        where: { id },
        data: { ...data, updatedBy },
        include: {
          category: { select: { id: true, name: true } },
          brand: { select: { id: true, name: true } },
          baseUnit: { select: { id: true, name: true, abbreviation: true } },
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar producto' });
    }
  }

  async removeProduct(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.product.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Producto no encontrado' });
      await prisma.product.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar producto' });
    }
  }

  // ─── Mall (public) ────────────────────────────────────────────────────────

  async listMallProducts(filters: { businessIds?: string[]; search?: string; take?: number; skip?: number }) {
    try {
      const take = Math.min(filters.take ?? 40, 100);
      const skip = filters.skip ?? 0;

      const where: any = { active: true, salePrice: { not: null } };

      if (filters.businessIds?.length) {
        where.businessId = { in: filters.businessIds };
      }

      if (filters.search) {
        where.OR = [
          { name: { contains: filters.search, mode: 'insensitive' } },
          { description: { contains: filters.search, mode: 'insensitive' } },
        ];
      }

      const [items, total] = await Promise.all([
        prisma.product.findMany({
          where,
          select: {
            id: true,
            businessId: true,
            name: true,
            description: true,
            salePrice: true,
            imageUrl: true,
            category: { select: { id: true, name: true } },
            brand: { select: { id: true, name: true } },
          },
          orderBy: { createdAt: 'desc' },
          take,
          skip,
        }),
        prisma.product.count({ where }),
      ]);

      return { items, total };
    } catch (err) {
      this.logger.error(`[product.listMall] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar productos del mall' });
    }
  }

  // ─── Product Units ─────────────────────────────────────────────────────────

  async addProductUnit(
    productId: string,
    businessId: string,
    data: {
      unitId: string;
      conversionFactor: number;
      isBase?: boolean;
      isPurchaseUnit?: boolean;
      isSaleUnit?: boolean;
      barcode?: string;
      salePrice?: number;
      purchasePrice?: number;
    },
    createdBy: string,
  ) {
    try {
      const product = await prisma.product.findFirst({ where: { id: productId, businessId, active: true } });
      if (!product) throw new RpcException({ status: 404, message: 'Producto no encontrado' });

      return await prisma.productUnit.create({
        data: { productId, ...data, createdBy, active: true },
        include: { unit: { select: { id: true, name: true, abbreviation: true } } },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[productUnit.add] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al agregar unidad al producto' });
    }
  }

  async updateProductUnit(
    productUnitId: string,
    productId: string,
    businessId: string,
    data: Record<string, any>,
    updatedBy: string,
  ) {
    try {
      const product = await prisma.product.findFirst({ where: { id: productId, businessId, active: true } });
      if (!product) throw new RpcException({ status: 404, message: 'Producto no encontrado' });

      const existing = await prisma.productUnit.findFirst({ where: { id: productUnitId, productId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Unidad de producto no encontrada' });

      return await prisma.productUnit.update({
        where: { id: productUnitId },
        data: { ...data, updatedBy },
        include: { unit: { select: { id: true, name: true, abbreviation: true } } },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar unidad del producto' });
    }
  }

  async removeProductUnit(productUnitId: string, productId: string, businessId: string, updatedBy: string) {
    try {
      const product = await prisma.product.findFirst({ where: { id: productId, businessId, active: true } });
      if (!product) throw new RpcException({ status: 404, message: 'Producto no encontrado' });

      const existing = await prisma.productUnit.findFirst({ where: { id: productUnitId, productId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Unidad de producto no encontrada' });

      await prisma.productUnit.update({ where: { id: productUnitId }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar unidad del producto' });
    }
  }
}
