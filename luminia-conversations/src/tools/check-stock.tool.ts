import { Injectable, Inject, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout } from 'rxjs';
import { RMQServiceInventories } from '@/config';
import type { ToolDefinition } from '@/ai-providers/interfaces/ai-provider.interface';

const RMQ_TIMEOUT = 10_000;

@Injectable()
export class CheckStockTool {
  private readonly logger = new Logger(CheckStockTool.name);

  constructor(
    @Inject(RMQServiceInventories.getName())
    private readonly inventoriesClient: ClientProxy,
  ) {}

  get definition(): ToolDefinition {
    return {
      type: 'function',
      function: {
        name: 'checkStock',
        description: 'Verificar el stock/disponibilidad de un producto específico. Retorna la cantidad disponible por almacén.',
        parameters: {
          type: 'object',
          properties: {
            productId: { type: 'string', description: 'UUID del producto a verificar' },
          },
          required: ['productId'],
        },
      },
    };
  }

  async execute(args: { productId: string }, businessId: string): Promise<any> {
    try {
      const result = await firstValueFrom(
        this.inventoriesClient
          .send('store.stock.list', { businessId, filters: { productId: args.productId } })
          .pipe(timeout(RMQ_TIMEOUT)),
      );
      const stocks = Array.isArray(result) ? result : [];
      return stocks.map((s: any) => ({
        productName: s.product?.name,
        warehouse: s.warehouse?.name,
        quantity: s.quantity,
        unit: s.unit?.abbreviation ?? s.unit?.name,
      }));
    } catch (err) {
      this.logger.error(`checkStock error: ${(err as Error).message}`);
      return { error: 'No se pudo verificar el stock en este momento' };
    }
  }
}
