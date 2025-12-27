import { PatientProfile } from './patient-profile.model';
import { Appointment } from './appointment.model';
import { Prescription } from './prescription.model';
import { MedicalRecord } from './medical-record.model';

export interface MedicalHistory {
  patient?: PatientProfile;
  appointments?: Appointment[];
  prescriptions?: Prescription[];
  medicalRecords?: MedicalRecord[];
  allergies?: string[];
  chronicDiseases?: string[];
  currentMedications?: string[];
  pastSurgeries?: string[];
  familyHistory?: string;
  notes?: string;
  bloodGroup?: string;
  lastUpdated?: string;
}
