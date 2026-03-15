import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../../lib/prisma';
import { IsString, IsNumber, IsOptional, IsEnum } from 'class-validator';

enum EnvironmentSiat {
  TEST = 'TEST',
  PRODUCTION = 'PRODUCTION',
}

export class UpsertSiatConfigDto {
  @IsEnum(EnvironmentSiat) environment: EnvironmentSiat;
  @IsString() nit: string;
  @IsString() socialReason: string;
  @IsNumber() mainActivityCode: number;
}

@Injectable()
export class SiatConfigService {
  private readonly logger = new Logger(SiatConfigService.name);

  constructor(private readonly prisma: PrismaService) {}

  async findOne(businessId: string) {
    this.logger.log(`[siatConfig.findOne] businessId=${businessId}`);
    return this.prisma.siatConfig.findFirst({
      where: { businessId, active: true },
    });
  }

  async upsert(dto: UpsertSiatConfigDto, businessId: string, userId: string) {
    this.logger.log(`[siatConfig.upsert] businessId=${businessId}`);
    const existing = await this.prisma.siatConfig.findFirst({
      where: { businessId },
    });

    if (existing) {
      return this.prisma.siatConfig.update({
        where: { id: existing.id },
        data: {
          environment: dto.environment as any,
          nit: dto.nit,
          socialReason: dto.socialReason,
          mainActivityCode: dto.mainActivityCode,
          active: true,
          updatedBy: userId,
        },
      });
    }

    return this.prisma.siatConfig.create({
      data: {
        businessId,
        environment: dto.environment as any,
        nit: dto.nit,
        socialReason: dto.socialReason,
        mainActivityCode: dto.mainActivityCode,
        active: true,
        createdBy: userId,
      },
    });
  }

  async deactivate(businessId: string, userId: string) {
    this.logger.log(`[siatConfig.deactivate] businessId=${businessId}`);
    const config = await this.prisma.siatConfig.findFirst({
      where: { businessId, active: true },
    });
    if (!config) return { success: false, message: 'No config found' };

    await this.prisma.siatConfig.update({
      where: { id: config.id },
      data: { active: false, updatedBy: userId },
    });
    return { success: true };
  }
}
