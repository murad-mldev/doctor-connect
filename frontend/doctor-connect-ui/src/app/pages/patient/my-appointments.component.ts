import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  Calendar,
  Clock,
  X,
  MapPin,
  DollarSign,
  Video,
} from 'lucide-angular';
import {
  AppointmentService,
  UserService,
  ScheduleService,
} from '../../services';
import {
  Appointment,
  UserProfileResponse,
  ScheduleSlot,
  AppointmentStatus,
} from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-my-appointments',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    LucideAngularModule,
    ModalComponent,
  ],
  templateUrl: './my-appointments.component.html',
  styleUrl: './my-appointments.component.scss',
})
export class MyAppointmentsComponent implements OnInit {
  readonly Calendar = Calendar;
  readonly Clock = Clock;
  readonly X = X;
  readonly MapPin = MapPin;
  readonly DollarSign = DollarSign;
  readonly Video = Video;
  readonly AppointmentStatus = AppointmentStatus;

  appointments: Appointment[] = [];
  filteredAppointments: Appointment[] = [];
  loading = false;
  userProfile?: UserProfileResponse;

  // Filters
  statusFilter: AppointmentStatus | '' = '';
  fromDate = '';
  toDate = '';

  // View details modal
  showDetailsModal = false;
  selectedAppointment: Appointment | null = null;

  // Reschedule modal
  showRescheduleModal = false;
  availableSlots: ScheduleSlot[] = [];
  loadingSlots = false;
  rescheduleDate = new Date().toISOString().split('T')[0];
  minDate = new Date().toISOString().split('T')[0];
  selectedNewSlot: ScheduleSlot | null = null;

  constructor(
    private appointmentService: AppointmentService,
    private userService: UserService,
    private scheduleService: ScheduleService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.loadAppointments();
      },
      error: (error) => console.error('Failed to load profile', error),
    });
  }

  loadAppointments(): void {
    if (!this.userProfile?.id) return;

    this.loading = true;
    this.appointmentService
      .getUserAppointments(
        this.userProfile.id,
        this.statusFilter || undefined,
        this.fromDate,
        this.toDate
      )
      .subscribe({
        next: (response) => {
          this.appointments = response.data;
          this.filteredAppointments = response.data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Failed to load appointments', error);
          this.loading = false;
        },
      });
  }

  applyFilters(): void {
    this.loadAppointments();
  }

  clearFilters(): void {
    this.statusFilter = '';
    this.fromDate = '';
    this.toDate = '';
    this.loadAppointments();
  }

  viewDetails(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedAppointment = null;
  }

  cancelAppointment(appointmentId: string): void {
    if (confirm('Are you sure you want to cancel this appointment?')) {
      this.appointmentService
        .cancelAppointment(appointmentId, { cancellationReason: 'Patient requested cancellation' })
        .subscribe({
          next: () => {
            alert('Appointment cancelled successfully');
            this.closeDetailsModal();
            this.loadAppointments();
          },
          error: (error) => {
            console.error('Failed to cancel appointment', error);
            alert('Failed to cancel appointment. Please try again.');
          },
        });
    }
  }

  openRescheduleModal(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.showDetailsModal = false;
    this.showRescheduleModal = true;
    this.loadAvailableSlotsForReschedule();
  }

  loadAvailableSlotsForReschedule(): void {
    if (!this.selectedAppointment?.doctor?.user?.id) return;

    this.loadingSlots = true;
    this.scheduleService
      .getDoctorSchedules(
        this.selectedAppointment.doctor.user.id,
        this.rescheduleDate
      )
      .subscribe({
        next: (slots) => {
          this.availableSlots = slots.filter((slot) => (slot.bookedCount || 0) < slot.capacity);
          this.loadingSlots = false;
        },
        error: (error) => {
          console.error('Failed to load slots', error);
          this.loadingSlots = false;
        },
      });
  }

  onRescheduleDateChange(): void {
    this.loadAvailableSlotsForReschedule();
  }

  rescheduleAppointment(): void {
    if (!this.selectedAppointment?.id || !this.selectedNewSlot?.id) {
      alert('Please select a new slot');
      return;
    }

    this.appointmentService
      .rescheduleAppointment(this.selectedAppointment.id, {
        newSlotId: this.selectedNewSlot.id,
      })
      .subscribe({
        next: () => {
          alert('Appointment rescheduled successfully!');
          this.closeRescheduleModal();
          this.loadAppointments();
        },
        error: (error) => {
          console.error('Failed to reschedule appointment', error);
          alert('Failed to reschedule appointment. Please try again.');
        },
      });
  }

  closeRescheduleModal(): void {
    this.showRescheduleModal = false;
    this.selectedAppointment = null;
    this.selectedNewSlot = null;
    this.availableSlots = [];
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
      default:
        return '';
    }
  }
}
