import { Module } from '@nestjs/common';
import { OpenAiProvider } from './openai/openai.provider';
import { DeepSeekProvider } from './deepseek/deepseek.provider';
import { AiProviderRegistryService } from './ai-provider-registry.service';

@Module({
  providers: [OpenAiProvider, DeepSeekProvider, AiProviderRegistryService],
  exports: [AiProviderRegistryService],
})
export class AiProvidersModule {}
