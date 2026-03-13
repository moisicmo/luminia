import { Module } from '@nestjs/common';
import { CatalogModule } from './catalog/catalog.module';
import { ProductModule } from './product/product.module';
import { InventoryModule } from './inventory/inventory.module';

@Module({
  imports: [CatalogModule, ProductModule, InventoryModule],
})
export class AppModule {}
