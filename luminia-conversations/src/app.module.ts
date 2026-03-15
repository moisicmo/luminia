import { Module } from '@nestjs/common';
import { ConversationsModule } from './conversations/conversations.module';
import { AgentsModule } from './agents/agents.module';

@Module({
  imports: [ConversationsModule, AgentsModule],
})
export class AppModule {}
