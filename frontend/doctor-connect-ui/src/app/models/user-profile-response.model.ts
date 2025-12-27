import { Role } from './role.model';

// UserProfileResponse matches backend UserDto structure
export interface UserProfileResponse {
  id?: string;
  emailOrPhoneNumber: string;
  fullName?: string;
  phone?: string;
  email?: string;
  isActive?: boolean;
  isVerified?: boolean;
  roles?: Role[];
}

export interface PublicUserResponse {
  id: string;
  fullName: string;
  isVerified: boolean;
}
