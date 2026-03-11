import { Injectable, Logger } from '@nestjs/common';
import { Cron } from '@nestjs/schedule';
import { PrismaService } from '../../../lib/prisma';
import { SyncService } from '../../sync/sync.service';

/**
 * Periodically refreshes CUFD codes for all active businesses.
 * CUFD codes expire daily and need to be renewed.
 */
@Injectable()
export class SyncCodesTask {
  private readonly logger = new Logger(SyncCodesTask.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly syncService: SyncService,
  ) {}

  @Cron('0 0 * * *') // midnight daily
  async run() {
    this.logger.log('Starting daily CUFD sync...');

    const expiredCufds = await this.prisma.cufd.findMany({
      where: {
        active: true,
        endDate: { lt: new Date() },
      },
      include: {
        cuis: {
          include: {
            pointSale: { include: { branchOffice: true } },
          },
        },
      },
    });

    for (const cufd of expiredCufds) {
      try {
        // Deactivate expired CUFD
        await this.prisma.cufd.update({
          where: { id: cufd.id },
          data: { active: false },
        });

        // Request a new CUFD
        const newCufd = await this.syncService.synchronizeCufd(cufd.cuisId);
        this.logger.log(`CUFD renewed for cuisId=${cufd.cuisId}: ${newCufd.code}`);
      } catch (err) {
        this.logger.error(`CUFD renewal failed for cuisId=${cufd.cuisId}: ${err.message}`);
      }
    }
  }
}
