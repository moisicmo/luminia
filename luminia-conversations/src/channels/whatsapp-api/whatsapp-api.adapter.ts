import { Injectable, Logger } from '@nestjs/common';
import { envs } from '@/config';
import type { ChannelAdapter, NormalizedInboundMessage, OutboundMessage } from '../interfaces/channel-adapter.interface';

@Injectable()
export class WhatsAppApiAdapter implements ChannelAdapter {
  private readonly logger = new Logger(WhatsAppApiAdapter.name);

  readonly channelType = 'WHATSAPP_API';

  parseInbound(payload: any): NormalizedInboundMessage[] {
    const messages: NormalizedInboundMessage[] = [];

    try {
      const entries = payload?.entry ?? [];
      for (const entry of entries) {
        const changes = entry?.changes ?? [];
        for (const change of changes) {
          const value = change?.value;
          if (!value?.messages) continue;

          const contacts = value.contacts ?? [];
          for (const msg of value.messages) {
            if (msg.type !== 'text') continue;

            const contact = contacts.find((c: any) => c.wa_id === msg.from);
            messages.push({
              externalId: msg.from,
              channelType: this.channelType,
              content: msg.text?.body ?? '',
              senderName: contact?.profile?.name,
              rawPayload: msg,
            });
          }
        }
      }
    } catch (err) {
      this.logger.error(`Error parsing WhatsApp webhook: ${(err as Error).message}`);
    }

    return messages;
  }

  async sendMessage(connectionConfig: any, message: OutboundMessage): Promise<void> {
    const { phoneNumberId, accessToken } = connectionConfig;
    if (!phoneNumberId || !accessToken) {
      this.logger.error('WhatsApp config missing phoneNumberId or accessToken');
      return;
    }

    const url = `https://graph.facebook.com/v18.0/${phoneNumberId}/messages`;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          messaging_product: 'whatsapp',
          to: message.externalId,
          type: 'text',
          text: { body: message.content },
        }),
      });

      if (!response.ok) {
        const errorBody = await response.text();
        this.logger.error(`WhatsApp API error: ${response.status} ${errorBody}`);
      }
    } catch (err) {
      this.logger.error(`WhatsApp send error: ${(err as Error).message}`);
    }
  }

  verifyWebhook(query: any): string | boolean {
    const mode = query['hub.mode'];
    const token = query['hub.verify_token'];
    const challenge = query['hub.challenge'];

    if (mode === 'subscribe' && token === envs.whatsappVerifyToken) {
      this.logger.log('WhatsApp webhook verified');
      return challenge;
    }
    return false;
  }
}
