import { Module } from '@nestjs/common';
import { ConversationsModule } from '@/conversations/conversations.module';
import { AiProvidersModule } from '@/ai-providers/ai-providers.module';
import { ToolsModule } from '@/tools/tools.module';
import { ChannelsModule } from '@/channels/channels.module';
import { AgentService } from './agent.service';
import { AgentController } from './agent.controller';

@Module({
  imports: [ConversationsModule, AiProvidersModule, ToolsModule, ChannelsModule],
  controllers: [AgentController],
  providers: [AgentService],
})
export class AgentsModule {}
