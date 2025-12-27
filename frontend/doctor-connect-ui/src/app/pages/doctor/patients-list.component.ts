import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Users, Eye, FileText, Calendar } from 'lucide-angular';
import { AppointmentService } from '../../services/appointment.service';
import { MedicalHistoryService } from '../../services/medical-history.service';
import { DoctorProfileService } from '../../services/doctor-profile.service';
import { UserService } from '../../services/user.service';

interface PatientSummary {
  patientId: string;
  patientName: string;
  lastVisit: Date;
  totalVisits: number;
  upcomingAppointments: number;
}

@Component({
  selector: 'app-patients-list',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  templateUrl: './patients-list.component.html',
  styleUrl: './patients-list.component.scss'
})
export class PatientsListComponent implements OnInit {
  readonly Users = Users;
  readonly Eye = Eye;
  readonly FileText = FileText;
  readonly Calendar = Calendar;

  patients: PatientSummary[] = [];
  isLoading = false;
  error: string | null = null;
  selectedPatient: PatientSummary | null = null;
  showMedicalHistoryModal = false;
  medicalHistory: any = null;
  loadingHistory = false;

  constructor(
    private appointmentService: AppointmentService,
    private medicalHistoryService: MedicalHistoryService,
    private userService: UserService,
    private doctorProfileService: DoctorProfileService
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.isLoading = true;
    this.error = null;

    // Get current doctor's user ID
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        // Get doctor's appointments to extract patients
        this.appointmentService.getUserAppointments(user.id!).subscribe({
          next: (response) => {
            // Group appointments by patient
            const patientMap = new Map<string, PatientSummary>();

            response.data.forEach((apt: any) => {
              const patientId = apt.patient?.id || apt.patient?.user?.id;
              const patientName = apt.patient?.user?.fullName || 'Unknown Patient';

              if (patientId) {
                if (!patientMap.has(patientId)) {
                  patientMap.set(patientId, {
                    patientId,
                    patientName,
                    lastVisit: new Date(apt.appointmentDate),
                    totalVisits: 1,
                    upcomingAppointments: apt.status === 'CONFIRMED' ? 1 : 0
                  });
                } else {
                  const patient = patientMap.get(patientId)!;
                  patient.totalVisits++;
                  if (apt.status === 'CONFIRMED') {
                    patient.upcomingAppointments++;
                  }
                  const aptDate = new Date(apt.appointmentDate);
                  if (aptDate > patient.lastVisit) {
                    patient.lastVisit = aptDate;
                  }
                }
              }
            });

            this.patients = Array.from(patientMap.values()).sort((a, b) =>
              b.lastVisit.getTime() - a.lastVisit.getTime()
            );
            this.isLoading = false;
          },
          error: (error) => {
            this.error = 'Failed to load patients';
            console.error('Error loading patients:', error);
            this.isLoading = false;
          }
        });
      },
      error: (error) => {
        this.error = 'Failed to load user information';
        console.error('Error loading user:', error);
        this.isLoading = false;
      }
    });
  }

  viewMedicalHistory(patient: PatientSummary): void {
    this.selectedPatient = patient;
    this.showMedicalHistoryModal = true;
    this.loadingHistory = true;
    this.medicalHistory = null;

    this.medicalHistoryService.getPatientMedicalHistory(patient.patientId).subscribe({
      next: (history) => {
        this.medicalHistory = history;
        this.loadingHistory = false;
      },
      error: (error) => {
        this.error = 'Failed to load medical history';
        console.error('Error loading medical history:', error);
        this.loadingHistory = false;
      }
    });
  }

  closeMedicalHistoryModal(): void {
    this.showMedicalHistoryModal = false;
    this.selectedPatient = null;
    this.medicalHistory = null;
  }
}
