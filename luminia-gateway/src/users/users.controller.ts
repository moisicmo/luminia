import { Controller, Get, Query } from '@nestjs/common';
import { ApiBearerAuth, ApiOperation, ApiQuery, ApiTags } from '@nestjs/swagger';
import { UsersService } from './users.service';

@ApiTags('Users')
@ApiBearerAuth('Authorization')
@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Get('search')
  @ApiOperation({ summary: 'Buscar usuarios por nombre o número de documento' })
  @ApiQuery({ name: 'q', description: 'Texto a buscar (nombre, apellido o CI/NIT)', example: 'Juan' })
  search(@Query('q') q: string) {
    return this.usersService.searchUsers(q ?? '');
  }
}
