import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  Put,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiBearerAuth, ApiHeader, ApiOperation, ApiTags } from '@nestjs/swagger';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessId } from '../common/decorators/business-id.decorator';
import { BusinessGuard } from '../common/guards/business.guard';
import { MinRole, MemberRole } from '../common/decorators/min-role.decorator';
import { BillingService } from './billing.service';

@ApiTags('Billing / SIAT')
@ApiBearerAuth('Authorization')
@ApiHeader({ name: 'X-Business-Id', required: true, description: 'ID del negocio activo' })
@UseGuards(BusinessGuard)
@Controller('billing')
export class BillingController {
  constructor(private readonly billingService: BillingService) {}

  // ─── Branch Offices ──────────────────────────────────────────────────────────

  @Post('branch-offices')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear sucursal SIAT' })
  createBranchOffice(@Body() body: any, @BusinessId() businessId: string) {
    return this.billingService.createBranchOffice({ ...body, businessId });
  }

  @Get('branch-offices')
  @ApiOperation({ summary: 'Listar sucursales SIAT' })
  listBranchOffices(@BusinessId() businessId: string) {
    return this.billingService.listBranchOffices(businessId);
  }

  @Get('branch-offices/:id')
  @ApiOperation({ summary: 'Obtener sucursal SIAT' })
  findBranchOffice(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.billingService.findBranchOffice(id, businessId);
  }

