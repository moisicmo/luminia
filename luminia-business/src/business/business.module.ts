import { Module } from '@nestjs/common';
import { BusinessController } from './business.controller';
import { BusinessService } from './business.service';
import { MemberService } from './member.service';

@Module({
  controllers: [BusinessController],
  providers: [BusinessService, MemberService],
})
export class BusinessModule {}
