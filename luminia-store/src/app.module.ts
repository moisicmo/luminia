import { Module } from '@nestjs/common';
import { CatalogModule } from './catalog/catalog.module';
import { ProductModule } from './product/product.module';

@Module({
  imports: [CatalogModule, ProductModule],
})
export class AppModule {}
