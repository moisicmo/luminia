import {
  Controller,
  Get,
  Post,
  Put,
  Delete,
  Body,
  Param,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiBearerAuth, ApiHeader, ApiOperation, ApiTags } from '@nestjs/swagger';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessId } from '../common/decorators/business-id.decorator';
import { BusinessGuard } from '../common/guards/business.guard';
import { MinRole, MemberRole } from '../common/decorators/min-role.decorator';
import { ConversationsService } from './conversations.service';

@ApiTags('Conversations')
@ApiBearerAuth('Authorization')
@ApiHeader({ name: 'X-Business-Id', required: true, description: 'ID del negocio activo' })
@UseGuards(BusinessGuard)
@Controller('conversations')
export class ConversationsController {
  constructor(private readonly conversationsService: ConversationsService) {}

  // ─── Conversations ─────────────────────────────────────────────────────────

  @Get()
  @ApiOperation({ summary: 'Listar conversaciones del negocio' })
  list(
    @BusinessId() businessId: string,
    @Query('status') status?: string,
    @Query('take') take?: string,
    @Query('skip') skip?: string,
  ) {
    return this.conversationsService.list(businessId, {
      status,
      take: take ? Number(take) : undefined,
      skip: skip ? Number(skip) : undefined,
    });
  }

  @Get(':id')
  @ApiOperation({ summary: 'Obtener conversación con mensajes' })
  findOne(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.conversationsService.findOne(id, businessId);
  }

  @Post(':id/close')
  @ApiOperation({ summary: 'Cerrar conversación' })
  close(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.conversationsService.close(id, businessId);
  }

  // ─── Direct message (web chat / testing) ───────────────────────────────────

  @Post('message')
  @ApiOperation({ summary: 'Enviar mensaje directo al agente (web chat / testing)' })
  sendMessage(@Body() body: any, @BusinessId() businessId: string) {
    return this.conversationsService.sendMessage(businessId, {
      externalId: body.externalId ?? body.sessionId ?? 'web-chat',
      channelType: body.channelType ?? 'WEB_CHAT',
      content: body.content,
      senderName: body.senderName,
    });
  }

  // ─── Agent Config ──────────────────────────────────────────────────────────

  @Get('agent-config')
  @ApiOperation({ summary: 'Obtener configuración del agente IA' })
  getAgentConfig(@BusinessId() businessId: string) {
    return this.conversationsService.getAgentConfig(businessId);
  }

  @Put('agent-config')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear/actualizar configuración del agente IA' })
  upsertAgentConfig(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.conversationsService.upsertAgentConfig(businessId, body, user.sub);
  }

  // ─── Channel Connections ───────────────────────────────────────────────────

  @Get('channels')
  @ApiOperation({ summary: 'Listar conexiones de canal' })
  listChannels(@BusinessId() businessId: string) {
    return this.conversationsService.listChannels(businessId);
  }

  @Post('channels')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear/actualizar conexión de canal' })
  upsertChannel(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.conversationsService.upsertChannel(businessId, body, user.sub);
  }

  @Delete('channels/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Desactivar conexión de canal' })
  deactivateChannel(@Param('id') id: string) {
    return this.conversationsService.deactivateChannel(id);
  }
}
