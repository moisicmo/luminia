export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthModel {
  token: string;
  name: string;
  lastName: string;
  email?: string;
  role: RoleModel;
  branches: BranchModel[];
}

export interface RoleModel {
  id: string;
  name: string;
  permissions: PermissionModel[];
}

export interface PermissionModel {
  id: string;
  action: string;
  subject: string;
}

export interface BranchModel {
  id: string;
  name: string;
  address: AddressModel;
  phone: string[];
}

export interface AddressModel {
  city: string;
  zone: string;
  detail: string;
}

export interface ValidatePinRequest {
  idUser: string;
  pin: string;
}

export interface UpdateProfileRequest {
  name: string;
  lastName: string;
  email?: string;
}

export interface UpdatePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ForgotPasswordRequest {
  email: string;
}
