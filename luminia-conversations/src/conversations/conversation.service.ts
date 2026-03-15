import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class ConversationService {
  private readonly logger = new Logger(ConversationService.name);

  async findOrCreate(data: {
    businessId: string;
    channelType: string;
    externalId: string;
    channelConnectionId?: string;
    customerName?: string;
  }) {
    try {
      let conversation = await prisma.conversation.findUnique({
        where: {
          businessId_channelType_externalId: {
            businessId: data.businessId,
            channelType: data.channelType as any,
            externalId: data.externalId,
          },
        },
      });

      if (!conversation) {
        conversation = await prisma.conversation.create({
          data: {
            businessId: data.businessId,
            channelType: data.channelType as any,
            externalId: data.externalId,
            channelConnectionId: data.channelConnectionId,
            customerName: data.customerName,
            status: 'ACTIVE',
          },
        });
      } else if (conversation.status === 'CLOSED') {
        conversation = await prisma.conversation.update({
          where: { id: conversation.id },
          data: { status: 'ACTIVE' },
        });
      }

      return conversation;
    } catch (err) {
      this.logger.error(`[conversation.findOrCreate] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al buscar/crear conversación' });
    }
  }

  async addMessage(data: {
    conversationId: string;
    role: string;
    content?: string;
    toolCalls?: any;
    toolCallId?: string;
    toolName?: string;
    tokens?: number;
  }) {
    try {
      const message = await prisma.message.create({
        data: {
          conversationId: data.conversationId,
          role: data.role as any,
          content: data.content ?? null,
          toolCalls: data.toolCalls ?? undefined,
          toolCallId: data.toolCallId ?? null,
          toolName: data.toolName ?? null,
          tokens: data.tokens ?? null,
        },
      });

      await prisma.conversation.update({
        where: { id: data.conversationId },
        data: { lastMessageAt: new Date() },
      });

      return message;
    } catch (err) {
      this.logger.error(`[conversation.addMessage] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al agregar mensaje' });
    }
  }

  async getHistory(conversationId: string, limit = 50) {
    try {
      return await prisma.message.findMany({
        where: { conversationId },
        orderBy: { createdAt: 'asc' },
        take: limit,
      });
    } catch (err) {
      this.logger.error(`[conversation.getHistory] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener historial' });
    }
  }

  async list(businessId: string, filters?: { status?: string; take?: number; skip?: number }) {
    try {
      const where: any = { businessId };
      if (filters?.status) where.status = filters.status;

      const take = Math.min(filters?.take ?? 50, 100);
      const skip = filters?.skip ?? 0;

      const [items, total] = await Promise.all([
        prisma.conversation.findMany({
          where,
          include: {
            messages: {
              orderBy: { createdAt: 'desc' },
              take: 1,
              select: { content: true, role: true, createdAt: true },
            },
            _count: { select: { messages: true } },
          },
          orderBy: { lastMessageAt: 'desc' },
          take,
          skip,
        }),
        prisma.conversation.count({ where }),
      ]);

      return { items, total };
    } catch (err) {
      this.logger.error(`[conversation.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar conversaciones' });
    }
  }

  async findOne(id: string, businessId: string) {
    try {
      const conversation = await prisma.conversation.findFirst({
        where: { id, businessId },
        include: {
          messages: { orderBy: { createdAt: 'asc' } },
          channelConnection: { select: { id: true, channelType: true, status: true } },
        },
      });
      if (!conversation) throw new RpcException({ status: 404, message: 'Conversación no encontrada' });
      return conversation;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al obtener conversación' });
    }
  }

  async close(id: string, businessId: string) {
    try {
      const conversation = await prisma.conversation.findFirst({
        where: { id, businessId, status: 'ACTIVE' },
      });
      if (!conversation) throw new RpcException({ status: 404, message: 'Conversación no encontrada' });

      await prisma.conversation.update({
        where: { id },
        data: { status: 'CLOSED' },
      });
      return { closed: true };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al cerrar conversación' });
    }
  }

  // ─── Agent Config ──────────────────────────────────────────────────────────

  async getAgentConfig(businessId: string, maskApiKey = false) {
    try {
      const config = await prisma.businessAgentConfig.findUnique({
        where: { businessId },
      });
      if (config && maskApiKey) {
        const key = config.apiKey;
        config.apiKey = key ? `${key.slice(0, 6)}...${'*'.repeat(8)}...${key.slice(-4)}` : '';
      }
      return config;
    } catch (err) {
      this.logger.error(`[agentConfig.get] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener config de agente' });
    }
  }

  async upsertAgentConfig(businessId: string, data: any, userId: string) {
    try {
      const result = await prisma.businessAgentConfig.upsert({
        where: { businessId },
        create: {
          businessId,
          provider: data.provider ?? 'OPENAI',
          apiKey: data.apiKey,
          model: data.model ?? 'gpt-4o-mini',
          systemPrompt: data.systemPrompt,
          temperature: data.temperature ?? 0.7,
          maxTokens: data.maxTokens ?? 1024,
          tools: data.tools ?? ['searchProducts', 'checkStock', 'createOrder'],
          createdBy: userId,
        },
        update: {
          provider: data.provider,
          apiKey: data.apiKey,
          model: data.model,
          systemPrompt: data.systemPrompt,
          temperature: data.temperature,
          maxTokens: data.maxTokens,
          tools: data.tools,
          updatedBy: userId,
        },
      });

      // Return masked apiKey
      const key = result.apiKey;
      result.apiKey = key ? `${key.slice(0, 6)}...${'*'.repeat(8)}...${key.slice(-4)}` : '';
      return result;
    } catch (err) {
      this.logger.error(`[agentConfig.upsert] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al guardar config de agente' });
    }
  }

  // ─── Channel Connections ───────────────────────────────────────────────────

  async listChannels(businessId: string) {
    try {
      return await prisma.channelConnection.findMany({
        where: { businessId },
        orderBy: { createdAt: 'desc' },
      });
    } catch (err) {
      this.logger.error(`[channels.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar canales' });
    }
  }

  async upsertChannel(businessId: string, data: any, userId: string) {
    try {
      return await prisma.channelConnection.upsert({
        where: {
          businessId_channelType: {
            businessId,
            channelType: data.channelType,
          },
        },
        create: {
          businessId,
          channelType: data.channelType,
          status: data.status ?? 'ACTIVE',
          config: data.config ?? {},
          webhookSecret: data.webhookSecret,
          createdBy: userId,
        },
        update: {
          status: data.status,
          config: data.config,
          webhookSecret: data.webhookSecret,
          updatedBy: userId,
        },
      });
    } catch (err) {
      this.logger.error(`[channels.upsert] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al guardar canal' });
    }
  }

  async deactivateChannel(id: string) {
    try {
      await prisma.channelConnection.update({
        where: { id },
        data: { status: 'INACTIVE' },
      });
      return { deactivated: true };
    } catch (err) {
      this.logger.error(`[channels.deactivate] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al desactivar canal' });
    }
  }
}
