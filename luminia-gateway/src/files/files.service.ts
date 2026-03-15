import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceFiles } from '@/config';

const RMQ_TIMEOUT_MS = 15_000;

@Injectable()
export class FilesService {
  private readonly logger = new Logger(FilesService.name);

  constructor(
    @Inject(RMQServiceFiles.getName())
    private readonly filesClient: ClientProxy,
  ) {}

  private async send<T>(pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] enviando → ${pattern}`);
    try {
      const result = await firstValueFrom(
        this.filesClient.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        throw new HttpException(
          `El servicio de archivos no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      throw err;
    }
  }

  createFileRecord(data: any) {
    return this.send('file.create', data);
  }

  findFile(id: string) {
    return this.send('file.findOne', { id });
  }

  listFiles(businessId: string, category?: string) {
    return this.send('file.list', { businessId, category });
  }

  deleteFile(id: string) {
    return this.send('file.delete', { id });
  }
}
