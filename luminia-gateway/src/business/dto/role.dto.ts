import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsArray, IsOptional, IsString, IsUUID } from 'class-validator';

export class CreateRoleDto {
  @ApiProperty({ example: 'Cajero' })
  @IsString()
  name: string;

  @ApiPropertyOptional({ example: 'Encargado de caja' })
  @IsOptional()
  @IsString()
  description?: string;

  @ApiProperty({ type: [String], example: ['ventas:ver', 'ventas:crear'] })
  @IsArray()
  @IsString({ each: true })
  permissions: string[];
}

export class UpdateRoleDto {
  @ApiPropertyOptional({ example: 'Cajero Senior' })
  @IsOptional()
  @IsString()
  name?: string;

  @ApiPropertyOptional()
  @IsOptional()
  @IsString()
  description?: string;

  @ApiPropertyOptional({ type: [String] })
  @IsOptional()
  @IsArray()
  @IsString({ each: true })
  permissions?: string[];
}

export class AssignRoleDto {
  @ApiProperty({ description: 'ID del rol a asignar' })
  @IsUUID()
  roleId: string;
}
