import { AppointmentStatus } from './appointment-status.enum';
import { ScheduleSlot } from './schedule-slot.model';
import { DoctorProfile } from './doctor-profile.model';
import { PatientProfile } from './patient-profile.model';

export interface Appointment {
  id?: string;
  slot?: ScheduleSlot;
  doctor?: DoctorProfile;
  patient?: PatientProfile;
  status?: AppointmentStatus;
  appointmentTime?: string;
  reason?: string;
  fee?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateAppointmentRequest {
  doctorId: string;
  slotId: string;
  patientId: string;
  reason?: string;
  idempotencyKey?: string;
}

export interface UpdateAppointmentStatusRequest {
  status: AppointmentStatus;
}

export interface CancelAppointmentRequest {
  cancellationReason?: string;
}

export interface RescheduleAppointmentRequest {
  newSlotId: string;
}
