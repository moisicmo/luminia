export class CreateBranchDto {
  name: string;
  region: string;
  address?: string;
  municipality?: string;
  phone?: string;
  latitude?: number;
  longitude?: number;
  openingHours?: string;
}

export class CreateBusinessDto {
  name: string;
  businessType: string;
  taxId?: string;
  url?: string;
  branch: CreateBranchDto;
}
