import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { envs, RMQServiceAuthentications, RMQServiceUsers } from '@/config';

@Module({
  imports: [
    ClientsModule.register([
      {
        name: RMQServiceAuthentications.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceAuthentications.getQueueName(),
          queueOptions: { durable: true },
        },
      },
      {
        name: RMQServiceUsers.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceUsers.getQueueName(),
          queueOptions: { durable: true },
        },
      },
    ]),
  ],
  controllers: [AuthController],
  providers: [AuthService],
})
export class AuthModule {}
