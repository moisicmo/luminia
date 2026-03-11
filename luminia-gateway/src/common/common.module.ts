import { Global, Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceBusiness } from '@/config';
import { BusinessGuard } from './guards/business.guard';

@Global()
@Module({
  imports: [
    ClientsModule.register([
      {
        name: RMQServiceBusiness.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceBusiness.getQueueName(),
          queueOptions: { durable: true },
        },
      },
    ]),
  ],
  providers: [BusinessGuard],
  exports: [BusinessGuard, ClientsModule],
})
export class CommonModule {}
