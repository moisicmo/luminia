import { Module } from '@nestjs/common';
import { WhatsAppApiAdapter } from './whatsapp-api/whatsapp-api.adapter';
import { ChannelRegistryService } from './channel-registry.service';

@Module({
  providers: [WhatsAppApiAdapter, ChannelRegistryService],
  exports: [ChannelRegistryService],
})
export class ChannelsModule {}
