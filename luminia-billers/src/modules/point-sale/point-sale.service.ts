import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../../lib/prisma';
import { IsString, IsNumber, IsOptional, IsBoolean } from 'class-validator';

export class CreatePointSaleDto {
  @IsString() branchOfficeId: string;
  @IsString() @IsOptional() pointOfSaleId?: string;
  @IsNumber() pointSaleSiatId: number;
  @IsString() name: string;
  @IsString() description: string;
  @IsString() @IsOptional() pointSaleTypeId?: string;
  @IsString() createdBy: string;
}

export class UpdatePointSaleDto {
  @IsString() @IsOptional() name?: string;
  @IsString() @IsOptional() description?: string;
  @IsBoolean() @IsOptional() active?: boolean;
  @IsString() @IsOptional() pointSaleTypeId?: string;
  @IsString() updatedBy: string;
}

@Injectable()
export class PointSaleService {
  constructor(private readonly prisma: PrismaService) {}

  create(dto: CreatePointSaleDto) {
    return this.prisma.pointSale.create({ data: { ...dto } });
  }

  findAll(branchOfficeId: string) {
    return this.prisma.pointSale.findMany({ where: { branchOfficeId } });
  }

  async findOne(id: string) {
    const record = await this.prisma.pointSale.findUnique({
      where: { id },
      include: { cuis: { where: { active: true }, take: 1 } },
    });
    if (!record) throw new NotFoundException(`PointSale not found: ${id}`);
    return record;
  }

  async update(id: string, dto: UpdatePointSaleDto) {
    await this.findOne(id);
    return this.prisma.pointSale.update({ where: { id }, data: { ...dto } });
  }
}