  @Patch('branch-offices/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Actualizar sucursal SIAT' })
  updateBranchOffice(@Param('id') id: string, @Body() body: any, @BusinessId() businessId: string) {
    return this.billingService.updateBranchOffice(id, { ...body, businessId });
  }

  // ─── Point of Sale ──────────────────────────────────────────────────────────

  @Post('point-sales')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear punto de venta SIAT' })
  createPointSale(@Body() body: any, @BusinessId() businessId: string) {
    return this.billingService.createPointSale({ ...body, businessId });
  }

  @Get('point-sales')
  @ApiOperation({ summary: 'Listar puntos de venta SIAT' })
  listPointSales(
    @BusinessId() businessId: string,
    @Query('branchOfficeId') branchOfficeId: string,
  ) {
    return this.billingService.listPointSales(branchOfficeId, businessId);
  }

  @Get('point-sales/:id')
  @ApiOperation({ summary: 'Obtener punto de venta SIAT' })
  findPointSale(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.billingService.findPointSale(id, businessId);
  }

  @Patch('point-sales/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Actualizar punto de venta SIAT' })
  updatePointSale(@Param('id') id: string, @Body() body: any, @BusinessId() businessId: string) {
    return this.billingService.updatePointSale(id, { ...body, businessId });
  }

  // ─── Sync ────────────────────────────────────────────────────────────────────

  @Post('sync/codes')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Sincronizar CUIS/CUFD para un punto de venta' })
  syncCodes(@Body() body: { pointSaleId: string }, @BusinessId() businessId: string) {
    return this.billingService.syncCodes(businessId, body.pointSaleId);
  }

  @Post('sync/all-codes')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Sincronizar todos los códigos CUIS/CUFD' })
  syncAllCodes(@BusinessId() businessId: string) {
    return this.billingService.syncAllCodes(businessId);
  }

  @Post('sync/parameters')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Sincronizar parámetros SIAT (actividades, unidades, etc.)' })
  syncParameters(@BusinessId() businessId: string) {
    return this.billingService.syncParameters(businessId);
  }

  // ─── Invoice ────────────────────────────────────────────────────────────────

  @Post('invoices')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Emitir factura SIAT' })
  issueInvoice(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.billingService.issueInvoice({ ...body, businessId, issuedBy: user.sub });
  }

  @Post('invoices/simple')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Emitir factura SIAT simplificada (desde POS)' })
  issueSimpleInvoice(@Body() body: any, @BusinessId() businessId: string) {
    return this.billingService.issueSimpleInvoice({ ...body, businessId });
  }

  @Post('invoices/:id/cancel')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Anular factura SIAT' })
  cancelInvoice(@Param('id') id: string, @Body() body: any, @BusinessId() businessId: string) {
    return this.billingService.cancelInvoice({ invoiceId: id, ...body, businessId });
  }

  @Post('invoices/:id/cancel-reversal')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Revertir anulación de factura SIAT' })
  cancelReversal(@Param('id') id: string, @Body() body: any, @BusinessId() businessId: string) {
    return this.billingService.cancelReversal({ invoiceId: id, ...body, businessId });
  }

  @Post('check-nit')
  @ApiOperation({ summary: 'Verificar NIT del comprador' })
  checkNit(@Body() body: { nit: string }, @BusinessId() businessId: string) {
    return this.billingService.checkNit({ nit: body.nit, businessId });
  }

  @Get('invoices/:id/status')
  @ApiOperation({ summary: 'Verificar estado de factura en SIAT' })
  checkInvoiceStatus(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.billingService.checkInvoiceStatus({ invoiceId: id, businessId });
  }

  @Get('invoices')
  @ApiOperation({ summary: 'Listar facturas emitidas' })
  listInvoices(
    @BusinessId() businessId: string,
    @Query('status') status?: string,
    @Query('dateFrom') dateFrom?: string,
    @Query('dateTo') dateTo?: string,
    @Query('take') take?: string,
    @Query('skip') skip?: string,
  ) {
    return this.billingService.listInvoices(businessId, {
      status,
      dateFrom,
      dateTo,
      take: take ? Number(take) : undefined,
      skip: skip ? Number(skip) : undefined,
    });
  }

  // ─── SIAT Config ────────────────────────────────────────────────────────────

  @Get('config')
  @ApiOperation({ summary: 'Obtener configuración SIAT del negocio' })
  findSiatConfig(@BusinessId() businessId: string) {
    return this.billingService.findSiatConfig(businessId);
  }

  @Put('config')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear o actualizar configuración SIAT' })
  upsertSiatConfig(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.billingService.upsertSiatConfig(body, businessId, user.sub);
  }

  @Delete('config')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Desactivar configuración SIAT' })
  deactivateSiatConfig(@BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.billingService.deactivateSiatConfig(businessId, user.sub);
  }

  // ─── Catalogs ───────────────────────────────────────────────────────────────

  @Get('catalogs/activities')
  @ApiOperation({ summary: 'Listar actividades económicas SIAT' })
  listActivities(@BusinessId() businessId: string) {
    return this.billingService.listActivities(businessId);
  }

  @Get('catalogs/measurement-units')
  @ApiOperation({ summary: 'Listar unidades de medida SIAT' })
  listMeasurementUnits(@BusinessId() businessId: string) {
    return this.billingService.listMeasurementUnits(businessId);
  }

  @Get('catalogs/product-services')
  @ApiOperation({ summary: 'Listar productos/servicios SIAT' })
  listProductServices(@BusinessId() businessId: string) {
    return this.billingService.listProductServices(businessId);
  }

  @Get('catalogs/payment-methods')
  @ApiOperation({ summary: 'Listar métodos de pago SIAT' })
  listPaymentMethodTypes(@BusinessId() businessId: string) {
    return this.billingService.listPaymentMethodTypes(businessId);
  }

  @Get('catalogs/currencies')
  @ApiOperation({ summary: 'Listar tipos de moneda SIAT' })
  listCurrencyTypes(@BusinessId() businessId: string) {
    return this.billingService.listCurrencyTypes(businessId);
  }

  @Get('catalogs/document-sectors')
  @ApiOperation({ summary: 'Listar tipos de documento sector SIAT' })
  listDocumentSectorTypes(@BusinessId() businessId: string) {
    return this.billingService.listDocumentSectorTypes(businessId);
  }

  @Get('catalogs/identity-document-types')
  @ApiOperation({ summary: 'Listar tipos de documento de identidad SIAT' })
  listIdentityDocumentTypes(@BusinessId() businessId: string) {
    return this.billingService.listIdentityDocumentTypes(businessId);
  }

  @Get('catalogs/cancellation-reasons')
  @ApiOperation({ summary: 'Listar motivos de anulación SIAT' })
  listCancellationReasons(@BusinessId() businessId: string) {
    return this.billingService.listCancellationReasons(businessId);
  }

  // ─── Signatures ─────────────────────────────────────────────────────────────

  @Post('signatures')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Subir certificado digital' })
  uploadSignature(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.billingService.uploadSignature({ ...body, businessId, userId: user.sub });
  }

  @Get('signatures')
  @ApiOperation({ summary: 'Listar certificados digitales' })
  listSignatures(@BusinessId() businessId: string) {
    return this.billingService.listSignatures(businessId);
  }

  @Get('signatures/active')
  @ApiOperation({ summary: 'Obtener certificado digital activo' })
  findActiveSignature(@BusinessId() businessId: string) {
    return this.billingService.findActiveSignature(businessId);
  }

  @Delete('signatures/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Desactivar certificado digital' })
  deactivateSignature(@Param('id') id: string, @User() user: CurrentUser) {
    return this.billingService.deactivateSignature(id, user.sub);
  }
}
