import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LucideAngularModule, ArrowLeft, Calendar, Clock, MapPin, Award, ChevronLeft, ChevronRight } from 'lucide-angular';
import { PublicService, ScheduleService, UserService } from '../../services';
import { DoctorProfile, ScheduleSlot } from '../../models';

@Component({
  selector: 'app-doctor-details',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  templateUrl: './doctor-details.component.html',
  styleUrl: './doctor-details.component.scss',
})
export class DoctorDetailsComponent implements OnInit {
  readonly ArrowLeft = ArrowLeft;
  readonly Calendar = Calendar;
  readonly Clock = Clock;
  readonly MapPin = MapPin;
  readonly Award = Award;
  readonly ChevronLeft = ChevronLeft;
  readonly ChevronRight = ChevronRight;

  doctor: DoctorProfile | null = null;
  availableSlots: ScheduleSlot[] = [];
  isLoading = false;
  isLoadingSlots = false;

  // Date selection
  currentDate = new Date();
  selectedDate: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private publicService: PublicService,
    private scheduleService: ScheduleService,
    private userService: UserService
  ) {
    this.selectedDate = this.formatDate(this.currentDate);
  }

  ngOnInit(): void {
    const doctorId = this.route.snapshot.paramMap.get('id');
    if (doctorId) {
      this.loadDoctorDetails(doctorId);
      this.loadAvailableSlots(doctorId);
    }
  }

  loadDoctorDetails(doctorId: string): void {
    this.isLoading = true;
    this.publicService.getDoctorById(doctorId).subscribe({
      next: (doctor) => {
        this.doctor = doctor;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load doctor', error);
        this.isLoading = false;
        this.router.navigate(['/browse-doctors']);
      }
    });
  }

  loadAvailableSlots(doctorId: string): void {
    this.isLoadingSlots = true;
    this.scheduleService.getAvailableSchedules(doctorId, this.selectedDate).subscribe({
      next: (slots) => {
        this.availableSlots = slots;
        this.isLoadingSlots = false;
      },
      error: (error) => {
        console.error('Failed to load slots', error);
        this.isLoadingSlots = false;
      }
    });
  }

  bookAppointment(slot: ScheduleSlot): void {
    // Check if user is logged in
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        // User is logged in, navigate to booking page with slot info
        this.router.navigate(['/book-appointment'], {
          queryParams: {
            doctorId: this.doctor?.id,
            slotId: slot.id,
            date: slot.date,
            time: `${slot.startTime} - ${slot.endTime}`
          }
        });
      },
      error: () => {
        // User is not logged in, redirect to login with return URL
        const returnUrl = `/doctors/${this.doctor?.id}`;
        this.router.navigate(['/login'], {
          queryParams: { returnUrl }
        });
      }
    });
  }

  previousDay(): void {
    this.currentDate.setDate(this.currentDate.getDate() - 1);
    this.selectedDate = this.formatDate(this.currentDate);
    this.loadAvailableSlots(this.doctor!.id!);
  }

  nextDay(): void {
    this.currentDate.setDate(this.currentDate.getDate() + 1);
    this.selectedDate = this.formatDate(this.currentDate);
    this.loadAvailableSlots(this.doctor!.id!);
  }

  today(): void {
    this.currentDate = new Date();
    this.selectedDate = this.formatDate(this.currentDate);
    this.loadAvailableSlots(this.doctor!.id!);
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatDateDisplay(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  goBack(): void {
    this.router.navigate(['/browse-doctors']);
  }
}
