import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';
import type { PaperSize } from '@/generated/prisma/client';

@Injectable()
export class PointOfSaleService {
  private readonly logger = new Logger(PointOfSaleService.name);

  async list(branchId: string) {
    this.logger.log(`[pos.list] branchId=${branchId}`);
    try {
      return await prisma.pointOfSale.findMany({
        where: { branchId, active: true },
        orderBy: { createdAt: 'asc' },
      });
    } catch (err) {
      this.logger.error(`[pos.list] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar puntos de venta' });
    }
  }

  async create(dto: { name: string; paperSize?: PaperSize }, branchId: string, createdBy: string) {
    this.logger.log(`[pos.create] branchId=${branchId} name="${dto.name}"`);
    try {
      const branch = await prisma.branch.findFirst({ where: { id: branchId, active: true } });
      if (!branch) {
        throw new RpcException({ status: 404, message: 'Sucursal no encontrada' });
      }
      return await prisma.pointOfSale.create({
        data: {
          branchId,
          name: dto.name,
          paperSize: (dto.paperSize as any) ?? 'LETTER',
          active: true,
          createdBy,
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[pos.create] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear punto de venta' });
    }
  }

  async update(id: string, dto: { name?: string; paperSize?: PaperSize }, updatedBy: string) {
    this.logger.log(`[pos.update] id=${id}`);
    try {
      const pos = await prisma.pointOfSale.findFirst({ where: { id, active: true } });
      if (!pos) {
        throw new RpcException({ status: 404, message: 'Punto de venta no encontrado' });
      }
      return await prisma.pointOfSale.update({
        where: { id },
        data: { ...dto, updatedBy },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[pos.update] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar punto de venta' });
    }
  }

  async deactivate(id: string, updatedBy: string) {
    this.logger.log(`[pos.deactivate] id=${id}`);
    try {
      const pos = await prisma.pointOfSale.findFirst({ where: { id, active: true } });
      if (!pos) {
        throw new RpcException({ status: 404, message: 'Punto de venta no encontrado' });
      }
      await prisma.pointOfSale.update({
        where: { id },
        data: { active: false, updatedBy },
      });
      return { success: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[pos.deactivate] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al desactivar punto de venta' });
    }
  }
}
