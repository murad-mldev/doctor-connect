import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Clock, Plus, Edit, Trash2, Calendar, ChevronLeft, ChevronRight } from 'lucide-angular';
import { ScheduleService, DoctorProfileService } from '../../services';
import { ScheduleSlot, CreateScheduleSlotRequest } from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-doctor-schedule',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ModalComponent],
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.scss'
})
export class DoctorScheduleComponent implements OnInit {
  readonly Clock = Clock;
  readonly Plus = Plus;
  readonly Edit = Edit;
  readonly Trash2 = Trash2;
  readonly Calendar = Calendar;
  readonly ChevronLeft = ChevronLeft;
  readonly ChevronRight = ChevronRight;

  scheduleSlots: ScheduleSlot[] = [];
  isLoading = false;
  showModal = false;
  isEditMode = false;

  currentDate = new Date();
  selectedDate = '';
  doctorId = '';

  currentSlot: CreateScheduleSlotRequest = {
    date: '',
    startTime: '',
    endTime: '',
    capacity: 1
  };

  constructor(
    private scheduleService: ScheduleService,
    private doctorProfileService: DoctorProfileService
  ) {}

  ngOnInit(): void {
    this.loadDoctorProfile();
  }

  loadDoctorProfile(): void {
    this.doctorProfileService.getCurrentDoctorProfile().subscribe({
      next: (profile) => {
        this.doctorId = profile.id!;
        this.selectedDate = this.formatDateForAPI(this.currentDate);
        this.loadSchedules();
      },
      error: (error) => console.error('Failed to load doctor profile', error)
    });
  }

  loadSchedules(): void {
    if (!this.doctorId) return;

    this.isLoading = true;
    this.scheduleService.getDoctorSchedules(this.doctorId, this.selectedDate).subscribe({
      next: (slots) => {
        this.scheduleSlots = slots;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load schedules', error);
        this.isLoading = false;
      }
    });
  }

  previousDay(): void {
    this.currentDate.setDate(this.currentDate.getDate() - 1);
    this.selectedDate = this.formatDateForAPI(this.currentDate);
    this.loadSchedules();
  }

  nextDay(): void {
    this.currentDate.setDate(this.currentDate.getDate() + 1);
    this.selectedDate = this.formatDateForAPI(this.currentDate);
    this.loadSchedules();
  }

  today(): void {
    this.currentDate = new Date();
    this.selectedDate = this.formatDateForAPI(this.currentDate);
    this.loadSchedules();
  }

  addSchedule(): void {
    this.isEditMode = false;
    this.currentSlot = {
      date: this.selectedDate,
      startTime: '',
      endTime: '',
      capacity: 1
    };
    this.showModal = true;
  }

  editSchedule(slot: ScheduleSlot): void {
    this.isEditMode = true;
    this.currentSlot = {
      date: slot.date,
      startTime: slot.startTime,
      endTime: slot.endTime,
      capacity: slot.capacity
    };
    this.showModal = true;
  }

  saveSchedule(): void {
    if (!this.doctorId) return;

    if (this.isEditMode) {
      // Note: Backend expects slotId for update
      const slotId = this.scheduleSlots.find(s =>
        s.date === this.currentSlot.date &&
        s.startTime === this.currentSlot.startTime
      )?.id;

      if (!slotId) {
        console.error('Slot ID not found');
        return;
      }

      this.scheduleService.updateScheduleSlot(this.doctorId, slotId, this.currentSlot).subscribe({
        next: () => {
          this.loadSchedules();
          this.closeModal();
        },
        error: (error) => console.error('Failed to update schedule', error)
      });
    } else {
      this.scheduleService.createScheduleSlot(this.doctorId, this.currentSlot).subscribe({
        next: () => {
          this.loadSchedules();
          this.closeModal();
        },
        error: (error) => console.error('Failed to create schedule', error)
      });
    }
  }

  deleteSchedule(slot: ScheduleSlot): void {
    if (!this.doctorId || !slot.id) return;

    if (confirm(`Are you sure you want to delete this time slot?`)) {
      this.scheduleService.deleteScheduleSlot(this.doctorId, slot.id).subscribe({
        next: () => {
          this.loadSchedules();
        },
        error: (error) => console.error('Failed to delete schedule', error)
      });
    }
  }

  closeModal(): void {
    this.showModal = false;
  }

  formatDateForAPI(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatDateForDisplay(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatTime(time: string): string {
    return time;
  }
}
