export declare class CreateBranchDto {
    name: string;
    region: string;
    address?: string;
    municipality?: string;
    phone?: string;
    latitude?: number;
    longitude?: number;
    openingHours?: string;
}
export declare class CreateBusinessDto {
    name: string;
    businessType: string;
    taxId?: string;
    url?: string;
    branch: CreateBranchDto;
}
