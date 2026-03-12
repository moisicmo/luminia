import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import {
  IsString,
  IsNotEmpty,
  IsOptional,
  IsEnum,
  ValidateNested,
  IsNumber,
} from 'class-validator';
import { Type } from 'class-transformer';

enum BusinessType {
  ADMINISTRACION = 'ADMINISTRACION',
  SERVICIO = 'SERVICIO',
  TIENDA = 'TIENDA',
  RESTAURANT = 'RESTAURANT',
  HOTEL = 'HOTEL',
  CAPACITACION = 'CAPACITACION',
  INSTITUTO = 'INSTITUTO',
  COLEGIO = 'COLEGIO',
  TRANSPORTE = 'TRANSPORTE',
  SALUD = 'SALUD',
  ENTRETENIMIENTO = 'ENTRETENIMIENTO',
  ECOMMERCE = 'ECOMMERCE',
  CONSULTORIA = 'CONSULTORIA',
}

export class CreateBranchDto {
  @ApiProperty({ example: 'Sucursal Central' })
  @IsString()
  @IsNotEmpty()
  name: string;

  @ApiProperty({ example: 'La Paz' })
  @IsString()
  @IsNotEmpty()
  region: string;

  @ApiPropertyOptional({ example: 'Av. Arce 1234' })
  @IsString()
  @IsOptional()
  address?: string;

  @ApiPropertyOptional({ example: 'La Paz' })
  @IsString()
  @IsOptional()
  municipality?: string;

  @ApiPropertyOptional({ example: '+591 2 1234567' })
  @IsString()
  @IsOptional()
  phone?: string;

  @ApiPropertyOptional({ example: -16.5 })
  @IsNumber()
  @IsOptional()
  latitude?: number;

  @ApiPropertyOptional({ example: -68.15 })
  @IsNumber()
  @IsOptional()
  longitude?: number;

  @ApiPropertyOptional({ example: 'Lun-Vie 08:00-18:00' })
  @IsString()
  @IsOptional()
  openingHours?: string;
}

export class CreateBusinessDto {
  @ApiProperty({ example: 'Mi Tienda S.R.L.' })
  @IsString()
  @IsNotEmpty()
  name: string;

  @ApiProperty({ enum: BusinessType, example: BusinessType.TIENDA })
  @IsEnum(BusinessType)
  businessType?: BusinessType;

  @ApiPropertyOptional({ example: '123456789' })
  @IsString()
  @IsOptional()
  taxId?: string;

  @ApiPropertyOptional({ example: 'mi-tienda' })
  @IsString()
  @IsOptional()
  url?: string;

  @ApiProperty({ type: CreateBranchDto })
  @ValidateNested()
  @Type(() => CreateBranchDto)
  branch: CreateBranchDto;
}
