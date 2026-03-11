export interface RegisterAuthDto {
  personId: string;
  typeAuth: 'EMAIL' | 'PHONE' | 'GOOGLE' | 'FACEBOOK' | 'APPLE';
  // identifier = email, phone number, or OAuth token
  identifier: string;
  password: string;
  username?: string;
}
