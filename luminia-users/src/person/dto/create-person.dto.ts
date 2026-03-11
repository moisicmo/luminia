export interface CreatePersonDto {
  numberDocument: string;
  typeDocument: 'dni' | 'nit' | 'pasaporte' | 'otro';
  name: string;
  lastName: string;
  birthDate?: string;
  gender?: string;
  nationality?: string;
  address?: string;
}
