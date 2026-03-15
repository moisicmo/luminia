import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  Query,
} from '@nestjs/common';
import { ApiBearerAuth, ApiOperation, ApiTags } from '@nestjs/swagger';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessService } from './business.service';
import { CreateBusinessDto } from './dto/create-business.dto';
import { AddMemberDto, UpdateMemberRoleDto } from './dto/member.dto';
import { CreateRoleDto, UpdateRoleDto, AssignRoleDto } from './dto/role.dto';
import { Public } from '../common/decorators/public.decorator';

@ApiTags('Business')
@ApiBearerAuth('Authorization')
@Controller('business')
export class BusinessController {
  constructor(private readonly businessService: BusinessService) {}

  // ─── Public ───────────────────────────────────────────────────────────────

  @Get('public')
  @Public()
  @ApiOperation({ summary: 'Listar todos los negocios activos (público)' })
  listPublic() {
    return this.businessService.listPublic();
  }

  @Get('resolve')
  @Public()
  @ApiOperation({ summary: 'Resolver negocio por URL/slug (público)' })
  resolve(@Query('url') url: string) {
    return this.businessService.resolveByUrl(url);
  }

  // ─── Business ─────────────────────────────────────────────────────────────

  @Post()
  @ApiOperation({ summary: 'Crear un nuevo negocio (el usuario queda como owner)' })
  create(@Body() dto: CreateBusinessDto, @User() user: CurrentUser) {
    console.log('aqui');
    return this.businessService.createBusiness(dto, user.sub);
  }

