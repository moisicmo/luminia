import { Injectable, Logger } from '@nestjs/common';
import { Cron } from '@nestjs/schedule';
import { PrismaService } from '../../../lib/prisma';
import { SignatureService } from '../../signature/signature.service';

/**
 * Checks for expiring digital certificates and logs a warning.
 * Runs at 8:00 and 16:00 daily (same as Java's ExpirationDigitalCertificateTask).
 */
@Injectable()
export class ExpirationCertificateTask {
  private readonly logger = new Logger(ExpirationCertificateTask.name);
  private readonly EXPIRATION_THRESHOLD_DAYS = 30;

  constructor(
    private readonly prisma: PrismaService,
    private readonly signatureService: SignatureService,
  ) {}

  @Cron('0 8,16 * * *') // 8am and 4pm daily
  async run() {
    const signatures = await this.prisma.signature.findMany({
      where: { active: true },
    });

    for (const sig of signatures) {
      try {
        const expiring = this.signatureService.isCertificateExpiringSoon(
          sig.certificate,
          this.EXPIRATION_THRESHOLD_DAYS,
        );
        if (expiring) {
          const { validTo } = this.signatureService.parseCertificate(sig.certificate);
          this.logger.warn(
            `Certificate for business=${sig.businessId} expires on ${validTo.toISOString()}`,
          );
          // TODO: send notification via luminia-notifiers
        }
      } catch (err) {
        this.logger.error(`Could not parse certificate id=${sig.id}: ${err.message}`);
      }
    }
  }
}
