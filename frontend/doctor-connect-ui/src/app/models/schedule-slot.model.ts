import { DoctorProfile } from './doctor-profile.model';

export interface ScheduleSlot {
  id?: string;
  doctor?: DoctorProfile;
  date: string;
  startTime: string;
  endTime: string;
  capacity: number;
  bookedCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateScheduleSlotRequest {
  date: string;
  startTime: string;
  endTime: string;
  capacity: number;
}
