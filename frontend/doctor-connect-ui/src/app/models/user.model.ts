import { Role } from './role.model';

export interface User {
  id?: string;
  emailOrPhoneNumber: string;
  password?: string;
  fullName?: string;
  phone?: string;
  email?: string;
  isActive?: boolean;
  isVerified?: boolean;
  roles?: Role[];
}
