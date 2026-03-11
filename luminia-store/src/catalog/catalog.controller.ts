import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { CatalogService } from './catalog.service';

@Controller()
export class CatalogController {
  constructor(private readonly catalogService: CatalogService) {}

  // ─── Categories ───────────────────────────────────────────────────────────

  @MessagePattern('store.categories.create')
  createCategory(@Payload() d: { data: any; createdBy: string }) {
    return this.catalogService.createCategory(d.data, d.createdBy);
  }

  @MessagePattern('store.categories.list')
  listCategories(@Payload() d: { businessId: string }) {
    return this.catalogService.listCategories(d.businessId);
  }

  @MessagePattern('store.categories.update')
  updateCategory(@Payload() d: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.catalogService.updateCategory(d.id, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('store.categories.remove')
  removeCategory(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.catalogService.removeCategory(d.id, d.businessId, d.updatedBy);
  }

  // ─── Brands ───────────────────────────────────────────────────────────────

  @MessagePattern('store.brands.create')
  createBrand(@Payload() d: { data: any; createdBy: string }) {
    return this.catalogService.createBrand(d.data, d.createdBy);
  }

  @MessagePattern('store.brands.list')
  listBrands(@Payload() d: { businessId: string }) {
    return this.catalogService.listBrands(d.businessId);
  }

  @MessagePattern('store.brands.update')
  updateBrand(@Payload() d: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.catalogService.updateBrand(d.id, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('store.brands.remove')
  removeBrand(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.catalogService.removeBrand(d.id, d.businessId, d.updatedBy);
  }

  // ─── Suppliers ────────────────────────────────────────────────────────────

  @MessagePattern('store.suppliers.create')
  createSupplier(@Payload() d: { data: any; createdBy: string }) {
    return this.catalogService.createSupplier(d.data, d.createdBy);
  }

  @MessagePattern('store.suppliers.list')
  listSuppliers(@Payload() d: { businessId: string }) {
    return this.catalogService.listSuppliers(d.businessId);
  }

  @MessagePattern('store.suppliers.update')
  updateSupplier(@Payload() d: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.catalogService.updateSupplier(d.id, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('store.suppliers.remove')
  removeSupplier(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.catalogService.removeSupplier(d.id, d.businessId, d.updatedBy);
  }

  // ─── Units ────────────────────────────────────────────────────────────────

  @MessagePattern('store.units.create')
  createUnit(@Payload() d: { data: any; createdBy: string }) {
    return this.catalogService.createUnit(d.data, d.createdBy);
  }

  @MessagePattern('store.units.list')
  listUnits(@Payload() d: { businessId: string }) {
    return this.catalogService.listUnits(d.businessId);
  }

  @MessagePattern('store.units.update')
  updateUnit(@Payload() d: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.catalogService.updateUnit(d.id, d.businessId, d.data, d.updatedBy);
  }
}
