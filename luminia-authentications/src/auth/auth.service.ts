import { Injectable, Logger } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { RpcException } from '@nestjs/microservices';
import * as crypto from 'crypto';
import { prisma } from '@/lib/prisma';
import { LoginDto } from './dto/login.dto';
import { RegisterAuthDto } from './dto/register.dto';

const OAUTH_TYPES = ['GOOGLE', 'FACEBOOK', 'APPLE'];
const BLOCKED_STATES = ['BANEADO', 'BLOQUEADO', 'INACTIVO', 'SUSPENDIDO'];
const SESSION_DAYS = 7;
const REFRESH_DAYS = 30;

@Injectable()
export class AuthService {
  private readonly logger = new Logger(AuthService.name);

  constructor(private readonly jwtService: JwtService) {}

  async login(dto: LoginDto) {
    this.logger.log(`[auth.login] identifier: ${dto.identifier}, type: ${dto.typeAuth}`);
    try {
      const authRecord = await prisma.authentication.findFirst({
        where: { typeAuth: dto.typeAuth, data: dto.identifier, validated: true, active: true },
      });
      if (!authRecord) {
        throw new RpcException({ status: 401, message: 'Credenciales inválidas' });
      }

      const user = await prisma.user.findUnique({ where: { personId: authRecord.userId } });
      if (!user) {
        throw new RpcException({ status: 401, message: 'Credenciales inválidas' });
      }

      if (BLOCKED_STATES.includes(user.state)) {
        throw new RpcException({ status: 403, message: `Cuenta ${user.state.toLowerCase()}` });
      }

      if (!OAUTH_TYPES.includes(dto.typeAuth)) {
        if (!dto.password) {
          throw new RpcException({ status: 400, message: 'Contraseña requerida' });
        }
        const hash = crypto
          .pbkdf2Sync(dto.password, user.passwordSalt, 10000, 64, 'sha512')
          .toString('hex');
        if (hash !== user.password) {
          throw new RpcException({ status: 401, message: 'Credenciales inválidas' });
        }
      }

      await prisma.session.updateMany({
        where: {
          userId: user.personId,
          revokedAt: null,
          expiresAt: { gt: new Date() },
        },
        data: { revokedAt: new Date() },
      });

      const sessionToken = crypto.randomBytes(32).toString('hex');
      const refreshToken = crypto.randomBytes(32).toString('hex');
      const expiresAt = new Date(Date.now() + SESSION_DAYS * 24 * 60 * 60 * 1000);

      const session = await prisma.session.create({
        data: {
          userId: user.personId,
          sessionToken,
          refreshToken,
          userAgent: dto.userAgent ?? 'unknown',
          ipAddress: dto.ipAddress ?? 'unknown',
          fcmToken: dto.fcmToken,
          expiresAt,
          createdBy: user.personId,
        },
      });

      const token = this.jwtService.sign(
        { sub: user.personId, sessionId: session.id, systemId: dto.systemId },
        { expiresIn: `${SESSION_DAYS}d` },
      );

      this.logger.log(`[auth.login] sesión creada para: ${user.personId}`);
      return {
        token,
        refreshToken: session.refreshToken,
        expiresAt: session.expiresAt,
        refreshExpiresAt: new Date(Date.now() + REFRESH_DAYS * 24 * 60 * 60 * 1000),
        user: {
          personId: user.personId,
          username: user.username,
          state: user.state,
        },
      };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[auth.login] error inesperado: ${(err as Error).message}`, (err as Error).stack);
      throw new RpcException({ status: 500, message: 'Error interno al iniciar sesión' });
    }
  }

  async refreshToken(dto: { refreshToken: string; userAgent?: string; ipAddress?: string }) {
    this.logger.log(`[auth.refresh] renovando token`);
    try {
      const session = await prisma.session.findUnique({
        where: { refreshToken: dto.refreshToken },
        include: { user: true },
      });

      if (!session) {
        throw new RpcException({ status: 401, message: 'Refresh token inválido' });
      }

      if (session.revokedAt) {
        throw new RpcException({ status: 401, message: 'La sesión fue revocada' });
      }

      const refreshExpiry = new Date(session.createdAt.getTime() + REFRESH_DAYS * 24 * 60 * 60 * 1000);
      if (new Date() > refreshExpiry) {
        throw new RpcException({ status: 401, message: 'Refresh token expirado, inicia sesión nuevamente' });
      }

      if (BLOCKED_STATES.includes(session.user.state)) {
        throw new RpcException({ status: 403, message: `Cuenta ${session.user.state.toLowerCase()}` });
      }

      await prisma.session.update({
        where: { id: session.id },
        data: { revokedAt: new Date() },
      });

      const newSessionToken = crypto.randomBytes(32).toString('hex');
      const newRefreshToken = crypto.randomBytes(32).toString('hex');
      const newExpiresAt = new Date(Date.now() + SESSION_DAYS * 24 * 60 * 60 * 1000);

      const newSession = await prisma.session.create({
        data: {
          userId: session.userId,
          sessionToken: newSessionToken,
          refreshToken: newRefreshToken,
          userAgent: dto.userAgent ?? session.userAgent,
          ipAddress: dto.ipAddress ?? session.ipAddress,
          fcmToken: session.fcmToken,
          expiresAt: newExpiresAt,
          createdBy: session.userId,
        },
      });

      const token = this.jwtService.sign(
        { sub: session.userId, sessionId: newSession.id },
        { expiresIn: `${SESSION_DAYS}d` },
      );

      this.logger.log(`[auth.refresh] nueva sesión: ${newSession.id}`);
      return {
        token,
        refreshToken: newSession.refreshToken,
        expiresAt: newSession.expiresAt,
      };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[auth.refresh] error inesperado: ${(err as Error).message}`, (err as Error).stack);
      throw new RpcException({ status: 500, message: 'Error interno al renovar token' });
    }
  }

  async register(dto: RegisterAuthDto) {
    this.logger.log(`[auth.register] personId: ${dto.personId}, identifier: ${dto.identifier}`);
    try {
      const existing = await prisma.user.findUnique({ where: { personId: dto.personId } });
      if (existing) {
        throw new RpcException({ status: 409, message: 'Esta persona ya tiene una cuenta registrada' });
      }

      const existingAuth = await prisma.authentication.findFirst({
        where: { typeAuth: dto.typeAuth, data: dto.identifier },
      });
      if (existingAuth) {
        throw new RpcException({ status: 409, message: 'El email/teléfono ya está registrado' });
      }

      const salt = crypto.randomBytes(16).toString('hex');
      const hash = crypto.pbkdf2Sync(dto.password, salt, 10000, 64, 'sha512').toString('hex');

      await prisma.user.create({
        data: {
          personId: dto.personId,
          username: dto.username,
          password: hash,
          passwordSalt: salt,
          state: 'PENDIENTE',
        },
      });

      const tokenValidation = crypto.randomBytes(32).toString('hex');

      await prisma.authentication.create({
        data: {
          userId: dto.personId,
          typeAuth: dto.typeAuth,
          data: dto.identifier,
          validated: true, // TODO: cambiar a false cuando se implemente la verificación por email
          tokenValidation,
          active: true,
          createdBy: dto.personId,
        },
      });

      this.logger.log(`[auth.register] cuenta creada para personId: ${dto.personId}`);
      return {
        personId: dto.personId,
        message: 'Cuenta creada. Revisa tu email para verificar tu cuenta.',
      };
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[auth.register] error inesperado: ${(err as Error).message}`, (err as Error).stack);
      throw new RpcException({ status: 500, message: 'Error interno al registrar la cuenta' });
    }
  }
}
