import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceConversations } from '@/config';

const RMQ_TIMEOUT_MS = 30_000; // longer timeout for AI processing

@Injectable()
export class ConversationsService {
  private readonly logger = new Logger(ConversationsService.name);

  constructor(
    @Inject(RMQServiceConversations.getName())
    private readonly client: ClientProxy,
  ) {}

  private async send<T>(pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] → ${pattern}`);
    try {
      const result = await firstValueFrom(
        this.client.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        throw new HttpException(
          `Servicio de conversaciones no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      throw err;
    }
  }

  // ─── Conversations ─────────────────────────────────────────────────────────

  list(businessId: string, filters?: any) {
    return this.send('conversations.list', { businessId, filters });
  }

  findOne(id: string, businessId: string) {
    return this.send('conversations.findOne', { id, businessId });
  }

  close(id: string, businessId: string) {
    return this.send('conversations.close', { id, businessId });
  }

  // ─── Agent Config ──────────────────────────────────────────────────────────

  getAgentConfig(businessId: string) {
    return this.send('conversations.agentConfig.get', { businessId });
  }

  upsertAgentConfig(businessId: string, dto: any, userId: string) {
    return this.send('conversations.agentConfig.upsert', { businessId, dto, userId });
  }

  // ─── Channels ──────────────────────────────────────────────────────────────

  listChannels(businessId: string) {
    return this.send('conversations.channels.list', { businessId });
  }

  upsertChannel(businessId: string, dto: any, userId: string) {
    return this.send('conversations.channels.upsert', { businessId, dto, userId });
  }

  deactivateChannel(id: string) {
    return this.send('conversations.channels.deactivate', { id });
  }

  // ─── Webhooks ──────────────────────────────────────────────────────────────

  handleWebhook(channelType: string, payload: any) {
    return this.send(`conversations.webhook.${channelType}`, { payload });
  }

  verifyWebhook(channelType: string, query: any, body: any) {
    return this.send('conversations.webhook.verify', { channelType, query, body });
  }

  // ─── Direct message (for web chat / testing) ──────────────────────────────

  sendMessage(businessId: string, message: any, channelConnectionId?: string) {
    return this.send('conversations.message.inbound', { businessId, message, channelConnectionId });
  }
}
