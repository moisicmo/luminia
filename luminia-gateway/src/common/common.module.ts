import { Global, Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceBusinesses } from '@/config';
import { BusinessGuard } from './guards/business.guard';

@Global()
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
  providers: [BusinessGuard],
  exports: [BusinessGuard, ClientsModule],
})
export class CommonModule {}
