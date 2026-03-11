import { Module } from '@nestjs/common';
import { ScheduleModule } from '@nestjs/schedule';
import { CheckInvoiceSiatTask } from './tasks/check-invoice-siat.task';
import { ExpirationCertificateTask } from './tasks/expiration-certificate.task';
import { SyncCodesTask } from './tasks/sync-codes.task';
import { SyncModule } from '../sync/sync.module';

@Module({
  imports: [ScheduleModule.forRoot(), SyncModule],
  providers: [CheckInvoiceSiatTask, ExpirationCertificateTask, SyncCodesTask],
})
export class SchedulerModule {}
