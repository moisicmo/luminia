import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
} from '@nestjs/common';
import { ApiBearerAuth, ApiOperation, ApiTags } from '@nestjs/swagger';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessService } from './business.service';
import { CreateBusinessDto } from './dto/create-business.dto';
import { AddMemberDto, UpdateMemberRoleDto } from './dto/member.dto';

@ApiTags('Business')
@ApiBearerAuth('Authorization')
@Controller('business')
export class BusinessController {
  constructor(private readonly businessService: BusinessService) {}

  // ─── Business ─────────────────────────────────────────────────────────────

  @Post()
  @ApiOperation({ summary: 'Crear un nuevo negocio (el usuario queda como owner)' })
  create(@Body() dto: CreateBusinessDto, @User() user: CurrentUser) {
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
    return this.businessService.addMember(businessId, dto.userId, dto.role, user.sub);
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
    return this.businessService.updateMemberRole(businessId, memberId, dto.role, user.sub);
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
}
