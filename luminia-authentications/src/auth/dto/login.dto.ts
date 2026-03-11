export interface LoginDto {
  typeAuth: 'EMAIL' | 'PHONE' | 'GOOGLE' | 'FACEBOOK' | 'APPLE';
  // identifier = email, phone, or OAuth token
  identifier: string;
  password?: string;
  fcmToken?: string;
  systemId: string;
  userAgent?: string;
  ipAddress?: string;
}
