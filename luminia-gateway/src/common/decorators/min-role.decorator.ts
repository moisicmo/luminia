import { SetMetadata } from '@nestjs/common';

export enum MemberRole {
  VIEWER    = 'VIEWER',
  INVENTORY = 'INVENTORY',
  SELLER    = 'SELLER',
  MANAGER   = 'MANAGER',
  ADMIN     = 'ADMIN',
  OWNER     = 'OWNER',
}

// Numeric weight — higher = more permissions
export const ROLE_WEIGHT: Record<MemberRole, number> = {
  [MemberRole.VIEWER]:    10,
  [MemberRole.INVENTORY]: 30,
  [MemberRole.SELLER]:    40,
  [MemberRole.MANAGER]:   60,
  [MemberRole.ADMIN]:     80,
  [MemberRole.OWNER]:     100,
};

export const MIN_ROLE_KEY = 'minRole';
export const MinRole = (role: MemberRole) => SetMetadata(MIN_ROLE_KEY, role);
