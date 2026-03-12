import { Module } from '@nestjs/common';
import { BusinessController } from './business.controller';
import { BusinessService } from './business.service';
import { MemberService } from './member.service';
import { RoleService } from './role.service';

@Module({
  controllers: [BusinessController],
  providers: [BusinessService, MemberService, RoleService],
})
export class BusinessModule {}
