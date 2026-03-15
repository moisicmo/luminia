import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../../lib/prisma';

@Injectable()
export class CatalogService {
  private readonly logger = new Logger(CatalogService.name);

  constructor(private readonly prisma: PrismaService) {}

  listActivities(businessId: string) {
    this.logger.log(`[catalog.activities] businessId=${businessId}`);
    return this.prisma.activity.findMany({ where: { businessId } });
  }

  listMeasurementUnits(businessId: string) {
    this.logger.log(`[catalog.measurementUnits] businessId=${businessId}`);
    return this.prisma.measurementUnit.findMany({ where: { businessId } });
  }

  listProductServices(businessId: string) {
    this.logger.log(`[catalog.productServices] businessId=${businessId}`);
    return this.prisma.productService.findMany({ where: { businessId } });
  }

  listPaymentMethodTypes(businessId: string) {
    this.logger.log(`[catalog.paymentMethodTypes] businessId=${businessId}`);
    return this.prisma.paymentMethodType.findMany({ where: { businessId } });
  }

  listCurrencyTypes(businessId: string) {
    this.logger.log(`[catalog.currencyTypes] businessId=${businessId}`);
    return this.prisma.currencyType.findMany({ where: { businessId } });
  }

  listDocumentSectorTypes(businessId: string) {
    this.logger.log(`[catalog.documentSectorTypes] businessId=${businessId}`);
    return this.prisma.sectorDocumentType.findMany({ where: { businessId } });
  }

  listIdentityDocumentTypes(businessId: string) {
    this.logger.log(`[catalog.identityDocumentTypes] businessId=${businessId}`);
    return this.prisma.identityDocumentType.findMany({ where: { businessId } });
  }

  listCancellationReasons(businessId: string) {
    this.logger.log(`[catalog.cancellationReasons] businessId=${businessId}`);
    return this.prisma.cancellationReason.findMany({ where: { businessId } });
  }
}
