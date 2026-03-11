import { ArgumentsHost, Catch, ExceptionFilter, HttpException, HttpStatus } from '@nestjs/common';
import { Response } from 'express';

@Catch()
export class RpcExceptionFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();

    // Errores HTTP nativos de NestJS (ValidationPipe, etc.)
    if (exception instanceof HttpException) {
      return response
        .status(exception.getStatus())
        .json(exception.getResponse());
    }

    // Errores RPC: llegan como objeto plano { status, message }
    const rpcError = this.extractRpcError(exception);
    if (rpcError) {
      return response
        .status(rpcError.status)
        .json({ statusCode: rpcError.status, message: rpcError.message });
    }

    // Fallback
    return response
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .json({ statusCode: 500, message: 'Internal server error' });
  }

  private extractRpcError(exception: unknown): { status: number; message: string } | null {
    // Caso 1: objeto directo { status, message }
    if (
      typeof exception === 'object' &&
      exception !== null &&
      'status' in exception &&
      'message' in exception
    ) {
      const e = exception as { status: unknown; message: unknown };
      if (typeof e.status === 'number' && typeof e.message === 'string') {
        return { status: e.status, message: e.message };
      }
    }

    // Caso 2: Error con message JSON stringificado
    if (exception instanceof Error) {
      try {
        const parsed = JSON.parse(exception.message);
        if (typeof parsed.status === 'number' && typeof parsed.message === 'string') {
          return { status: parsed.status, message: parsed.message };
        }
      } catch {
        return { status: HttpStatus.INTERNAL_SERVER_ERROR, message: exception.message };
      }
    }

    return null;
  }
}
