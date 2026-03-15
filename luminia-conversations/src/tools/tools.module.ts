import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceInventories, RMQServiceOrders } from '@/config';
import { SearchProductsTool } from './search-products.tool';
import { CheckStockTool } from './check-stock.tool';
import { CreateOrderTool } from './create-order.tool';
import { ToolRegistryService } from './tool-registry.service';

@Module({
  imports: [
    ClientsModule.register([
      {
        name: RMQServiceInventories.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceInventories.getQueueName(),
          queueOptions: { durable: true },
        },
      },
      {
        name: RMQServiceOrders.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceOrders.getQueueName(),
          queueOptions: { durable: true },
        },
      },
    ]),
  ],
  providers: [SearchProductsTool, CheckStockTool, CreateOrderTool, ToolRegistryService],
  exports: [ToolRegistryService],
})
export class ToolsModule {}
