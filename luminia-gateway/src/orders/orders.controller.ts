import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiBearerAuth, ApiHeader, ApiOperation, ApiTags } from '@nestjs/swagger';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessId } from '../common/decorators/business-id.decorator';
import { BusinessGuard } from '../common/guards/business.guard';
import { MinRole, MemberRole } from '../common/decorators/min-role.decorator';
import { OrdersService } from './orders.service';

@ApiTags('Orders')
@ApiBearerAuth('Authorization')
@ApiHeader({ name: 'X-Business-Id', required: true, description: 'ID del negocio activo' })
@UseGuards(BusinessGuard)
@Controller('orders')
export class OrdersController {
  constructor(private readonly ordersService: OrdersService) {}

  @Post()
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Crear una nueva orden' })
  create(
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.ordersService.create({ ...body, businessId }, user.sub);
  }

  @Get()
  @ApiOperation({ summary: 'Listar órdenes del negocio' })
  list(
    @BusinessId() businessId: string,
    @Query('status') status?: string,
    @Query('customerId') customerId?: string,
    @Query('type') type?: string,
    @Query('from') from?: string,
    @Query('to') to?: string,
  ) {
    return this.ordersService.list(businessId, { status, customerId, type, from, to });
  }

  @Get(':id')
  @ApiOperation({ summary: 'Obtener una orden por ID' })
  findOne(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.ordersService.findOne(id, businessId);
  }

  @Post(':id/items')
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Agregar ítem a la orden' })
  addItem(
    @Param('id') orderId: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.ordersService.addItem(orderId, businessId, body, user.sub);
  }

  @Delete(':id/items/:itemId')
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Eliminar ítem de la orden' })
  removeItem(
    @Param('id') orderId: string,
    @Param('itemId') itemId: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.ordersService.removeItem(orderId, itemId, businessId, user.sub);
  }

  @Post(':id/payments')
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Agregar pago a la orden' })
  addPayment(
    @Param('id') orderId: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.ordersService.addPayment(orderId, businessId, body, user.sub);
  }

  @Post(':id/confirm')
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Confirmar la orden' })
  confirm(
    @Param('id') orderId: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.ordersService.confirm(orderId, businessId, user.sub);
  }

  @Post(':id/cancel')
  @MinRole(MemberRole.SELLER)
  @ApiOperation({ summary: 'Cancelar la orden' })
  cancel(
    @Param('id') orderId: string,
    @Body() body: { reason?: string },
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.ordersService.cancel(orderId, businessId, body.reason ?? '', user.sub);
  }
}
