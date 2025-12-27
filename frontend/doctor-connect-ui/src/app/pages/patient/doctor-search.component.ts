import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  LucideAngularModule,
  Calendar,
  Clock,
  DollarSign,
  MapPin,
  X,
} from 'lucide-angular';
import {
  DoctorService,
  MetaService,
  ScheduleService,
  AppointmentService,
  UserService,
} from '../../services';
import { DoctorProfile, Department, ScheduleSlot } from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-doctor-search',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    LucideAngularModule,
    ModalComponent,
  ],
  templateUrl: './doctor-search.component.html',
  styleUrl: './doctor-search.component.scss',
})
export class DoctorSearchComponent implements OnInit {
  readonly Calendar = Calendar;
  readonly Clock = Clock;
  readonly DollarSign = DollarSign;
  readonly MapPin = MapPin;
  readonly X = X;

  doctors: DoctorProfile[] = [];
  departments: Department[] = [];
  loading = false;

  searchParams = {
    name: '',
    departmentId: '',
    location: '',
  };

  // View availability modal
  showAvailabilityModal = false;
  selectedDoctor: DoctorProfile | null = null;
  availableSlots: ScheduleSlot[] = [];
  loadingSlots = false;
  selectedDate = new Date().toISOString().split('T')[0];
  minDate = new Date().toISOString().split('T')[0];

  // Booking modal
  showBookingModal = false;
  selectedSlot: ScheduleSlot | null = null;
  bookingReason = '';
  bookingLoading = false;
  currentUserId = '';

  constructor(
    private doctorService: DoctorService,
    private metaService: MetaService,
    private scheduleService: ScheduleService,
    private appointmentService: AppointmentService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadDepartments();
    this.searchDoctors();
  }

  loadUserProfile(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.currentUserId = profile.id || '';
      },
      error: (error) => console.error('Failed to load user profile', error),
    });
  }

  loadDepartments(): void {
    this.metaService.getDepartments().subscribe({
      next: (departments) => {
        this.departments = departments;
      },
      error: (error) => console.error('Failed to load departments', error),
    });
  }

  searchDoctors(): void {
    this.loading = true;
    this.doctorService
      .searchDoctors(
        this.searchParams.departmentId,
        undefined,
        this.searchParams.name
      )
      .subscribe({
        next: (response) => {
          this.doctors = response.data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Failed to search doctors', error);
          this.loading = false;
        },
      });
  }

  viewAvailability(doctor: DoctorProfile): void {
    this.selectedDoctor = doctor;
    this.showAvailabilityModal = true;
    this.loadAvailableSlots();
  }

  loadAvailableSlots(): void {
    if (!this.selectedDoctor?.user?.id) return;

    this.loadingSlots = true;
    this.scheduleService
      .getDoctorSchedules(this.selectedDoctor.user.id, this.selectedDate)
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

  onDateChange(): void {
    this.loadAvailableSlots();
  }

  selectSlot(slot: ScheduleSlot): void {
    this.selectedSlot = slot;
    this.showAvailabilityModal = false;
    this.showBookingModal = true;
  }

  bookAppointment(): void {
    if (!this.selectedSlot?.id || !this.bookingReason.trim()) {
      alert('Please provide a reason for the appointment');
      return;
    }

    if (!this.selectedDoctor?.id || !this.currentUserId) {
      alert('Missing doctor or patient information');
      return;
    }

    this.bookingLoading = true;
    this.appointmentService
      .createAppointment({
        doctorId: this.selectedDoctor.id,
        slotId: this.selectedSlot.id,
        patientId: this.currentUserId,
        reason: this.bookingReason,
      })
      .subscribe({
        next: () => {
          alert('Appointment booked successfully!');
          this.closeBookingModal();
          this.bookingLoading = false;
        },
        error: (error) => {
          console.error('Failed to book appointment', error);
          alert('Failed to book appointment. Please try again.');
          this.bookingLoading = false;
        },
      });
  }

  closeAvailabilityModal(): void {
    this.showAvailabilityModal = false;
    this.selectedDoctor = null;
    this.availableSlots = [];
  }

  closeBookingModal(): void {
    this.showBookingModal = false;
    this.selectedSlot = null;
    this.bookingReason = '';
  }
}
