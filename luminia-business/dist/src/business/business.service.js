"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var BusinessService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.BusinessService = void 0;
const common_1 = require("@nestjs/common");
const microservices_1 = require("@nestjs/microservices");
const prisma_1 = require("@/lib/prisma");
let BusinessService = BusinessService_1 = class BusinessService {
    logger = new common_1.Logger(BusinessService_1.name);
    async create(dto, ownerId) {
        this.logger.log(`[business.create] ownerId=${ownerId} name="${dto.name}"`);
        try {
            const result = await prisma_1.prisma.$transaction(async (tx) => {
                const business = await tx.business.create({
                    data: {
                        ownerId,
                        name: dto.name,
                        businessType: dto.businessType,
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
            this.logger.log(`[business.create] negocio creado: ${result.business.id}`);
            return {
                businessId: result.business.id,
                branchId: result.branch.id,
                pointOfSaleId: result.pointOfSale.id,
                name: result.business.name,
                businessType: result.business.businessType,
            };
        }
        catch (err) {
            if (err instanceof microservices_1.RpcException)
                throw err;
            this.logger.error(`[business.create] error: ${err.message}`, err.stack);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al crear el negocio' });
        }
    }
    async findByOwner(ownerId) {
        this.logger.log(`[business.list] ownerId=${ownerId}`);
        try {
            const businesses = await prisma_1.prisma.business.findMany({
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
        }
        catch (err) {
            this.logger.error(`[business.list] error: ${err.message}`);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al obtener los negocios' });
        }
    }
    async findOne(businessId, requesterId) {
        this.logger.log(`[business.findOne] businessId=${businessId} requesterId=${requesterId}`);
        try {
            const business = await prisma_1.prisma.business.findFirst({
                where: {
                    id: businessId,
                    active: true,
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
                throw new microservices_1.RpcException({ status: 404, message: 'Negocio no encontrado' });
            }
            return business;
        }
        catch (err) {
            if (err instanceof microservices_1.RpcException)
                throw err;
            this.logger.error(`[business.findOne] error: ${err.message}`);
            throw new microservices_1.RpcException({ status: 500, message: 'Error al obtener el negocio' });
        }
    }
};
exports.BusinessService = BusinessService;
exports.BusinessService = BusinessService = BusinessService_1 = __decorate([
    (0, common_1.Injectable)()
], BusinessService);
//# sourceMappingURL=business.service.js.map