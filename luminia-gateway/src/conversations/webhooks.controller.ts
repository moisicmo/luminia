import { Controller, Get, Post, Body, Query, Res } from '@nestjs/common';
import { ApiOperation, ApiTags } from '@nestjs/swagger';
import { Response } from 'express';
import { Public } from '../common/decorators/public.decorator';
import { ConversationsService } from './conversations.service';

@ApiTags('Webhooks')
@Controller('webhooks')
export class WebhooksController {
  constructor(private readonly conversationsService: ConversationsService) {}

  @Public()
  @Get('whatsapp')
  @ApiOperation({ summary: 'Verificación de webhook WhatsApp (Meta challenge)' })
  async verifyWhatsApp(@Query() query: any, @Res() res: Response) {
    const result = await this.conversationsService.verifyWebhook('WHATSAPP_API', query, {});
    if (result && result !== false) {
      return res.status(200).send(result);
    }
    return res.status(403).send('Forbidden');
  }

  @Public()
  @Post('whatsapp')
  @ApiOperation({ summary: 'Recibir mensajes de WhatsApp (webhook Meta)' })
  async handleWhatsApp(@Body() body: any) {
    // Respond 200 immediately, process async
    this.conversationsService.handleWebhook('whatsapp', body).catch(() => {});
    return { status: 'ok' };
  }
}
