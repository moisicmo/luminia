import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { CatalogService } from './catalog.service';

@Controller()
export class CatalogController {
  constructor(private readonly service: CatalogService) {}

  @MessagePattern('catalog.activities')
  listActivities(@Payload() data: { businessId: string }) {
    return this.service.listActivities(data.businessId);
  }

  @MessagePattern('catalog.measurementUnits')
  listMeasurementUnits(@Payload() data: { businessId: string }) {
    return this.service.listMeasurementUnits(data.businessId);
  }

  @MessagePattern('catalog.productServices')
  listProductServices(@Payload() data: { businessId: string }) {
    return this.service.listProductServices(data.businessId);
  }

  @MessagePattern('catalog.paymentMethodTypes')
  listPaymentMethodTypes(@Payload() data: { businessId: string }) {
    return this.service.listPaymentMethodTypes(data.businessId);
  }

  @MessagePattern('catalog.currencyTypes')
  listCurrencyTypes(@Payload() data: { businessId: string }) {
    return this.service.listCurrencyTypes(data.businessId);
  }

  @MessagePattern('catalog.documentSectorTypes')
  listDocumentSectorTypes(@Payload() data: { businessId: string }) {
    return this.service.listDocumentSectorTypes(data.businessId);
  }

  @MessagePattern('catalog.identityDocumentTypes')
  listIdentityDocumentTypes(@Payload() data: { businessId: string }) {
    return this.service.listIdentityDocumentTypes(data.businessId);
  }

  @MessagePattern('catalog.cancellationReasons')
  listCancellationReasons(@Payload() data: { businessId: string }) {
    return this.service.listCancellationReasons(data.businessId);
  }
}
