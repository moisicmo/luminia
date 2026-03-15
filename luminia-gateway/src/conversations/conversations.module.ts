import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { envs, RMQServiceConversations } from '@/config';
import { ConversationsController } from './conversations.controller';
import { WebhooksController } from './webhooks.controller';
import { ConversationsService } from './conversations.service';

@Module({
  imports: [
    ClientsModule.register([
      {
        name: RMQServiceConversations.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceConversations.getQueueName(),
          queueOptions: { durable: true },
        },
      },
    ]),
  ],
  controllers: [ConversationsController, WebhooksController],
  providers: [ConversationsService],
})
export class ConversationsModule {}
