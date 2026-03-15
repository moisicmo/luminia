import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { AgentService } from './agent.service';

@Controller()
export class AgentController {
  constructor(private readonly agentService: AgentService) {}

  @MessagePattern('conversations.webhook.whatsapp')
  handleWhatsAppWebhook(@Payload() data: { payload: any }) {
    return this.agentService.handleWebhook('WHATSAPP_API', data.payload);
  }

  @MessagePattern('conversations.webhook.verify')
  verifyWebhook(@Payload() data: { channelType: string; query: any; body: any }) {
    return this.agentService.verifyWebhook(data.channelType, data.query, data.body);
  }

  @MessagePattern('conversations.message.inbound')
  handleInbound(@Payload() data: {
    businessId: string;
    message: any;
    channelConnectionId?: string;
  }) {
    return this.agentService.handleInboundMessage(
      data.businessId,
      data.message,
      data.channelConnectionId,
    );
  }
}
