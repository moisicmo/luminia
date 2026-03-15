import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { ConversationService } from './conversation.service';

@Controller()
export class ConversationController {
  constructor(private readonly conversationService: ConversationService) {}

  @MessagePattern('conversations.list')
  list(@Payload() data: { businessId: string; filters?: any }) {
    return this.conversationService.list(data.businessId, data.filters);
  }

  @MessagePattern('conversations.findOne')
  findOne(@Payload() data: { id: string; businessId: string }) {
    return this.conversationService.findOne(data.id, data.businessId);
  }

  @MessagePattern('conversations.close')
  close(@Payload() data: { id: string; businessId: string }) {
    return this.conversationService.close(data.id, data.businessId);
  }

  @MessagePattern('conversations.agentConfig.get')
  getAgentConfig(@Payload() data: { businessId: string }) {
    return this.conversationService.getAgentConfig(data.businessId, true);
  }

  @MessagePattern('conversations.agentConfig.upsert')
  upsertAgentConfig(@Payload() data: { businessId: string; dto: any; userId: string }) {
    return this.conversationService.upsertAgentConfig(data.businessId, data.dto, data.userId);
  }

  @MessagePattern('conversations.channels.list')
  listChannels(@Payload() data: { businessId: string }) {
    return this.conversationService.listChannels(data.businessId);
  }

  @MessagePattern('conversations.channels.upsert')
  upsertChannel(@Payload() data: { businessId: string; dto: any; userId: string }) {
    return this.conversationService.upsertChannel(data.businessId, data.dto, data.userId);
  }

  @MessagePattern('conversations.channels.deactivate')
  deactivateChannel(@Payload() data: { id: string }) {
    return this.conversationService.deactivateChannel(data.id);
  }
}
