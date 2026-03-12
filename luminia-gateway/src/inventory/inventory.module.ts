import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceInventories } from '@/config';
import { InventoryController } from './inventory.controller';
import { InventoryService } from './inventory.service';

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
    ]),
  ],
  controllers: [InventoryController],
  providers: [InventoryService],
})
export class InventoryModule {}
