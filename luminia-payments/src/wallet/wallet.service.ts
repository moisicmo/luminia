import { Injectable, Logger } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { prisma } from '@/lib/prisma';


@Injectable()
export class WalletService {
  private readonly logger = new Logger(WalletService.name);

  async getOrCreate(
    businessId: string,
    ownerId: string,
    ownerType: string,
    currency?: string,
  ) {
    try {
      const curr = currency ?? 'BOB';
      return await prisma.wallet.upsert({
        where: {
          businessId_ownerId_ownerType_currency: {
            businessId,
            ownerId,
            ownerType: ownerType as any,
            currency: curr,
          },
        },
        update: {},
        create: {
          businessId,
          ownerId,
          ownerType: ownerType as any,
          currency: curr,
          balance: 0,
        },
      });
    } catch (err) {
      this.logger.error(`[wallet.getOrCreate] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener o crear billetera' });
    }
  }

  async getWallet(
    businessId: string,
    ownerId: string,
    ownerType: string,
    currency?: string,
  ) {
    try {
      const wallet = await prisma.wallet.findFirst({
        where: {
          businessId,
          ownerId,
          ownerType: ownerType as any,
          currency: currency ?? 'BOB',
          active: true,
        },
      });
      if (!wallet) {
        throw new RpcException({ status: 404, message: 'Billetera no encontrada' });
      }
      return wallet;
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[wallet.getWallet] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al obtener billetera' });
    }
  }

  async credit(
    walletId: string,
    amount: number,
    description: string,
    createdBy: string,
    transactionId?: string,
  ) {
    try {
      return await prisma.$transaction(async (tx) => {
        const wallet = await tx.wallet.findUnique({ where: { id: walletId } });
        if (!wallet) {
          throw new RpcException({ status: 404, message: 'Billetera no encontrada' });
        }

        const balanceBefore = Number(wallet.balance);
        const balanceAfter = balanceBefore + Number(amount);

        await tx.walletMovement.create({
          data: {
            walletId,
            transactionId,
            type: 'CREDIT',
            amount,
            balanceBefore,
            balanceAfter,
            description,
            createdBy,
          },
        });

        return await tx.wallet.update({
          where: { id: walletId },
          data: { balance: balanceAfter },
        });
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[wallet.credit] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al acreditar billetera' });
    }
  }

  async debit(
    walletId: string,
    amount: number,
    description: string,
    createdBy: string,
    transactionId?: string,
  ) {
    try {
      return await prisma.$transaction(async (tx) => {
        const wallet = await tx.wallet.findUnique({ where: { id: walletId } });
        if (!wallet) {
          throw new RpcException({ status: 404, message: 'Billetera no encontrada' });
        }

        const balanceBefore = Number(wallet.balance);
        const balanceAfter = balanceBefore - Number(amount);

        if (balanceBefore < Number(amount)) {
          throw new RpcException({ status: 400, message: 'Saldo insuficiente en la billetera' });
        }

        await tx.walletMovement.create({
          data: {
            walletId,
            transactionId,
            type: 'DEBIT',
            amount,
            balanceBefore,
            balanceAfter,
            description,
            createdBy,
          },
        });

        return await tx.wallet.update({
          where: { id: walletId },
          data: { balance: balanceAfter },
        });
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[wallet.debit] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al debitar billetera' });
    }
  }

  async listMovements(walletId: string, businessId: string) {
    try {
      const wallet = await prisma.wallet.findFirst({
        where: { id: walletId, businessId },
      });
      if (!wallet) {
        throw new RpcException({ status: 404, message: 'Billetera no encontrada' });
      }

      return await prisma.walletMovement.findMany({
        where: { walletId },
        orderBy: { createdAt: 'desc' },
        take: 50,
      });
    } catch (err) {
      if (err instanceof RpcException) throw err;
      this.logger.error(`[wallet.listMovements] ${(err as Error).message}`);
      throw new RpcException({ status: 500, message: 'Error al listar movimientos de billetera' });
    }
  }
}
