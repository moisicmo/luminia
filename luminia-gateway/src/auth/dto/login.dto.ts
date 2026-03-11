import { IsEnum, IsOptional, IsString, Matches, MinLength } from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { TypeAuth } from './register.dto';

export class LoginDto {
  @Matches(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i, { message: 'systemId must be a UUID' })
  @ApiProperty({ description: 'ID del sistema al que ingresa', example: '00000000-0000-0000-0000-000000000002' })
  systemId: string;
  
  @ApiProperty({ enum: TypeAuth, example: TypeAuth.EMAIL })
  @IsEnum(TypeAuth)
  typeAuth: TypeAuth;

  @ApiProperty({ example: 'admin@luminia.com', description: 'Email, teléfono, o token OAuth' })
  @IsString()
  identifier: string;

  @ApiPropertyOptional({ example: 'Luminia2025*', description: 'Requerido para EMAIL y PHONE' })
  @IsOptional()
  @IsString()
  @MinLength(8)
  password?: string;

  @ApiPropertyOptional({ description: 'Token FCM para notificaciones push' })
  @IsOptional()
  @IsString()
  fcmToken?: string;

}
