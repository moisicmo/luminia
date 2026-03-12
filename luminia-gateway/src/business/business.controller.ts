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
}
