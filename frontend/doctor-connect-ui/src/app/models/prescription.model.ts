import { Appointment } from './appointment.model';
import { DoctorProfile } from './doctor-profile.model';
import { PatientProfile } from './patient-profile.model';

export interface PrescriptionMedicine {
  id?: string;
  name: string;
  dosage?: string;
  duration?: string;
  instructions?: string;
  genericName?: string;
  type?: string;
}

export interface PrescriptionTest {
  id?: string;
  name: string;
  instructions?: string;
  description?: string;
}

export interface Prescription {
  id?: string;
  appointment?: Appointment;
  doctor?: DoctorProfile;
  patient?: PatientProfile;
  notes?: string;
  medicines?: PrescriptionMedicine[];
  tests?: PrescriptionTest[];
  labTests?: PrescriptionTest[];
  pdfFileId?: string;
  prescriptionDate?: string;
  diagnosis?: string;
  followUpDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreatePrescriptionRequest {
  notes?: string;
  medicines?: PrescriptionMedicine[];
  tests?: PrescriptionTest[];
  idempotencyKey?: string;
}
