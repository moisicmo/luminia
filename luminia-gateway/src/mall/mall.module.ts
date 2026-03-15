import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceInventories, RMQServiceBusinesses } from '@/config';
import { MallController } from './mall.controller';
import { MallService } from './mall.service';

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
        name: RMQServiceBusinesses.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceBusinesses.getQueueName(),
          queueOptions: { durable: true },
        },
      },
    ]),
  ],
  controllers: [MallController],
  providers: [MallService],
})
export class MallModule {}
