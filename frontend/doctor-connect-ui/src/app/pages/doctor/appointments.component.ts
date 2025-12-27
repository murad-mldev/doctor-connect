import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  Calendar,
  Eye,
  CheckCircle,
  XCircle,
  Clock,
  FileText,
  Filter,
} from 'lucide-angular';
import {
  AppointmentService,
  UserService,
  PrescriptionService,
} from '../../services';
import {
  Appointment,
  AppointmentStatus,
  CreatePrescriptionRequest,
  Prescription,
} from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-doctor-appointments',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ModalComponent],
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.scss',
})
export class DoctorAppointmentsComponent implements OnInit {
  readonly Calendar = Calendar;
  readonly Eye = Eye;
  readonly CheckCircle = CheckCircle;
  readonly XCircle = XCircle;
  readonly Clock = Clock;
  readonly FileText = FileText;
  readonly Filter = Filter;
  readonly AppointmentStatus = AppointmentStatus;

  appointments: Appointment[] = [];
  filteredAppointments: Appointment[] = [];
  isLoading = false;

  // Filters
  selectedStatus: AppointmentStatus | '' = '';
  fromDate = '';
  toDate = '';

  // Modals
  showDetailsModal = false;
  showPrescriptionModal = false;
  selectedAppointment: Appointment | null = null;

  // Prescription form
  prescriptionForm: CreatePrescriptionRequest = {
    notes: '',
    medicines: [],
    tests: [],
  };

  currentUserId = '';

  constructor(
    private appointmentService: AppointmentService,
    private userService: UserService,
    private prescriptionService: PrescriptionService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.currentUserId = profile.id || '';
        this.loadAppointments();
      },
      error: (error) => console.error('Failed to load user profile', error),
    });
  }

  loadAppointments(): void {
    if (!this.currentUserId) return;

    this.isLoading = true;
    const status = this.selectedStatus || undefined;
    const from = this.fromDate || undefined;
    const to = this.toDate || undefined;

    this.appointmentService
      .getUserAppointments(this.currentUserId, status, from, to)
      .subscribe({
        next: (response) => {
          this.appointments = response.data;
          this.filteredAppointments = response.data;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Failed to load appointments', error);
          this.isLoading = false;
        },
      });
  }

  applyFilters(): void {
    this.loadAppointments();
  }

  clearFilters(): void {
    this.selectedStatus = '';
    this.fromDate = '';
    this.toDate = '';
    this.loadAppointments();
  }

  viewDetails(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.showDetailsModal = true;
  }

  updateStatus(appointmentId: string, status: AppointmentStatus): void {
    this.appointmentService
      .updateAppointmentStatus(appointmentId, { status })
      .subscribe({
        next: () => {
          this.loadAppointments();
          this.closeDetailsModal();
        },
        error: (error) => console.error('Failed to update status', error),
      });
  }

  openPrescriptionModal(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.prescriptionForm = {
      notes: '',
      medicines: [],
      tests: [],
    };
    this.showPrescriptionModal = true;
  }

  addMedicine(): void {
    this.prescriptionForm.medicines?.push({
      name: '',
      dosage: '',
      duration: '',
      instructions: '',
    });
  }

  removeMedicine(index: number): void {
    this.prescriptionForm.medicines?.splice(index, 1);
  }

  addLabTest(): void {
    this.prescriptionForm.tests?.push({
      name: '',
      instructions: '',
    });
  }

  removeLabTest(index: number): void {
    this.prescriptionForm.tests?.splice(index, 1);
  }

  submitPrescription(): void {
    if (!this.selectedAppointment?.id) return;

    this.prescriptionService
      .createPrescription(this.selectedAppointment.id, this.prescriptionForm)
      .subscribe({
        next: () => {
          this.loadAppointments();
          this.closePrescriptionModal();
        },
        error: (error) => console.error('Failed to create prescription', error),
      });
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedAppointment = null;
  }

  closePrescriptionModal(): void {
    this.showPrescriptionModal = false;
    this.selectedAppointment = null;
  }

  getStatusClass(status?: string): string {
    switch (status) {
      case 'SCHEDULED':
        return 'status-scheduled';
      case 'IN_PROGRESS':
        return 'status-in-progress';
      case 'COMPLETED':
        return 'status-completed';
      case 'CANCELLED':
        return 'status-cancelled';
      case 'NO_SHOW':
        return 'status-no-show';
      default:
        return '';
    }
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleString();
  }
}
