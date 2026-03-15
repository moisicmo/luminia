import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class BranchService {
  private readonly logger = new Logger(BranchService.name);

  async list(businessId: string) {
    this.logger.log(`[branches.list] businessId=${businessId}`);
    try {
      return await prisma.branch.findMany({
        where: { businessId, active: true },
        include: {
          pointsOfSale: { where: { active: true }, orderBy: { createdAt: 'asc' } },
        },
        orderBy: { createdAt: 'asc' },
      });
    } catch (err) {
      this.logger.error(`[branches.list] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar sucursales' });
    }
  }

  async findOne(id: string, businessId: string) {
    this.logger.log(`[branches.findOne] id=${id}`);
    try {
      const branch = await prisma.branch.findFirst({
        where: { id, businessId, active: true },
        include: {
          pointsOfSale: { where: { active: true }, orderBy: { createdAt: 'asc' } },
        },
      });
      if (!branch) {
        throw new RpcException({ status: 404, message: 'Sucursal no encontrada' });
      }
      return branch;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[branches.findOne] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener la sucursal' });
    }
  }

  async create(
    dto: { name: string; region: string; address?: string; municipality?: string; phone?: string; latitude?: number; longitude?: number; openingHours?: string },
    businessId: string,
    createdBy: string,
  ) {
    this.logger.log(`[branches.create] businessId=${businessId} name="${dto.name}"`);
    try {
      return await prisma.$transaction(async (tx) => {
        const branch = await tx.branch.create({
          data: {
            businessId,
            name: dto.name,
            region: dto.region,
            address: dto.address,
            municipality: dto.municipality,
            phone: dto.phone,
            latitude: dto.latitude,
            longitude: dto.longitude,
            openingHours: dto.openingHours,
            active: true,
            createdBy,
          },
        });

        const pos = await tx.pointOfSale.create({
          data: {
            branchId: branch.id,
            name: 'Caja Principal',
            active: true,
            createdBy,
          },
        });

        return { ...branch, pointsOfSale: [pos] };
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[branches.create] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al crear la sucursal' });
    }
  }

  async update(
    id: string,
    dto: { name?: string; region?: string; address?: string; municipality?: string; phone?: string; latitude?: number; longitude?: number; openingHours?: string },
    businessId: string,
    updatedBy: string,
  ) {
    this.logger.log(`[branches.update] id=${id}`);
    try {
      const branch = await prisma.branch.findFirst({ where: { id, businessId, active: true } });
      if (!branch) {
        throw new RpcException({ status: 404, message: 'Sucursal no encontrada' });
      }
      return await prisma.branch.update({
        where: { id },
        data: { ...dto, updatedBy },
        include: {
          pointsOfSale: { where: { active: true }, orderBy: { createdAt: 'asc' } },
        },
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[branches.update] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al actualizar la sucursal' });
    }
  }

  async deactivate(id: string, businessId: string, updatedBy: string) {
    this.logger.log(`[branches.deactivate] id=${id}`);
    try {
      const branch = await prisma.branch.findFirst({ where: { id, businessId, active: true } });
      if (!branch) {
        throw new RpcException({ status: 404, message: 'Sucursal no encontrada' });
      }
      await prisma.branch.update({
        where: { id },
        data: { active: false, updatedBy },
      });
      return { success: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[branches.deactivate] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al desactivar la sucursal' });
    }
  }
}
