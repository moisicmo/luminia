export interface NormalizedInboundMessage {
  externalId: string;
  channelType: string;
  content: string;
  senderName?: string;
  mediaUrl?: string;
  rawPayload: any;
}

export interface OutboundMessage {
  externalId: string;
  content: string;
  mediaUrl?: string;
}

export interface ChannelAdapter {
  readonly channelType: string;
  parseInbound(payload: any): NormalizedInboundMessage[];
  sendMessage(connectionConfig: any, message: OutboundMessage): Promise<void>;
  verifyWebhook?(query: any, body: any): string | boolean;
}
