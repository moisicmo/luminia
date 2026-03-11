import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { WalletService } from './wallet.service';

@Controller()
export class WalletController {
  constructor(private readonly walletService: WalletService) {}

  @MessagePattern('payments.wallet.getOrCreate')
  getOrCreate(
    @Payload()
    d: {
      businessId: string;
      ownerId: string;
      ownerType: string;
      currency?: string;
    },
  ) {
    return this.walletService.getOrCreate(d.businessId, d.ownerId, d.ownerType, d.currency);
  }

  @MessagePattern('payments.wallet.get')
  getWallet(
    @Payload()
    d: {
      businessId: string;
      ownerId: string;
      ownerType: string;
      currency?: string;
    },
  ) {
    return this.walletService.getWallet(d.businessId, d.ownerId, d.ownerType, d.currency);
  }

  @MessagePattern('payments.wallet.credit')
  credit(
    @Payload()
    d: {
      walletId: string;
      amount: number;
      description: string;
      createdBy: string;
      transactionId?: string;
    },
  ) {
    return this.walletService.credit(
      d.walletId,
      d.amount,
      d.description,
      d.createdBy,
      d.transactionId,
    );
  }

  @MessagePattern('payments.wallet.debit')
  debit(
    @Payload()
    d: {
      walletId: string;
      amount: number;
      description: string;
      createdBy: string;
      transactionId?: string;
    },
  ) {
    return this.walletService.debit(
      d.walletId,
      d.amount,
      d.description,
      d.createdBy,
      d.transactionId,
    );
  }

  @MessagePattern('payments.wallet.movements')
  listMovements(@Payload() d: { walletId: string; businessId: string }) {
    return this.walletService.listMovements(d.walletId, d.businessId);
  }
}
