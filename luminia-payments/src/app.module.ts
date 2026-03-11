import { Module } from '@nestjs/common';
import { TransactionModule } from './transaction/transaction.module';
import { WalletModule } from './wallet/wallet.module';

@Module({
  imports: [TransactionModule, WalletModule],
})
export class AppModule {}
