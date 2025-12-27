import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideAngularModule,
  Activity,
  FileText,
  Calendar,
  Clipboard,
  Eye,
  X,
  AlertCircle,
} from 'lucide-angular';
import { MedicalHistoryService, UserService } from '../../services';
import { MedicalHistory, Appointment, Prescription } from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-medical-history',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, ModalComponent],
  templateUrl: './medical-history.component.html',
  styleUrl: './medical-history.component.scss',
})
export class MedicalHistoryComponent implements OnInit {
  readonly Activity = Activity;
  readonly FileText = FileText;
  readonly Calendar = Calendar;
  readonly Clipboard = Clipboard;
  readonly Eye = Eye;
  readonly X = X;
  readonly AlertCircle = AlertCircle;

  medicalHistory: MedicalHistory | null = null;
  loading = false;
  currentUserId = '';

  // View modals
  showAppointmentModal = false;
  showPrescriptionModal = false;
  selectedAppointment: Appointment | null = null;
  selectedPrescription: Prescription | null = null;

  constructor(
    private medicalHistoryService: MedicalHistoryService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.currentUserId = profile.id || '';
        this.loadMedicalHistory();
      },
      error: (error) => console.error('Failed to load user profile', error),
    });
  }

  loadMedicalHistory(): void {
    if (!this.currentUserId) return;

    this.loading = true;
    this.medicalHistoryService
      .getPatientMedicalHistory(this.currentUserId)
      .subscribe({
        next: (history) => {
          this.medicalHistory = history;
          this.loading = false;
        },
        error: (error) => {
          console.error('Failed to load medical history', error);
          this.loading = false;
        },
      });
  }

  viewAppointmentDetails(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.showAppointmentModal = true;
  }

  closeAppointmentModal(): void {
    this.showAppointmentModal = false;
    this.selectedAppointment = null;
  }

  viewPrescriptionDetails(prescription: Prescription): void {
    this.selectedPrescription = prescription;
    this.showPrescriptionModal = true;
  }

  closePrescriptionModal(): void {
    this.showPrescriptionModal = false;
    this.selectedPrescription = null;
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString();
  }

  getStatusClass(status: string | undefined): string {
    if (!status) return '';
    return `status-${status.toLowerCase()}`;
  }
}
