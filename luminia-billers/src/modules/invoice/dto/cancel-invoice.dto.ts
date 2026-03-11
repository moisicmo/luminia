import { IsString, IsNotEmpty, IsNumber } from 'class-validator';

export class CancelInvoiceDto {
  @IsString()
  @IsNotEmpty()
  cuf: string;

  @IsNumber()
  cancellationReasonId: number;
}

export class CancellationReversalDto {
  @IsString()
  @IsNotEmpty()
  cuf: string;
}

export class CheckNitDto {
  @IsString()
  @IsNotEmpty()
  nit: string;

  @IsString()
  @IsNotEmpty()
  businessCode: string;
}
