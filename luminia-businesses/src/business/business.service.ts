import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';
import { CreateBusinessDto } from './dto/create-business.dto';
import { RoleService } from './role.service';

@Injectable()
export class BusinessService {
  private readonly logger = new Logger(BusinessService.name);

  constructor(private readonly roleService: RoleService) {}

  async create(dto: CreateBusinessDto, ownerId: string) {
    this.logger.log(`[business.create] ownerId=${ownerId} name="${dto.name}"`);
    try {
      // Create business + first branch in a transaction
      const result = await prisma.$transaction(async (tx) => {
        const business = await tx.business.create({
          data: {
            ownerId,
            name: dto.name,
            businessType: dto.businessType as any,
            taxId: dto.taxId,
            url: dto.url,
            active: true,
            createdBy: ownerId,
          },
        });

        const branch = await tx.branch.create({
          data: {
            businessId: business.id,
            name: dto.branch.name,
            region: dto.branch.region,
            address: dto.branch.address,
            municipality: dto.branch.municipality,
            phone: dto.branch.phone,
            latitude: dto.branch.latitude,
            longitude: dto.branch.longitude,
            openingHours: dto.branch.openingHours,
            active: true,
            createdBy: ownerId,
          },
        });

        // Create a default point of sale for the first branch
        const pointOfSale = await tx.pointOfSale.create({
          data: {
            branchId: branch.id,
            name: 'Caja Principal',
            active: true,
            createdBy: ownerId,
          },
        });

        return { business, branch, pointOfSale };
      });

      // Create default roles (Administrador, Cajero, Vendedor) for the new business
      await this.roleService.createDefaults(result.business.id, ownerId);

      this.logger.log(`[business.create] negocio creado: ${result.business.id}`);
      return {
        businessId: result.business.id,
        branchId: result.branch.id,
        pointOfSaleId: result.pointOfSale.id,
        name: result.business.name,
        businessType: result.business.businessType,
      };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[business.create] error: ${(err as Error).message}`, (err as Error).stack);
      throw new RpcException({ status: 500, message: 'Error al crear el negocio' });
    }
  }

  async findByOwner(ownerId: string) {
    this.logger.log(`[business.list] ownerId=${ownerId}`);
    try {
      const businesses = await prisma.business.findMany({
        where: { ownerId, active: true },
        include: {
          branches: {
            where: { active: true },
            include: { pointsOfSale: { where: { active: true } } },
          },
        },
        orderBy: { createdAt: 'desc' },
      });
      return businesses;
    } catch (err) {
      this.logger.error(`[business.list] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener los negocios' });
    }
  }

  async findPublic() {
    this.logger.log(`[business.listPublic]`);
    try {
      const businesses = await prisma.business.findMany({
        where: { active: true },
        select: {
          id: true,
          name: true,
          businessType: true,
          url: true,
          logo: true,
          branches: {
            where: { active: true },
            select: { region: true, municipality: true, address: true, phone: true },
            take: 1,
          },
        },
        orderBy: { createdAt: 'desc' },
      });
      return businesses.map((b) => ({
        id: b.id,
        name: b.name,
        businessType: b.businessType,
        url: b.url,
        logo: b.logo,
        city: b.branches[0]?.region ?? null,
        address: b.branches[0]?.address ?? null,
        phone: b.branches[0]?.phone ?? null,
      }));
    } catch (err) {
      this.logger.error(`[business.listPublic] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener los negocios' });
    }
  }

  async findByUrl(url: string) {
    this.logger.log(`[business.findByUrl] url=${url}`);
    try {
      const business = await prisma.business.findFirst({
        where: { url, active: true },
        include: {
          systemAssignments: { select: { systemId: true } },
        },
      });

      if (!business) {
        throw new RpcException({ status: 404, message: 'Negocio no encontrado' });
      }

      return {
        businessId: business.id,
        name: business.name,
        url: business.url,
        systemId: business.systemAssignments[0]?.systemId ?? null,
      };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[business.findByUrl] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al buscar el negocio' });
    }
  }

  async findOne(businessId: string, requesterId: string) {
    this.logger.log(`[business.findOne] businessId=${businessId} requesterId=${requesterId}`);
    try {
      const business = await prisma.business.findFirst({
        where: {
          id: businessId,
          active: true,
          // Only owner can see it (extend later with member roles)
          ownerId: requesterId,
        },
        include: {
          branches: {
            where: { active: true },
            include: { pointsOfSale: { where: { active: true } } },
          },
        },
      });

      if (!business) {
        throw new RpcException({ status: 404, message: 'Negocio no encontrado' });
      }

      return business;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[business.findOne] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener el negocio' });
    }
  }
}
