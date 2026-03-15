import { Injectable, Logger } from '@nestjs/common';
import OpenAI from 'openai';
import { envs } from '@/config';
import type {
  AiProviderInterface,
  ChatMessage,
  ChatCompletionResult,
  ToolDefinition,
} from '../interfaces/ai-provider.interface';

@Injectable()
export class OpenAiProvider implements AiProviderInterface {
  private readonly logger = new Logger(OpenAiProvider.name);

  private getClient(apiKey?: string): OpenAI {
    return new OpenAI({ apiKey: apiKey || envs.openaiApiKey });
  }

  async chatCompletion(
    messages: ChatMessage[],
    tools?: ToolDefinition[],
    options?: { model?: string; temperature?: number; maxTokens?: number; apiKey?: string },
  ): Promise<ChatCompletionResult> {
    const client = this.getClient(options?.apiKey);

    const response = await client.chat.completions.create({
      model: options?.model ?? envs.defaultAiModel,
      messages: messages as any,
      tools: tools?.length ? (tools as any) : undefined,
      temperature: options?.temperature ?? 0.7,
      max_tokens: options?.maxTokens ?? 1024,
    });

    const choice = response.choices[0];

    return {
      message: {
        role: 'assistant',
        content: choice.message.content,
        tool_calls: choice.message.tool_calls?.map((tc: any) => ({
          id: tc.id,
          type: 'function' as const,
          function: {
            name: tc.function.name,
            arguments: tc.function.arguments,
          },
        })),
      },
      finishReason: choice.finish_reason ?? 'stop',
      usage: {
        promptTokens: response.usage?.prompt_tokens ?? 0,
        completionTokens: response.usage?.completion_tokens ?? 0,
        totalTokens: response.usage?.total_tokens ?? 0,
      },
    };
  }
}
