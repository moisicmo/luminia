import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { ProductService } from './product.service';

@Controller()
export class ProductController {
  constructor(private readonly productService: ProductService) {}

  // ─── Products ─────────────────────────────────────────────────────────────

  @MessagePattern('store.products.create')
  createProduct(@Payload() d: { data: any; createdBy: string }) {
    return this.productService.createProduct(d.data, d.createdBy);
  }

  @MessagePattern('store.products.list')
  listProducts(@Payload() d: { businessId: string; filters?: { categoryId?: string; brandId?: string; search?: string } }) {
    return this.productService.listProducts(d.businessId, d.filters ?? {});
  }

  @MessagePattern('store.products.findOne')
  findProduct(@Payload() d: { id: string; businessId: string }) {
    return this.productService.findProduct(d.id, d.businessId);
  }

  @MessagePattern('store.products.update')
  updateProduct(@Payload() d: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.productService.updateProduct(d.id, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('store.products.remove')
  removeProduct(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.productService.removeProduct(d.id, d.businessId, d.updatedBy);
  }

  // ─── Product Units ─────────────────────────────────────────────────────────

  @MessagePattern('store.products.units.add')
  addProductUnit(@Payload() d: { productId: string; businessId: string; data: any; createdBy: string }) {
    return this.productService.addProductUnit(d.productId, d.businessId, d.data, d.createdBy);
  }

  @MessagePattern('store.products.units.update')
  updateProductUnit(@Payload() d: { productUnitId: string; productId: string; businessId: string; data: any; updatedBy: string }) {
    return this.productService.updateProductUnit(d.productUnitId, d.productId, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('store.products.units.remove')
  removeProductUnit(@Payload() d: { productUnitId: string; productId: string; businessId: string; updatedBy: string }) {
    return this.productService.removeProductUnit(d.productUnitId, d.productId, d.businessId, d.updatedBy);
  }
}
