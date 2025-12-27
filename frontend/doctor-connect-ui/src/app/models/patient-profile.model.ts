import { User } from './user.model';

export interface PatientProfile {
  id?: string;
  user?: User;
  dateOfBirth?: string;
  gender?: string;
  bloodGroup?: string;
  address?: string;
  emergencyContact?: string;
  createdAt?: string;
  updatedAt?: string;
}
