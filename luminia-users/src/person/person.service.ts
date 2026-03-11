import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';
import { CreatePersonDto } from './dto/create-person.dto';

@Injectable()
export class PersonService {
  private readonly logger = new Logger(PersonService.name);

  async search(q: string) {
    this.logger.log(`[person.search] query: "${q}"`);
    try {
      const persons = await prisma.person.findMany({
        where: {
          OR: [
            { numberDocument: { contains: q, mode: 'insensitive' } },
            { name: { contains: q, mode: 'insensitive' } },
            { lastName: { contains: q, mode: 'insensitive' } },
          ],
          active: true,
        },
        select: {
          id: true,
          name: true,
          lastName: true,
          numberDocument: true,
          typeDocument: true,
        },
        take: 10,
      });
      return persons;
    } catch (err) {
      this.logger.error(`[person.search] error: ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al buscar personas' });
    }
  }

  async create(dto: CreatePersonDto, createdBy: string) {
    this.logger.log(`[person.create] documento: ${dto.numberDocument}`);
    try {
      const existing = await prisma.person.findUnique({
        where: { numberDocument: dto.numberDocument },
      });

      if (existing) {
        this.logger.warn(`[person.create] documento ya existe: ${dto.numberDocument}`);
        throw new RpcException({
          status: 409,
          message: `El documento ${dto.numberDocument} ya está registrado`,
        });
      }

      const person = await prisma.person.create({
        data: {
          numberDocument: dto.numberDocument,
          typeDocument: dto.typeDocument,
          name: dto.name,
          lastName: dto.lastName,
          birthDate: dto.birthDate ? new Date(dto.birthDate) : undefined,
          gender: dto.gender,
          nationality: dto.nationality,
          address: dto.address,
          createdBy,
        },
      });

      this.logger.log(`[person.create] persona creada: ${person.id}`);
      return { personId: person.id };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[person.create] error inesperado: ${(err as Error).message}`, (err as Error).stack);
      throw new RpcException({ status: 500, message: 'Error interno al crear la persona' });
    }
  }
}
