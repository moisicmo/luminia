import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../../lib/prisma';
import { IsString, IsNumber, IsOptional, IsBoolean } from 'class-validator';

export class CreateBranchOfficeDto {
  @IsString() businessId: string;
  @IsString() @IsOptional() branchId?: string;
  @IsNumber() branchOfficeSiatId: number;
  @IsString() name: string;
  @IsString() description: string;
  @IsString() @IsOptional() city?: string;
  @IsString() @IsOptional() phone?: string;
  @IsString() createdBy: string;
}

export class UpdateBranchOfficeDto {
  @IsString() @IsOptional() name?: string;
  @IsString() @IsOptional() description?: string;
  @IsString() @IsOptional() city?: string;
  @IsString() @IsOptional() phone?: string;
  @IsBoolean() @IsOptional() active?: boolean;
  @IsString() updatedBy: string;
}

@Injectable()
export class BranchOfficeService {
  constructor(private readonly prisma: PrismaService) {}

  create(dto: CreateBranchOfficeDto) {
    return this.prisma.branchOffice.create({ data: { ...dto } });
  }

  findAll(businessId: string) {
    return this.prisma.branchOffice.findMany({ where: { businessId } });
  }

  async findOne(id: string) {
    const record = await this.prisma.branchOffice.findUnique({
      where: { id },
      include: { pointSales: true },
    });
    if (!record) throw new NotFoundException(`BranchOffice not found: ${id}`);
    return record;
  }

  async update(id: string, dto: UpdateBranchOfficeDto) {
    await this.findOne(id);
    return this.prisma.branchOffice.update({ where: { id }, data: { ...dto } });
  }
}
