import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { ConversationService } from '@/conversations/conversation.service';
import { AiProviderRegistryService } from '@/ai-providers/ai-provider-registry.service';
import { ToolRegistryService } from '@/tools/tool-registry.service';
import { ChannelRegistryService } from '@/channels/channel-registry.service';
import type { NormalizedInboundMessage } from '@/channels/interfaces/channel-adapter.interface';
import type { ChatMessage } from '@/ai-providers/interfaces/ai-provider.interface';
import { envs } from '@/config';
import { prisma } from '@/lib/prisma';

const MAX_TOOL_ITERATIONS = 5;

const DEFAULT_SYSTEM_PROMPT = `Eres un asistente de ventas amable y profesional. Tu objetivo es ayudar a los clientes a encontrar productos, verificar disponibilidad y realizar pedidos.

Instrucciones:
- Responde siempre en el idioma del cliente
- Sé conciso y profesional
- Usa las herramientas disponibles para buscar productos y verificar stock antes de recomendar
- Cuando el cliente quiera comprar, confirma los productos, cantidades y precios antes de crear el pedido
- Si no encuentras un producto, sugiere alternativas
- No inventes precios ni disponibilidad; siempre consulta las herramientas`;

@Injectable()
export class AgentService {
  private readonly logger = new Logger(AgentService.name);

  constructor(
    private readonly conversations: ConversationService,
    private readonly aiRegistry: AiProviderRegistryService,
    private readonly tools: ToolRegistryService,
    private readonly channels: ChannelRegistryService,
  ) {}

  async handleInboundMessage(
    businessId: string,
    message: NormalizedInboundMessage,
    channelConnectionId?: string,
  ) {
    try {
      // 1. Find or create conversation
      const conversation = await this.conversations.findOrCreate({
        businessId,
        channelType: message.channelType,
        externalId: message.externalId,
        channelConnectionId,
        customerName: message.senderName,
      });

      // 2. Load agent config
      const config = await this.conversations.getAgentConfig(businessId);
      if (!config?.apiKey) {
        throw new RpcException({ status: 400, message: 'El negocio no tiene configurado un API key de IA. Configúrelo en Ajustes > Agente IA.' });
      }
      const providerName = config.provider ?? envs.defaultAiProvider;
      const apiKey = config.apiKey;
      const model = config.model ?? envs.defaultAiModel;
      const temperature = Number(config.temperature) ?? 0.7;
      const maxTokens = config.maxTokens ?? 1024;
      const systemPrompt = config.systemPrompt ?? DEFAULT_SYSTEM_PROMPT;
      const enabledTools = config.tools
        ? (Array.isArray(config.tools) ? config.tools as string[] : JSON.parse(String(config.tools)))
        : ['searchProducts', 'checkStock', 'createOrder'];

      // 3. Save user message
      await this.conversations.addMessage({
        conversationId: conversation.id,
        role: 'USER',
        content: message.content,
      });

      // 4. Load conversation history
      const history = await this.conversations.getHistory(conversation.id, 40);

      // 5. Build messages array
      const chatMessages: ChatMessage[] = [
        { role: 'system', content: systemPrompt },
        ...history.map((m) => ({
          role: m.role.toLowerCase() as ChatMessage['role'],
          content: m.content,
          tool_calls: m.toolCalls as any ?? undefined,
          tool_call_id: m.toolCallId ?? undefined,
          name: m.toolName ?? undefined,
        })),
      ];

      // 6. Get AI provider and tool definitions
      const provider = this.aiRegistry.getProvider(providerName);
      const toolDefs = this.tools.getDefinitions(enabledTools);

      // 7. AI completion loop with tool calling
      let iterations = 0;
      let result = await provider.chatCompletion(chatMessages, toolDefs, {
        model,
        temperature,
        maxTokens,
        apiKey,
      });

      while (result.message.tool_calls?.length && iterations < MAX_TOOL_ITERATIONS) {
        iterations++;

        // Save assistant message with tool_calls
        await this.conversations.addMessage({
          conversationId: conversation.id,
          role: 'ASSISTANT',
          content: result.message.content ?? undefined,
          toolCalls: result.message.tool_calls,
          tokens: result.usage.totalTokens,
        });

        // Execute each tool call
        for (const tc of result.message.tool_calls) {
          let toolArgs: any;
          try {
            toolArgs = JSON.parse(tc.function.arguments);
          } catch {
            toolArgs = {};
          }

          const toolResult = await this.tools.execute(tc.function.name, toolArgs, businessId);

          // Save tool result message
          await this.conversations.addMessage({
            conversationId: conversation.id,
            role: 'TOOL',
            content: JSON.stringify(toolResult),
            toolCallId: tc.id,
            toolName: tc.function.name,
          });

          // Add to chat messages for next iteration
          chatMessages.push({
            role: 'assistant',
            content: result.message.content,
            tool_calls: result.message.tool_calls,
          });
          chatMessages.push({
            role: 'tool',
            content: JSON.stringify(toolResult),
            tool_call_id: tc.id,
            name: tc.function.name,
          });
        }

        // Call AI again with tool results
        result = await provider.chatCompletion(chatMessages, toolDefs, {
          model,
          temperature,
          maxTokens,
          apiKey,
        });
      }

      // 8. Save final assistant response
      const responseText = result.message.content ?? '';
      await this.conversations.addMessage({
        conversationId: conversation.id,
        role: 'ASSISTANT',
        content: responseText,
        tokens: result.usage.totalTokens,
      });

      return {
        conversationId: conversation.id,
        response: responseText,
        tokens: result.usage.totalTokens,
      };
    } catch (err) {
      this.logger.error(`[agent.handleInbound] ${(err as Error).message}`);
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al procesar mensaje' });
    }
  }

  async handleWebhook(channelType: string, payload: any) {
    const adapter = this.channels.getAdapter(channelType);
    if (!adapter) {
      this.logger.warn(`No adapter for channel: ${channelType}`);
      return { ok: true };
    }

    const messages = adapter.parseInbound(payload);
    if (!messages.length) return { ok: true };

    const results: any[] = [];

    for (const msg of messages) {
      // Find channel connection for this business
      const connection = await prisma.channelConnection.findFirst({
        where: {
          channelType: channelType as any,
          status: 'ACTIVE',
        },
      });

      if (!connection) {
        this.logger.warn(`No active channel connection for ${channelType}`);
        continue;
      }

      // Process message
      const result = await this.handleInboundMessage(
        connection.businessId,
        msg,
        connection.id,
      );

      // Send response back via channel
      if (result.response) {
        await adapter.sendMessage(connection.config as any, {
          externalId: msg.externalId,
          content: result.response,
        });
      }

      results.push(result);
    }

    return { ok: true, processed: results.length };
  }

  verifyWebhook(channelType: string, query: any, body: any): string | boolean {
    const adapter = this.channels.getAdapter(channelType);
    if (!adapter?.verifyWebhook) return false;
    return adapter.verifyWebhook(query, body);
  }
}
