import { DoctorProfile } from './doctor-profile.model';

export interface DoctorSearchResponse {
  doctor: DoctorProfile;
  hasAvailability: boolean;
}

export interface DoctorAvailabilityResponse {
  available: boolean;
}
