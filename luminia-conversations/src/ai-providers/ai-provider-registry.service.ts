import { Injectable } from '@nestjs/common';
import { OpenAiProvider } from './openai/openai.provider';
import { DeepSeekProvider } from './deepseek/deepseek.provider';
import type { AiProviderInterface } from './interfaces/ai-provider.interface';

@Injectable()
export class AiProviderRegistryService {
  private readonly providers: Map<string, AiProviderInterface>;

  constructor(
    private readonly openai: OpenAiProvider,
    private readonly deepseek: DeepSeekProvider,
  ) {
    this.providers = new Map<string, AiProviderInterface>([
      ['OPENAI', this.openai],
      ['DEEPSEEK', this.deepseek],
    ]);
  }

  getProvider(name: string): AiProviderInterface {
    const provider = this.providers.get(name.toUpperCase());
    if (!provider) {
      throw new Error(`AI provider not found: ${name}. Available: ${[...this.providers.keys()].join(', ')}`);
    }
    return provider;
  }
}
