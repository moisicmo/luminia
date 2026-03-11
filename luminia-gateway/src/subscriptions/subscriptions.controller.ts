import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiBearerAuth, ApiHeader, ApiOperation, ApiTags } from '@nestjs/swagger';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessId } from '../common/decorators/business-id.decorator';
import { BusinessGuard } from '../common/guards/business.guard';
import { MinRole, MemberRole } from '../common/decorators/min-role.decorator';
import { SubscriptionsService } from './subscriptions.service';

@ApiTags('Subscriptions')
@ApiBearerAuth('Authorization')
@ApiHeader({ name: 'X-Business-Id', required: true, description: 'ID del negocio activo' })
@Controller('subscriptions')
export class SubscriptionsController {
  constructor(private readonly subscriptionsService: SubscriptionsService) {}

  // ─── Platform Plans (public — no BusinessGuard needed) ────────────────────

  @Get('platform-plans')
  @ApiOperation({ summary: 'Listar planes de plataforma disponibles' })
  listPlatformPlans() {
    return this.subscriptionsService.listPlatformPlans();
  }

  // ─── Platform Subscription ────────────────────────────────────────────────

  @Post('platform')
  @UseGuards(BusinessGuard)
  @MinRole(MemberRole.OWNER)
  @ApiOperation({ summary: 'Suscribir el negocio a un plan de plataforma' })
  subscribeToPlatform(
    @Body() body: { planId: string },
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.subscriptionsService.subscribeToPlatform(businessId, body.planId, user.sub);
  }

  @Get('platform')
  @UseGuards(BusinessGuard)
  @ApiOperation({ summary: 'Obtener la suscripción de plataforma del negocio' })
  getPlatformSubscription(@BusinessId() businessId: string) {
    return this.subscriptionsService.getPlatformSubscription(businessId);
  }

  @Delete('platform')
  @UseGuards(BusinessGuard)
  @MinRole(MemberRole.OWNER)
  @ApiOperation({ summary: 'Cancelar la suscripción de plataforma del negocio' })
  cancelPlatformSubscription(
    @Body() body: { reason?: string },
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.subscriptionsService.cancelPlatformSubscription(
      businessId,
      body.reason ?? '',
      user.sub,
    );
  }

  // ─── Business Plans ────────────────────────────────────────────────────────

  @Post('plans')
  @UseGuards(BusinessGuard)
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear un plan del negocio' })
  createPlan(
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.subscriptionsService.createPlan(businessId, body, user.sub);
  }

  @Get('plans')
  @UseGuards(BusinessGuard)
  @ApiOperation({ summary: 'Listar planes del negocio' })
  listPlans(@BusinessId() businessId: string) {
    return this.subscriptionsService.listPlans(businessId);
  }

  @Patch('plans/:id')
  @UseGuards(BusinessGuard)
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Actualizar un plan del negocio' })
  updatePlan(
    @Param('id') id: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.subscriptionsService.updatePlan(id, businessId, body, user.sub);
  }

  @Delete('plans/:id')
  @UseGuards(BusinessGuard)
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar un plan del negocio' })
  removePlan(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.subscriptionsService.removePlan(id, businessId, user.sub);
  }

  // ─── Customer Subscriptions ────────────────────────────────────────────────

  @Post()
  @UseGuards(BusinessGuard)
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Suscribir un cliente a un plan del negocio' })
  subscribe(
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.subscriptionsService.subscribe(businessId, body, user.sub);
  }

  @Get()
  @UseGuards(BusinessGuard)
  @ApiOperation({ summary: 'Listar suscripciones del negocio' })
  listSubscriptions(
    @BusinessId() businessId: string,
    @Query('customerId') customerId?: string,
    @Query('status') status?: string,
    @Query('planId') planId?: string,
  ) {
    return this.subscriptionsService.listSubscriptions(businessId, {
      customerId,
      status,
      planId,
    });
  }

  @Get(':id')
  @UseGuards(BusinessGuard)
  @ApiOperation({ summary: 'Obtener una suscripción por ID' })
  getSubscription(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.subscriptionsService.getSubscription(id, businessId);
  }

  @Delete(':id')
  @UseGuards(BusinessGuard)
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Cancelar una suscripción' })
  cancelSubscription(
    @Param('id') id: string,
    @Body() body: { reason?: string },
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.subscriptionsService.cancelSubscription(
      id,
      businessId,
      body.reason ?? '',
      user.sub,
    );
  }
}
