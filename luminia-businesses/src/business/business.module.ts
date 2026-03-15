import { Module } from '@nestjs/common';
import { BusinessController } from './business.controller';
import { BusinessService } from './business.service';
import { MemberService } from './member.service';
import { RoleService } from './role.service';
import { BranchService } from './branch.service';
import { PointOfSaleService } from './point-of-sale.service';
import { CashRegisterService } from './cash-register.service';
import { CustomerService } from './customer.service';
import { InvitationService } from './invitation.service';

@Module({
  controllers: [BusinessController],
  providers: [BusinessService, MemberService, RoleService, BranchService, PointOfSaleService, CashRegisterService, CustomerService, InvitationService],
})
export class BusinessModule {}
