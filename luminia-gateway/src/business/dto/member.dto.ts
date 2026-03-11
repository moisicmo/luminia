import { ApiProperty } from '@nestjs/swagger';
import { IsEnum, IsUUID } from 'class-validator';

export enum MemberRole {
  ADMIN      = 'ADMIN',
  MANAGER    = 'MANAGER',
  SELLER     = 'SELLER',
  INVENTORY  = 'INVENTORY',
  VIEWER     = 'VIEWER',
}

export class AddMemberDto {
  @ApiProperty({ description: 'personId del usuario a agregar (obtenido de /users/search)' })
  @IsUUID()
  userId: string;

  @ApiProperty({ enum: MemberRole, example: MemberRole.SELLER })
  @IsEnum(MemberRole)
  role: MemberRole;
}

export class UpdateMemberRoleDto {
  @ApiProperty({ enum: MemberRole, example: MemberRole.MANAGER })
  @IsEnum(MemberRole)
  role: MemberRole;
}
