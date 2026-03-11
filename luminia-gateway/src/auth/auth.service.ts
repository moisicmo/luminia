import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { Request } from 'express';
import { RMQServiceAuthentications, RMQServiceUsers } from '@/config';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';

const LUMINIA_SYSTEM_ID = '00000000-0000-0000-0000-000000000001';
const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class AuthService {
  private readonly logger = new Logger(AuthService.name);

  constructor(
    @Inject(RMQServiceAuthentications.getName())
    private readonly authClient: ClientProxy,

    @Inject(RMQServiceUsers.getName())
    private readonly userClient: ClientProxy,
  ) {}

  private async send<T>(client: ClientProxy, pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] enviando → ${pattern}`);
    try {
      const result = await firstValueFrom(
        client.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      this.logger.log(`[RMQ] respuesta OK ← ${pattern}`);
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        this.logger.error(`[RMQ] TIMEOUT esperando respuesta de: ${pattern}`);
        throw new HttpException(
          `El servicio no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      this.logger.error(`[RMQ] ERROR en ${pattern}: ${JSON.stringify(err)}`);
      throw err;
    }
  }

  async register(dto: RegisterDto, _req: Request) {
    this.logger.log(`[register] iniciando registro para documento: ${dto.numberDocument}`);

    const { personId } = await this.send<{ personId: string }>(
      this.userClient,
      'person.create',
      {
        dto: {
          numberDocument: dto.numberDocument,
          typeDocument: dto.typeDocument,
          name: dto.name,
          lastName: dto.lastName,
          birthDate: dto.birthDate,
          gender: dto.gender,
          nationality: dto.nationality,
          address: dto.address,
        },
        createdBy: LUMINIA_SYSTEM_ID,
      },
    );

    this.logger.log(`[register] persona creada: ${personId}, creando cuenta...`);

    return this.send(this.authClient, 'auth.register', {
      personId,
      typeAuth: dto.typeAuth,
      identifier: dto.identifier,
      password: dto.password,
      username: dto.username,
    });
  }

  async login(dto: LoginDto, req: Request) {
    this.logger.log(`[login] intento de login: ${dto.identifier} (${dto.typeAuth})`);
    return this.send(this.authClient, 'auth.login', {
      typeAuth: dto.typeAuth,
      identifier: dto.identifier,
      password: dto.password,
      fcmToken: dto.fcmToken,
      systemId: dto.systemId,
      userAgent: req.headers['user-agent'] ?? 'unknown',
      ipAddress: req.ip ?? 'unknown',
    });
  }

  async refresh(refreshToken: string, req: Request) {
    this.logger.log(`[refresh] renovando token`);
    return this.send(this.authClient, 'auth.refresh', {
      refreshToken,
      userAgent: req.headers['user-agent'] ?? 'unknown',
      ipAddress: req.ip ?? 'unknown',
    });
  }
}
