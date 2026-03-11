import {
  IsString,
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsArray,
  IsBoolean,
  IsObject,
} from 'class-validator';

export class IssueInvoiceDto {
  @IsString()
  @IsNotEmpty()
  businessCode: string;

  @IsNumber()
  branchOfficeSiat: number;

  @IsNumber()
  pointSaleSiat: number;

  @IsNumber()
  documentSectorType: number;

  @IsString()
  @IsOptional()
  email?: string;

  @IsBoolean()
  @IsOptional()
  useCurrencyType?: boolean;

  @IsString()
  @IsOptional()
  barcode?: string;

  @IsObject()
  @IsNotEmpty()
  header: Record<string, any>;

  @IsArray()
  @IsOptional()
  detail?: Record<string, any>[];
}
