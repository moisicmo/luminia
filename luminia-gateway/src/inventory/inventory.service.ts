import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceInventories } from '@/config';

const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class InventoryService {
  private readonly logger = new Logger(InventoryService.name);

  constructor(
    @Inject(RMQServiceInventories.getName())
    private readonly inventoryClient: ClientProxy,
  ) {}

  private async send<T>(pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] enviando → ${pattern}`);
    try {
      const result = await firstValueFrom(
        this.inventoryClient.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      this.logger.log(`[RMQ] respuesta OK ← ${pattern}`);
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        this.logger.error(`[RMQ] TIMEOUT: ${pattern}`);
        throw new HttpException(
          `El servicio no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      this.logger.error(`[RMQ] ERROR en ${pattern}: ${JSON.stringify(err)}`);
      throw err;
    }
  }

  // ─── Categories ─────────────────────────────────────────────────────────────

  createCategory(businessId: string, data: any, createdBy: string) {
    return this.send('store.categories.create', { data: { ...data, businessId }, createdBy });
  }

  listCategories(businessId: string) {
    return this.send('store.categories.list', { businessId });
  }

  updateCategory(id: string, businessId: string, data: any, updatedBy: string) {
    return this.send('store.categories.update', { id, businessId, data, updatedBy });
  }

  removeCategory(id: string, businessId: string, updatedBy: string) {
    return this.send('store.categories.remove', { id, businessId, updatedBy });
  }

  // ─── Brands ─────────────────────────────────────────────────────────────────

  createBrand(businessId: string, data: any, createdBy: string) {
    return this.send('store.brands.create', { data: { ...data, businessId }, createdBy });
  }

  listBrands(businessId: string) {
    return this.send('store.brands.list', { businessId });
  }

  updateBrand(id: string, businessId: string, data: any, updatedBy: string) {
    return this.send('store.brands.update', { id, businessId, data, updatedBy });
  }

  removeBrand(id: string, businessId: string, updatedBy: string) {
    return this.send('store.brands.remove', { id, businessId, updatedBy });
  }

  // ─── Suppliers ───────────────────────────────────────────────────────────────

  createSupplier(businessId: string, data: any, createdBy: string) {
    return this.send('store.suppliers.create', { data: { ...data, businessId }, createdBy });
  }

  listSuppliers(businessId: string) {
    return this.send('store.suppliers.list', { businessId });
  }

  updateSupplier(id: string, businessId: string, data: any, updatedBy: string) {
    return this.send('store.suppliers.update', { id, businessId, data, updatedBy });
  }

  removeSupplier(id: string, businessId: string, updatedBy: string) {
    return this.send('store.suppliers.remove', { id, businessId, updatedBy });
  }

  // ─── Units ───────────────────────────────────────────────────────────────────

  createUnit(businessId: string, data: any, createdBy: string) {
    return this.send('store.units.create', { data: { ...data, businessId }, createdBy });
  }

  listUnits(businessId: string) {
    return this.send('store.units.list', { businessId });
  }

  updateUnit(id: string, businessId: string, data: any, updatedBy: string) {
    return this.send('store.units.update', { id, businessId, data, updatedBy });
  }

  // ─── Products ────────────────────────────────────────────────────────────────

  createProduct(businessId: string, data: any, createdBy: string) {
    return this.send('store.products.create', { data: { ...data, businessId }, createdBy });
  }

  listProducts(businessId: string, filters: any) {
    return this.send('store.products.list', { businessId, filters });
  }

  findProduct(id: string, businessId: string) {
    return this.send('store.products.findOne', { id, businessId });
  }

  updateProduct(id: string, businessId: string, data: any, updatedBy: string) {
    return this.send('store.products.update', { id, businessId, data, updatedBy });
  }

  removeProduct(id: string, businessId: string, updatedBy: string) {
    return this.send('store.products.remove', { id, businessId, updatedBy });
  }

  // ─── Product Units ────────────────────────────────────────────────────────────

  addProductUnit(productId: string, businessId: string, data: any, createdBy: string) {
    return this.send('store.products.units.add', { productId, businessId, data, createdBy });
  }

  updateProductUnit(productUnitId: string, productId: string, businessId: string, data: any, updatedBy: string) {
    return this.send('store.products.units.update', { productUnitId, productId, businessId, data, updatedBy });
  }

  removeProductUnit(productUnitId: string, productId: string, businessId: string, updatedBy: string) {
    return this.send('store.products.units.remove', { productUnitId, productId, businessId, updatedBy });
  }
}