  @Get()
  @ApiOperation({ summary: 'Listar mis negocios' })
  findAll(@User() user: CurrentUser) {
    return this.businessService.getMyBusinesses(user.sub);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Obtener un negocio por ID' })
  findOne(@Param('id') id: string, @User() user: CurrentUser) {
    return this.businessService.getBusinessById(id, user.sub);
  }

  @Get(':id/access')
  @ApiOperation({ summary: 'Verificar acceso del usuario al negocio' })
  checkAccess(@Param('id') businessId: string, @User() user: CurrentUser) {
    return this.businessService.checkAccess(businessId, user.sub);
  }

  // ─── Members ──────────────────────────────────────────────────────────────

  @Post(':id/members')
  @ApiOperation({ summary: 'Agregar un miembro al negocio (requiere ser owner o admin)' })
  addMember(
    @Param('id') businessId: string,
    @Body() dto: AddMemberDto,
    @User() user: CurrentUser,
  ) {
    return this.businessService.addMember(businessId, dto.userId, user.sub, dto.roleId);
  }

  @Get(':id/members')
  @ApiOperation({ summary: 'Listar miembros del negocio' })
  listMembers(@Param('id') businessId: string, @User() user: CurrentUser) {
    return this.businessService.listMembers(businessId, user.sub);
  }

  @Patch(':id/members/:memberId')
  @ApiOperation({ summary: 'Cambiar el rol de un miembro (requiere ser owner o admin)' })
  updateMemberRole(
    @Param('id') businessId: string,
    @Param('memberId') memberId: string,
    @Body() dto: UpdateMemberRoleDto,
    @User() user: CurrentUser,
  ) {
    return this.businessService.updateMemberRole(businessId, memberId, dto.roleId, user.sub);
  }

  @Delete(':id/members/:memberId')
  @ApiOperation({ summary: 'Remover un miembro del negocio (requiere ser owner o admin)' })
  removeMember(
    @Param('id') businessId: string,
    @Param('memberId') memberId: string,
    @User() user: CurrentUser,
  ) {
    return this.businessService.removeMember(businessId, memberId, user.sub);
  }

  // ─── Roles ────────────────────────────────────────────────────────────────

  @Get(':id/roles')
  @ApiOperation({ summary: 'Listar roles del negocio' })
  listRoles(@Param('id') businessId: string) {
    return this.businessService.listRoles(businessId);
  }

  @Get('permissions')
  @ApiOperation({ summary: 'Listar todos los permisos disponibles del sistema' })
  listPermissions() {
    return this.businessService.listPermissions();
  }

  @Post(':id/roles')
  @ApiOperation({ summary: 'Crear un rol personalizado' })
  createRole(
    @Param('id') businessId: string,
    @Body() dto: CreateRoleDto,
    @User() user: CurrentUser,
  ) {
    return this.businessService.createRole(businessId, dto, user.sub);
  }

  @Patch(':id/roles/:roleId')
  @ApiOperation({ summary: 'Actualizar un rol' })
  updateRole(
    @Param('id') businessId: string,
    @Param('roleId') roleId: string,
    @Body() dto: UpdateRoleDto,
    @User() user: CurrentUser,
  ) {
    return this.businessService.updateRole(businessId, roleId, dto, user.sub);
  }

  @Delete(':id/roles/:roleId')
  @ApiOperation({ summary: 'Eliminar un rol personalizado' })
  removeRole(
    @Param('id') businessId: string,
    @Param('roleId') roleId: string,
    @User() user: CurrentUser,
  ) {
    return this.businessService.removeRole(businessId, roleId, user.sub);
  }

  @Patch(':id/members/:memberId/role')
  @ApiOperation({ summary: 'Asignar un rol a un miembro' })
  assignRole(
    @Param('id') businessId: string,
    @Param('memberId') memberId: string,
    @Body() dto: AssignRoleDto,
    @User() user: CurrentUser,
  ) {
    return this.businessService.assignRole(businessId, memberId, dto.roleId, user.sub);
  }

  // ─── Branches ──────────────────────────────────────────────────────────────

  @Get(':id/branches')
  @ApiOperation({ summary: 'Listar sucursales del negocio' })
  listBranches(@Param('id') businessId: string) {
    return this.businessService.listBranches(businessId);
  }

  @Post(':id/branches')
  @ApiOperation({ summary: 'Crear una nueva sucursal' })
  createBranch(
    @Param('id') businessId: string,
    @Body() dto: any,
    @User() user: CurrentUser,
  ) {
    return this.businessService.createBranch(dto, businessId, user.sub);
  }

  @Get(':id/branches/:branchId')
  @ApiOperation({ summary: 'Obtener sucursal por ID' })
  findOneBranch(
    @Param('id') businessId: string,
    @Param('branchId') branchId: string,
  ) {
    return this.businessService.findOneBranch(branchId, businessId);
  }

  @Patch(':id/branches/:branchId')
  @ApiOperation({ summary: 'Actualizar una sucursal' })
  updateBranch(
    @Param('id') businessId: string,
    @Param('branchId') branchId: string,
    @Body() dto: any,
    @User() user: CurrentUser,
  ) {
    return this.businessService.updateBranch(branchId, dto, businessId, user.sub);
  }

  @Delete(':id/branches/:branchId')
  @ApiOperation({ summary: 'Desactivar una sucursal' })
  deactivateBranch(
    @Param('id') businessId: string,
    @Param('branchId') branchId: string,
    @User() user: CurrentUser,
  ) {
    return this.businessService.deactivateBranch(branchId, businessId, user.sub);
  }

  // ─── Points of Sale ────────────────────────────────────────────────────────

  @Get(':id/branches/:branchId/pos')
  @ApiOperation({ summary: 'Listar puntos de venta de una sucursal' })
  listPos(@Param('branchId') branchId: string) {
    return this.businessService.listPos(branchId);
  }

  @Post(':id/branches/:branchId/pos')
  @ApiOperation({ summary: 'Crear punto de venta' })
  createPos(
    @Param('branchId') branchId: string,
    @Body() dto: { name: string; paperSize?: string },
    @User() user: CurrentUser,
  ) {
    return this.businessService.createPos(dto, branchId, user.sub);
  }

  @Patch(':id/branches/:branchId/pos/:posId')
  @ApiOperation({ summary: 'Actualizar punto de venta' })
  updatePos(
    @Param('posId') posId: string,
    @Body() dto: { name?: string; paperSize?: string },
    @User() user: CurrentUser,
  ) {
    return this.businessService.updatePos(posId, dto, user.sub);
  }

  @Delete(':id/branches/:branchId/pos/:posId')
  @ApiOperation({ summary: 'Desactivar punto de venta' })
  deactivatePos(
    @Param('posId') posId: string,
    @User() user: CurrentUser,
  ) {
    return this.businessService.deactivatePos(posId, user.sub);
  }

  // ─── Cash Register ─────────────────────────────────────────────────────────

  @Post(':id/cash-register/open')
  @ApiOperation({ summary: 'Abrir caja registradora' })
  openCashRegister(
    @Param('id') businessId: string,
    @Body() dto: { pointOfSaleId: string; openingAmount: number; notes?: string },
    @User() user: CurrentUser,
  ) {
    return this.businessService.openCashRegister(dto, businessId, user.sub);
  }

  @Post(':id/cash-register/close')
  @ApiOperation({ summary: 'Cerrar caja registradora' })
  closeCashRegister(
    @Param('id') businessId: string,
    @Body() dto: { sessionId: string; closingAmount: number; notes?: string },
    @User() user: CurrentUser,
  ) {
    return this.businessService.closeCashRegister(dto.sessionId, { closingAmount: dto.closingAmount, notes: dto.notes }, user.sub);
  }

  @Get(':id/cash-register/active')
  @ApiOperation({ summary: 'Obtener sesión de caja activa del usuario' })
  getActiveCashRegister(
    @Param('id') businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.businessService.getActiveCashRegister(businessId, user.sub);
  }

  @Get(':id/cash-register')
  @ApiOperation({ summary: 'Listar sesiones de caja' })
  listCashRegisterSessions(
    @Param('id') businessId: string,
    @Query('pointOfSaleId') pointOfSaleId?: string,
    @Query('status') status?: string,
    @Query('dateFrom') dateFrom?: string,
    @Query('dateTo') dateTo?: string,
    @Query('take') take?: string,
    @Query('skip') skip?: string,
  ) {
    return this.businessService.listCashRegisterSessions(businessId, {
      pointOfSaleId: pointOfSaleId || undefined,
      status: status || undefined,
      dateFrom: dateFrom || undefined,
      dateTo: dateTo || undefined,
      take: take ? Number(take) : undefined,
      skip: skip ? Number(skip) : undefined,
    });
  }

  @Get(':id/cash-register/:sessionId')
  @ApiOperation({ summary: 'Obtener detalle de sesión de caja' })
  findOneCashRegisterSession(@Param('sessionId') sessionId: string) {
    return this.businessService.findOneCashRegisterSession(sessionId);
  }

  @Post(':id/cash-register/add-sale')
  @ApiOperation({ summary: 'Agregar monto de venta a la sesión activa' })
  addSaleAmount(@Body() dto: { sessionId: string; amount: number }) {
    return this.businessService.addSaleAmount(dto.sessionId, dto.amount);
  }

  // ─── Members assignment (branches/POS) ─────────────────────────────────────

  @Patch(':id/members/:memberId/assignment')
  @ApiOperation({ summary: 'Actualizar asignación de sucursales/POS de un miembro' })
  updateMemberAssignment(
    @Param('id') businessId: string,
    @Param('memberId') memberId: string,
    @Body() dto: { roleId?: string; branchIds?: string[]; pointOfSaleId?: string | null },
    @User() user: CurrentUser,
  ) {
    return this.businessService.updateMemberAssignment(businessId, memberId, dto, user.sub);
  }

  // ─── Customers ──────────────────────────────────────────────────────────────

  @Get(':id/customers')
  @ApiOperation({ summary: 'Listar clientes del negocio' })
  listCustomers(
    @Param('id') businessId: string,
    @Query('search') search?: string,
  ) {
    return this.businessService.listCustomers(businessId, search ? { search } : undefined);
  }

  @Post(':id/customers')
  @ApiOperation({ summary: 'Crear cliente' })
  createCustomer(
    @Param('id') businessId: string,
    @Body() dto: any,
    @User() user: CurrentUser,
  ) {
    return this.businessService.createCustomer(businessId, dto, user.sub);
  }

  @Get(':id/customers/:customerId')
  @ApiOperation({ summary: 'Obtener cliente por ID' })
  findOneCustomer(
    @Param('id') businessId: string,
    @Param('customerId') customerId: string,
  ) {
    return this.businessService.findOneCustomer(customerId, businessId);
  }

  @Patch(':id/customers/:customerId')
  @ApiOperation({ summary: 'Actualizar cliente' })
  updateCustomer(
    @Param('id') businessId: string,
    @Param('customerId') customerId: string,
    @Body() dto: any,
    @User() user: CurrentUser,
  ) {
    return this.businessService.updateCustomer(customerId, businessId, dto, user.sub);
  }

  @Delete(':id/customers/:customerId')
  @ApiOperation({ summary: 'Eliminar cliente (soft delete)' })
  removeCustomer(
    @Param('id') businessId: string,
    @Param('customerId') customerId: string,
    @User() user: CurrentUser,
  ) {
    return this.businessService.removeCustomer(customerId, businessId, user.sub);
  }

  // ─── Invitations ────────────────────────────────────────────────────────────

  @Get(':id/invitations')
  @ApiOperation({ summary: 'Listar invitaciones del negocio' })
  listInvitations(@Param('id') businessId: string) {
    return this.businessService.listInvitations(businessId);
  }

  @Post(':id/invitations')
  @ApiOperation({ summary: 'Crear invitación para unirse al equipo' })
  createInvitation(
    @Param('id') businessId: string,
    @Body() dto: { email?: string; phone?: string; roleId: string; branchIds?: string[] },
    @User() user: CurrentUser,
  ) {
    return this.businessService.createInvitation(businessId, dto, user.sub);
  }

  @Delete(':id/invitations/:invitationId')
  @ApiOperation({ summary: 'Cancelar invitación' })
  cancelInvitation(
    @Param('id') businessId: string,
    @Param('invitationId') invitationId: string,
  ) {
    return this.businessService.cancelInvitation(invitationId, businessId);
  }

  @Post('invitations/accept')
  @ApiOperation({ summary: 'Aceptar invitación por token' })
  acceptInvitation(
    @Body() dto: { token: string },
    @User() user: CurrentUser,
  ) {
    return this.businessService.acceptInvitation(dto.token, user.sub);
  }
}
