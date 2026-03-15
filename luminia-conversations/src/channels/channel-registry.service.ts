import { Injectable } from '@nestjs/common';
import { WhatsAppApiAdapter } from './whatsapp-api/whatsapp-api.adapter';
import type { ChannelAdapter } from './interfaces/channel-adapter.interface';

@Injectable()
export class ChannelRegistryService {
  private readonly adapters: Map<string, ChannelAdapter>;

  constructor(private readonly whatsappApi: WhatsAppApiAdapter) {
    this.adapters = new Map<string, ChannelAdapter>([
      ['WHATSAPP_API', this.whatsappApi],
    ]);
  }

  getAdapter(channelType: string): ChannelAdapter | undefined {
    return this.adapters.get(channelType);
  }

  getAvailableChannels(): string[] {
    return [...this.adapters.keys()];
  }
}
