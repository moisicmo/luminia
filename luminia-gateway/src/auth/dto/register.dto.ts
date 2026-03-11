import {
  IsDateString,
  IsEnum,
  IsOptional,
  IsString,
  Length,
  Matches,
  MinLength,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export enum TypeDocument {
  DNI = 'dni',
  NIT = 'nit',
  PASAPORTE = 'pasaporte',
  OTRO = 'otro',
}

export enum TypeAuth {
  EMAIL = 'EMAIL',
  PHONE = 'PHONE',
  GOOGLE = 'GOOGLE',
  FACEBOOK = 'FACEBOOK',
  APPLE = 'APPLE',
}

export class RegisterDto {
  // --- Datos de la persona ---
  @ApiProperty({ example: '12345678' })
  @IsString()
  numberDocument: string;

  @ApiProperty({ enum: TypeDocument, example: TypeDocument.DNI })
  @IsEnum(TypeDocument)
  typeDocument: TypeDocument;

  @ApiProperty({ example: 'Juan' })
  @IsString()
  name: string;

  @ApiProperty({ example: 'Pérez' })
  @IsString()
  lastName: string;

  @ApiPropertyOptional({ example: '1990-05-20' })
  @IsOptional()
  @IsDateString()
  birthDate?: string;

  @ApiPropertyOptional({ example: 'M', description: 'M, F u O' })
  @IsOptional()
  @IsString()
  @Length(1, 1)
  gender?: string;

  @ApiPropertyOptional({ example: 'Colombiana' })
  @IsOptional()
  @IsString()
  nationality?: string;

  @ApiPropertyOptional({ example: 'Calle 123 #45-67' })
  @IsOptional()
  @IsString()
  address?: string;

  // --- Datos de autenticación ---
  @ApiProperty({ enum: TypeAuth, example: TypeAuth.EMAIL })
  @IsEnum(TypeAuth)
  typeAuth: TypeAuth;

  @ApiProperty({ example: 'juan@example.com', description: 'Email, teléfono, o token OAuth' })
  @IsString()
  identifier: string;

  @ApiProperty({ example: 'MiPassword123*' })
  @IsString()
  @MinLength(8)
  password: string;

  @ApiPropertyOptional({ example: 'juanperez' })
  @IsOptional()
  @IsString()
  username?: string;

  // --- Contexto del sistema ---
  @Matches(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i, { message: 'systemId must be a UUID' })
  @ApiProperty({ description: 'ID del sistema al que se registra', example: '00000000-0000-0000-0000-000000000002' })
  systemId: string;
}
