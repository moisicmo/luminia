import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceStores } from '@/config';
import { StoreController } from './store.controller';
import { StoreService } from './store.service';

@Module({
  imports: [
    ClientsModule.register([
      {
        name: RMQServiceStores.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceStores.getQueueName(),
          queueOptions: { durable: true },
        },
      },
    ]),
  ],
  controllers: [StoreController],
  providers: [StoreService],
})
export class StoreModule {}
