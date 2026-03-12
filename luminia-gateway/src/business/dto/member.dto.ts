import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsOptional, IsUUID } from 'class-validator';

export class AddMemberDto {
  @ApiProperty({ description: 'personId del usuario a agregar (obtenido de /users/search)' })
  @IsUUID()
  userId: string;

  @ApiPropertyOptional({ description: 'ID del rol (BusinessRole) a asignar al nuevo miembro' })
  @IsOptional()
  @IsUUID()
  roleId?: string;
}

export class UpdateMemberRoleDto {
  @ApiProperty({ description: 'ID del rol (BusinessRole) a asignar' })
  @IsUUID()
  roleId: string;
}
