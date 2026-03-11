import {
  Body,
  Controller,
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
import { PaymentsService } from './payments.service';

@ApiTags('Payments')
@ApiBearerAuth('Authorization')
@ApiHeader({ name: 'X-Business-Id', required: true, description: 'ID del negocio activo' })
@UseGuards(BusinessGuard)
@Controller('payments')
export class PaymentsController {
  constructor(private readonly paymentsService: PaymentsService) {}

  // ─── Payment Methods ──────────────────────────────────────────────────────

  @Post('methods')
  @MinRole(MemberRole.ADMIN)
  @ApiOperation({ summary: 'Configurar un método de pago' })
  configureMethod(
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.paymentsService.configureMethod(businessId, body, user.sub);
  }

  @Get('methods')
  @ApiOperation({ summary: 'Listar métodos de pago del negocio' })
  listMethods(@BusinessId() businessId: string) {
    return this.paymentsService.listMethods(businessId);
  }

  // ─── Transactions ─────────────────────────────────────────────────────────

  @Post('transactions')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear una transacción' })
  createTransaction(
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.paymentsService.createTransaction({ ...body, businessId }, user.sub);
  }

  @Get('transactions')
  @ApiOperation({ summary: 'Listar transacciones del negocio' })
  listTransactions(
    @BusinessId() businessId: string,
    @Query('direction') direction?: string,
    @Query('referenceType') referenceType?: string,
    @Query('referenceId') referenceId?: string,
    @Query('status') status?: string,
    @Query('from') from?: string,
    @Query('to') to?: string,
  ) {
    return this.paymentsService.listTransactions(businessId, {
      direction,
      referenceType,
      referenceId,
      status,
      from,
      to,
    });
  }

  @Get('transactions/:id')
  @ApiOperation({ summary: 'Obtener una transacción por ID' })
  findTransaction(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.paymentsService.findTransaction(id, businessId);
  }

  @Post('transactions/:id/confirm')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Confirmar una transacción' })
  confirmTransaction(
    @Param('id') id: string,
    @Body() body: { gatewayRef: string; gatewayResponse?: any },
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.paymentsService.confirmTransaction(
      id,
      body.gatewayRef,
      body.gatewayResponse,
      user.sub,
    );
  }

  // ─── Wallet ───────────────────────────────────────────────────────────────

  @Get('wallet')
  @ApiOperation({ summary: 'Obtener la billetera del negocio' })
  getWallet(
    @BusinessId() businessId: string,
    @Query('ownerId') ownerId: string,
    @Query('ownerType') ownerType: string,
    @Query('currency') currency?: string,
  ) {
    return this.paymentsService.getWallet(businessId, ownerId, ownerType, currency);
  }

  @Get('wallet/movements')
  @ApiOperation({ summary: 'Listar movimientos de la billetera' })
  listMovements(
    @Query('walletId') walletId: string,
    @BusinessId() businessId: string,
  ) {
    return this.paymentsService.listMovements(walletId, businessId);
  }
}
