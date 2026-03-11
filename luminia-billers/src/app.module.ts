import { Module } from '@nestjs/common';
import { PrismaModule } from './lib/prisma.module';
import { SiatModule } from './modules/siat/siat.module';
import { SignatureModule } from './modules/signature/signature.module';
import { InvoiceModule } from './modules/invoice/invoice.module';
import { SyncModule } from './modules/sync/sync.module';
import { BranchOfficeModule } from './modules/branch-office/branch-office.module';
import { PointSaleModule } from './modules/point-sale/point-sale.module';
import { WrapperModule } from './modules/wrapper/wrapper.module';
import { SchedulerModule } from './modules/scheduler/scheduler.module';

@Module({
  imports: [
    // Infrastructure (global)
    PrismaModule,
    SiatModule,
    SignatureModule,

    // Domain modules
    BranchOfficeModule,
    PointSaleModule,
    InvoiceModule,
    SyncModule,
    WrapperModule,

    // Scheduled tasks
    SchedulerModule,
  ],
})
export class AppModule {}
