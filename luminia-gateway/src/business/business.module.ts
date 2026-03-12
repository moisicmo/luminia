import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceBusinesses } from '@/config';
import { BusinessController } from './business.controller';
import { BusinessService } from './business.service';

@Module({
  imports: [
    ClientsModule.register([
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
  controllers: [BusinessController],
  providers: [BusinessService],
})
export class BusinessModule {}
