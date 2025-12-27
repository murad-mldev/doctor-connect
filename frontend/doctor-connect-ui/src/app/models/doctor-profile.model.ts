import { User } from './user.model';
import { Department } from './department.model';

export interface DoctorProfile {
  id?: string;
  user?: User;
  description?: string;
  department?: Department;
  specialization?: string;
  designation?: string;
  qualifications?: string;
  licenseNumber?: string;
  approved?: boolean;
  stripeConnectAccountId?: string;
  stripeAccountStatus?: string;
  stripeOnboardingCompleted?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
