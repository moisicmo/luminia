import { Injectable, Inject, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout } from 'rxjs';
import { RMQServiceInventories } from '@/config';
import type { ToolDefinition } from '@/ai-providers/interfaces/ai-provider.interface';

const RMQ_TIMEOUT = 10_000;

@Injectable()
export class SearchProductsTool {
  private readonly logger = new Logger(SearchProductsTool.name);

  constructor(
    @Inject(RMQServiceInventories.getName())
    private readonly inventoriesClient: ClientProxy,
  ) {}

  get definition(): ToolDefinition {
    return {
      type: 'function',
      function: {
        name: 'searchProducts',
        description: 'Buscar productos en el catálogo del negocio por nombre, categoría o palabra clave. Retorna nombre, precio, descripción y disponibilidad.',
        parameters: {
          type: 'object',
          properties: {
            search: { type: 'string', description: 'Término de búsqueda (nombre o palabra clave del producto)' },
            categoryId: { type: 'string', description: 'UUID de categoría para filtrar (opcional)' },
          },
          required: ['search'],
        },
      },
    };
  }

  async execute(args: { search: string; categoryId?: string }, businessId: string): Promise<any> {
    try {
      const result = await firstValueFrom(
        this.inventoriesClient
          .send('store.products.list', { businessId, filters: { search: args.search, categoryId: args.categoryId } })
          .pipe(timeout(RMQ_TIMEOUT)),
      );
      const products = Array.isArray(result) ? result : [];
      return products.map((p: any) => ({
        id: p.id,
        name: p.name,
        description: p.description,
        price: p.salePrice,
        sku: p.sku,
        category: p.category?.name,
        imageUrl: p.imageUrl,
      }));
    } catch (err) {
      this.logger.error(`searchProducts error: ${(err as Error).message}`);
      return { error: 'No se pudieron buscar los productos en este momento' };
    }
  }
}
