import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';

@Injectable()
export class FilesService {
  private readonly logger = new Logger(FilesService.name);

  async create(data: {
    businessId?: string;
    ownerId?: string;
    ownerType?: 'BUSINESS' | 'PERSON' | 'SYSTEM';
    category: 'IMAGE' | 'DOCUMENT' | 'SPREADSHEET' | 'REPORT' | 'ATTACHMENT' | 'OTHER';
    mimeType: string;
    originalName: string;
    storageKey: string;
    url: string;
    sizeBytes: number;
    visibility?: 'PUBLIC' | 'PRIVATE';
    createdBy: string;
  }) {
    try {
      return await prisma.file.create({
        data: {
          businessId: data.businessId,
          ownerId: data.ownerId,
          ownerType: data.ownerType,
          category: data.category,
          mimeType: data.mimeType,
          originalName: data.originalName,
          storageKey: data.storageKey,
          url: data.url,
          sizeBytes: data.sizeBytes,
          visibility: data.visibility ?? 'PUBLIC',
          createdBy: data.createdBy,
        },
      });
    } catch (err) {
      this.logger.error(`[file.create] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al registrar archivo' });
    }
  }

  async findOne(id: string) {
    try {
      const file = await prisma.file.findFirst({
        where: { id, deletedAt: null },
      });
      if (!file) throw new RpcException({ status: 404, message: 'Archivo no encontrado' });
      return file;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al obtener archivo' });
    }
  }

  async listByBusiness(businessId: string, category?: string) {
    try {
      const where: any = { businessId, deletedAt: null };
      if (category) where.category = category;
      return await prisma.file.findMany({
        where,
        orderBy: { createdAt: 'desc' },
        take: 100,
      });
    } catch (err) {
      this.logger.error(`[file.list] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar archivos' });
    }
  }

  async remove(id: string) {
    try {
      const file = await prisma.file.findFirst({ where: { id, deletedAt: null } });
      if (!file) throw new RpcException({ status: 404, message: 'Archivo no encontrado' });
      await prisma.file.update({
        where: { id },
        data: { deletedAt: new Date() },
      });
      return { removed: true, storageKey: file.storageKey };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      throw new RpcException({ status: 500, message: 'Error al eliminar archivo' });
    }
  }
}
