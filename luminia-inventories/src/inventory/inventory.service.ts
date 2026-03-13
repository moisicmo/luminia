import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class InventoryService {
  private readonly logger = new Logger(InventoryService.name);

  // ─── Warehouses ──────────────────────────────────────────────────────────────

  async createWarehouse(
    data: { businessId: string; branchId?: string; name: string; description?: string; address?: string; isDefault?: boolean },
    createdBy: string,
  ) {
    try {
      // Si es default, quitar default de los demás
      if (data.isDefault) {
        await prisma.warehouse.updateMany({
          where: { businessId: data.businessId, isDefault: true },
          data: { isDefault: false },
        });
      }
      return await prisma.warehouse.create({
        data: { ...data, createdBy, active: true },
      });
    } catch (err) {
      this.logger.error(`[warehouse.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear almacén' });
    }
  }

  async listWarehouses(businessId: string) {
    try {
      return await prisma.warehouse.findMany({
        where: { businessId, active: true },
        orderBy: { name: 'asc' },
      });
    } catch (err) {
      this.logger.error(`[warehouse.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar almacenes' });
    }
  }

  async updateWarehouse(id: string, businessId: string, data: Record<string, any>, updatedBy: string) {
    try {
      const existing = await prisma.warehouse.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Almacén no encontrado' });

      if (data.isDefault) {
        await prisma.warehouse.updateMany({
          where: { businessId, isDefault: true, id: { not: id } },
          data: { isDefault: false },
        });
      }

      return await prisma.warehouse.update({ where: { id }, data: { ...data, updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar almacén' });
    }
  }

  async removeWarehouse(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.warehouse.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Almacén no encontrado' });
      await prisma.warehouse.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar almacén' });
    }
  }

  // ─── Inputs (Entradas) ───────────────────────────────────────────────────────

  async createInput(
    data: {
      businessId: string;
      warehouseId: string;
      supplierId?: string;
      type: string;
      documentNumber?: string;
      date: string;
      notes?: string;
      details: {
        productId: string;
        unitId: string;
        quantity: number;
        unitCost: number;
      }[];
    },
    createdBy: string,
  ) {
    try {
      // Validar warehouse
      const warehouse = await prisma.warehouse.findFirst({
        where: { id: data.warehouseId, businessId: data.businessId, active: true },
      });
      if (!warehouse) throw new RpcException({ status: 404, message: 'Almacén no encontrado' });

      // Calcular totales
      const details = data.details.map((d) => ({
        ...d,
        totalCost: d.quantity * d.unitCost,
        createdBy,
      }));
      const subtotal = details.reduce((sum, d) => sum + d.totalCost, 0);

      const input = await prisma.input.create({
        data: {
          businessId: data.businessId,
          warehouseId: data.warehouseId,
          supplierId: data.supplierId || null,
          type: data.type as any,
          documentNumber: data.documentNumber,
          date: new Date(data.date),
          notes: data.notes,
          subtotal,
          total: subtotal,
          status: 'DRAFT',
          createdBy,
          details: {
            create: details,
          },
        },
        include: {
          supplier: { select: { id: true, name: true } },
          warehouse: { select: { id: true, name: true } },
          details: {
            include: {
              product: { select: { id: true, name: true, sku: true } },
              unit: { select: { id: true, name: true, abbreviation: true } },
            },
          },
        },
      });

      return input;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[input.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear entrada' });
    }
  }

  async listInputs(businessId: string, filters: { warehouseId?: string; type?: string; status?: string }) {
    try {
      const where: any = { businessId };
      if (filters.warehouseId) where.warehouseId = filters.warehouseId;
      if (filters.type) where.type = filters.type;
      if (filters.status) where.status = filters.status;

      return await prisma.input.findMany({
        where,
        include: {
          supplier: { select: { id: true, name: true } },
          warehouse: { select: { id: true, name: true } },
          _count: { select: { details: true } },
        },
        orderBy: { createdAt: 'desc' },
      });
    } catch (err) {
      this.logger.error(`[input.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar entradas' });
    }
  }

  async findInput(id: string, businessId: string) {
    try {
      const input = await prisma.input.findFirst({
        where: { id, businessId },
        include: {
          supplier: { select: { id: true, name: true } },
          warehouse: { select: { id: true, name: true } },
          details: {
            include: {
              product: { select: { id: true, name: true, sku: true } },
              unit: { select: { id: true, name: true, abbreviation: true } },
            },
          },
        },
      });
      if (!input) throw new RpcException({ status: 404, message: 'Entrada no encontrada' });
      return input;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al obtener entrada' });
    }
  }

  async confirmInput(id: string, businessId: string, updatedBy: string) {
    try {
      const input = await prisma.input.findFirst({
        where: { id, businessId, status: 'DRAFT' },
        include: { details: true },
      });
      if (!input) throw new RpcException({ status: 404, message: 'Entrada no encontrada o ya confirmada' });

      // Transacción: confirmar + actualizar stock + crear kardex
      return await prisma.$transaction(async (tx) => {
        // 1. Confirmar entrada
        const confirmed = await tx.input.update({
          where: { id },
          data: { status: 'CONFIRMED', updatedBy },
          include: {
            supplier: { select: { id: true, name: true } },
            warehouse: { select: { id: true, name: true } },
            details: {
              include: {
                product: { select: { id: true, name: true, sku: true } },
                unit: { select: { id: true, name: true, abbreviation: true } },
              },
            },
          },
        });

        // 2. Actualizar stock por cada detalle
        for (const detail of input.details) {
          const qty = Number(detail.quantity);
          const cost = Number(detail.unitCost);

          const existingStock = await tx.stock.findUnique({
            where: {
              productId_warehouseId_unitId: {
                productId: detail.productId,
                warehouseId: input.warehouseId,
                unitId: detail.unitId,
              },
            },
          });

          let newQty: number;
          let newAvgCost: number;

          if (existingStock) {
            const oldQty = Number(existingStock.quantity);
            const oldAvgCost = Number(existingStock.averageCost);
            newQty = oldQty + qty;
            newAvgCost = newQty > 0 ? ((oldQty * oldAvgCost) + (qty * cost)) / newQty : cost;

            await tx.stock.update({
              where: { id: existingStock.id },
              data: { quantity: newQty, averageCost: newAvgCost, updatedBy },
            });
          } else {
            newQty = qty;
            newAvgCost = cost;

            await tx.stock.create({
              data: {
                businessId: input.businessId,
                productId: detail.productId,
                warehouseId: input.warehouseId,
                unitId: detail.unitId,
                quantity: newQty,
                averageCost: newAvgCost,
                createdBy: updatedBy,
              },
            });
          }

          // 3. Crear registro kardex
          await tx.kardex.create({
            data: {
              businessId: input.businessId,
              productId: detail.productId,
              warehouseId: input.warehouseId,
              unitId: detail.unitId,
              movementDate: new Date(),
              movementType: 'IN',
              documentType: 'INPUT',
              documentId: input.id,
              documentNumber: input.documentNumber,
              quantity: qty,
              unitCost: cost,
              totalCost: qty * cost,
              balanceQty: newQty,
              balanceCost: newQty * newAvgCost,
              createdBy: updatedBy,
            },
          });
        }

        return confirmed;
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[input.confirm] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al confirmar entrada' });
    }
  }

  async cancelInput(id: string, businessId: string, updatedBy: string) {
    try {
      const input = await prisma.input.findFirst({ where: { id, businessId, status: 'DRAFT' } });
      if (!input) throw new RpcException({ status: 404, message: 'Entrada no encontrada o ya confirmada' });

      return await prisma.input.update({
        where: { id },
        data: { status: 'CANCELLED', updatedBy },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al cancelar entrada' });
    }
  }

  // ─── Customers ────────────────────────────────────────────────────────────────

  async createCustomer(
    data: { businessId: string; name: string; lastName?: string; taxId?: string; phone?: string; email?: string; address?: string },
    createdBy: string,
  ) {
    try {
      return await prisma.customer.create({ data: { ...data, createdBy, active: true } });
    } catch (err) {
      this.logger.error(`[customer.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear cliente' });
    }
  }

  async listCustomers(businessId: string) {
    try {
      return await prisma.customer.findMany({
        where: { businessId, active: true },
        orderBy: { name: 'asc' },
      });
    } catch (err) {
      this.logger.error(`[customer.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar clientes' });
    }
  }

  async updateCustomer(id: string, businessId: string, data: Record<string, any>, updatedBy: string) {
    try {
      const existing = await prisma.customer.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Cliente no encontrado' });
      return await prisma.customer.update({ where: { id }, data: { ...data, updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al actualizar cliente' });
    }
  }

  async removeCustomer(id: string, businessId: string, updatedBy: string) {
    try {
      const existing = await prisma.customer.findFirst({ where: { id, businessId, active: true } });
      if (!existing) throw new RpcException({ status: 404, message: 'Cliente no encontrado' });
      await prisma.customer.update({ where: { id }, data: { active: false, updatedBy } });
      return { removed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar cliente' });
    }
  }

  // ─── Outputs (Salidas / Ventas) ──────────────────────────────────────────────

  async createOutput(
    data: {
      businessId: string;
      warehouseId: string;
      customerId?: string;
      type: string;
      documentNumber?: string;
      date: string;
      notes?: string;
      details: { productId: string; unitId: string; quantity: number; unitCost: number; salePrice?: number }[];
    },
    createdBy: string,
  ) {
    try {
      const warehouse = await prisma.warehouse.findFirst({
        where: { id: data.warehouseId, businessId: data.businessId, active: true },
      });
      if (!warehouse) throw new RpcException({ status: 404, message: 'Almacén no encontrado' });

      const details = data.details.map((d) => ({
        ...d,
        totalCost: d.quantity * d.unitCost,
        createdBy,
      }));
      const subtotal = details.reduce((sum, d) => sum + (d.quantity * (d.salePrice ?? d.unitCost)), 0);

      return await prisma.output.create({
        data: {
          businessId: data.businessId,
          warehouseId: data.warehouseId,
          customerId: data.customerId || null,
          type: data.type as any,
          documentNumber: data.documentNumber,
          date: new Date(data.date),
          notes: data.notes,
          subtotal,
          total: subtotal,
          status: 'DRAFT',
          createdBy,
          details: { create: details },
        },
        include: {
          customer: { select: { id: true, name: true, lastName: true } },
          warehouse: { select: { id: true, name: true } },
          details: {
            include: {
              product: { select: { id: true, name: true, sku: true } },
              unit: { select: { id: true, name: true, abbreviation: true } },
            },
          },
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[output.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear salida' });
    }
  }

  async listOutputs(businessId: string, filters: { warehouseId?: string; type?: string; status?: string }) {
    try {
      const where: any = { businessId };
      if (filters.warehouseId) where.warehouseId = filters.warehouseId;
      if (filters.type) where.type = filters.type;
      if (filters.status) where.status = filters.status;

      return await prisma.output.findMany({
        where,
        include: {
          customer: { select: { id: true, name: true, lastName: true } },
          warehouse: { select: { id: true, name: true } },
          _count: { select: { details: true } },
        },
        orderBy: { createdAt: 'desc' },
      });
    } catch (err) {
      this.logger.error(`[output.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar salidas' });
    }
  }

  async confirmOutput(id: string, businessId: string, updatedBy: string) {
    try {
      const output = await prisma.output.findFirst({
        where: { id, businessId, status: 'DRAFT' },
        include: { details: true },
      });
      if (!output) throw new RpcException({ status: 404, message: 'Salida no encontrada o ya confirmada' });

      return await prisma.$transaction(async (tx) => {
        const confirmed = await tx.output.update({
          where: { id },
          data: { status: 'CONFIRMED', updatedBy },
          include: {
            customer: { select: { id: true, name: true, lastName: true } },
            warehouse: { select: { id: true, name: true } },
            details: {
              include: {
                product: { select: { id: true, name: true, sku: true } },
                unit: { select: { id: true, name: true, abbreviation: true } },
              },
            },
          },
        });

        for (const detail of output.details) {
          const qty = Number(detail.quantity);
          const cost = Number(detail.unitCost);

          const existingStock = await tx.stock.findUnique({
            where: {
              productId_warehouseId_unitId: {
                productId: detail.productId,
                warehouseId: output.warehouseId,
                unitId: detail.unitId,
              },
            },
          });

          if (!existingStock || Number(existingStock.quantity) < qty) {
            throw new RpcException({ status: 400, message: `Stock insuficiente para el producto ${detail.productId}` });
          }

          const newQty = Number(existingStock.quantity) - qty;
          const avgCost = Number(existingStock.averageCost);

          await tx.stock.update({
            where: { id: existingStock.id },
            data: { quantity: newQty, updatedBy },
          });

          await tx.kardex.create({
            data: {
              businessId: output.businessId,
              productId: detail.productId,
              warehouseId: output.warehouseId,
              unitId: detail.unitId,
              movementDate: new Date(),
              movementType: 'OUT',
              documentType: 'OUTPUT',
              documentId: output.id,
              documentNumber: output.documentNumber,
              quantity: qty,
              unitCost: cost,
              totalCost: qty * cost,
              balanceQty: newQty,
              balanceCost: newQty * avgCost,
              createdBy: updatedBy,
            },
          });
        }

        return confirmed;
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[output.confirm] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al confirmar salida' });
    }
  }

  async cancelOutput(id: string, businessId: string, updatedBy: string) {
    try {
      const output = await prisma.output.findFirst({ where: { id, businessId, status: 'DRAFT' } });
      if (!output) throw new RpcException({ status: 404, message: 'Salida no encontrada o ya confirmada' });
      return await prisma.output.update({ where: { id }, data: { status: 'CANCELLED', updatedBy } });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al cancelar salida' });
    }
  }

  // ─── Stock ────────────────────────────────────────────────────────────────────

  async listStock(businessId: string, filters: { warehouseId?: string; productId?: string }) {
    try {
      const where: any = { businessId };
      if (filters.warehouseId) where.warehouseId = filters.warehouseId;
      if (filters.productId) where.productId = filters.productId;

      return await prisma.stock.findMany({
        where,
        include: {
          product: { select: { id: true, name: true, sku: true, minimumStock: true } },
          warehouse: { select: { id: true, name: true } },
          unit: { select: { id: true, name: true, abbreviation: true } },
        },
        orderBy: { product: { name: 'asc' } },
      });
    } catch (err) {
      this.logger.error(`[stock.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar stock' });
    }
  }

  // ─── Kardex ───────────────────────────────────────────────────────────────────

  async listKardex(businessId: string, filters: { productId?: string; warehouseId?: string }) {
    try {
      const where: any = { businessId };
      if (filters.productId) where.productId = filters.productId;
      if (filters.warehouseId) where.warehouseId = filters.warehouseId;

      return await prisma.kardex.findMany({
        where,
        include: {
          product: { select: { id: true, name: true, sku: true } },
          warehouse: { select: { id: true, name: true } },
          unit: { select: { id: true, name: true, abbreviation: true } },
        },
        orderBy: { movementDate: 'desc' },
        take: 200,
      });
    } catch (err) {
      this.logger.error(`[kardex.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar kardex' });
    }
  }
}
