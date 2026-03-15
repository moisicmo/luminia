import { Injectable, Inject, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout } from 'rxjs';
import { RMQServiceOrders } from '@/config';
import type { ToolDefinition } from '@/ai-providers/interfaces/ai-provider.interface';

const RMQ_TIMEOUT = 15_000;

@Injectable()
export class CreateOrderTool {
  private readonly logger = new Logger(CreateOrderTool.name);

  constructor(
    @Inject(RMQServiceOrders.getName())
    private readonly ordersClient: ClientProxy,
  ) {}

  get definition(): ToolDefinition {
    return {
      type: 'function',
      function: {
        name: 'createOrder',
        description: 'Crear un pedido de venta para el cliente. Usar cuando el cliente confirma que quiere comprar productos.',
        parameters: {
          type: 'object',
          properties: {
            customerName: { type: 'string', description: 'Nombre del cliente' },
            items: {
              type: 'array',
              description: 'Lista de productos a pedir',
              items: {
                type: 'object',
                properties: {
                  name: { type: 'string', description: 'Nombre del producto' },
                  quantity: { type: 'number', description: 'Cantidad' },
                  unitPrice: { type: 'number', description: 'Precio unitario' },
                },
                required: ['name', 'quantity', 'unitPrice'],
              },
            },
            notes: { type: 'string', description: 'Notas del pedido' },
          },
          required: ['items'],
        },
      },
    };
  }

  async execute(
    args: { customerName?: string; items: any[]; notes?: string },
    businessId: string,
  ): Promise<any> {
    try {
      const result = await firstValueFrom(
        this.ordersClient
          .send('orders.create', {
            data: {
              businessId,
              customerName: args.customerName ?? 'Cliente conversacional',
              items: args.items,
              notes: args.notes,
              source: 'CONVERSATION',
            },
            createdBy: 'AI_AGENT',
          })
          .pipe(timeout(RMQ_TIMEOUT)),
      );
      return {
        orderId: result.id,
        status: result.status,
        total: result.total,
        message: 'Pedido creado exitosamente',
      };
    } catch (err) {
      this.logger.error(`createOrder error: ${(err as Error).message}`);
      return { error: 'No se pudo crear el pedido en este momento. Intente nuevamente.' };
    }
  }
}
