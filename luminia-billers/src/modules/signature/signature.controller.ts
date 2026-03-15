import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { SignatureService } from './signature.service';
import { PrismaService } from '../../lib/prisma';

@Controller()
export class SignatureController {
  constructor(
    private readonly signatureService: SignatureService,
    private readonly prisma: PrismaService,
  ) {}

  @MessagePattern('signature.upload')
  async upload(
    @Payload() data: { certificate: string; privateKey: string; businessId: string; userId: string },
  ) {
    const { certificate, privateKey, businessId, userId } = data;

    // Parse certificate to extract dates
    const parsed = this.signatureService.parseCertificate(certificate);

    // Deactivate previous active signatures for this business
    await this.prisma.signature.updateMany({
      where: { businessId, active: true },
      data: { active: false, updatedBy: userId },
    });

    // Create new active signature
    return this.prisma.signature.create({
      data: {
        businessId,
        certificate,
        privateKey,
        startDate: parsed.validFrom,
        endDate: parsed.validTo,
        active: true,
        createdBy: userId,
      },
    });
  }

  @MessagePattern('signature.findActive')
  async findActive(@Payload() data: { businessId: string }) {
    const sig = await this.prisma.signature.findFirst({
      where: { businessId: data.businessId, active: true },
    });
    if (!sig) return null;

    const expiringSoon = this.signatureService.isCertificateExpiringSoon(sig.certificate);
    const parsed = this.signatureService.parseCertificate(sig.certificate);
    return {
      id: sig.id,
      subject: parsed.subject,
      startDate: sig.startDate,
      endDate: sig.endDate,
      active: sig.active,
      expiringSoon,
      createdAt: sig.createdAt,
    };
  }

  @MessagePattern('signature.list')
  list(@Payload() data: { businessId: string }) {
    return this.prisma.signature.findMany({
      where: { businessId: data.businessId },
      select: {
        id: true,
        startDate: true,
        endDate: true,
        active: true,
        createdAt: true,
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  @MessagePattern('signature.deactivate')
  async deactivate(@Payload() data: { id: string; userId: string }) {
    await this.prisma.signature.update({
      where: { id: data.id },
      data: { active: false, updatedBy: data.userId },
    });
    return { success: true };
  }
}
