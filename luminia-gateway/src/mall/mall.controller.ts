import { Controller, Get, Query } from '@nestjs/common';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { Public } from '@/common/decorators/public.decorator';
import { MallService } from './mall.service';

@ApiTags('Mall')
@Controller('mall')
export class MallController {
  constructor(private readonly mallService: MallService) {}

  @Public()
  @Get('products')
  @ApiOperation({ summary: 'Listar productos públicos del marketplace' })
  listProducts(
    @Query('search') search?: string,
    @Query('businessType') businessType?: string,
    @Query('businessId') businessId?: string,
    @Query('take') take?: string,
    @Query('skip') skip?: string,
  ) {
    return this.mallService.listMallProducts({
      search: search || undefined,
      businessType: businessType || undefined,
      businessId: businessId || undefined,
      take: take ? Number(take) : undefined,
      skip: skip ? Number(skip) : undefined,
    });
  }
}
