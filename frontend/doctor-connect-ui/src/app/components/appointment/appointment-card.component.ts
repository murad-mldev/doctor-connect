import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Appointment } from '../../models';

@Component({
  selector: 'app-appointment-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './appointment-card.component.html',
  styleUrl: './appointment-card.component.scss',
})
export class AppointmentCardComponent {
  @Input() appointment!: Appointment;
  @Input() isDoctor = false;
  @Input() showViewDetails = true;
  @Input() showJoinVideo = true;
  @Input() showPrescription = true;
  @Input() showCancel = true;
  @Input() showReschedule = true;
  @Input() showComplete = false;

  @Output() viewDetails = new EventEmitter<Appointment>();
  @Output() viewPrescription = new EventEmitter<Appointment>();
  @Output() cancel = new EventEmitter<Appointment>();
  @Output() reschedule = new EventEmitter<Appointment>();
  @Output() complete = new EventEmitter<Appointment>();

  getInitial(): string {
    if (this.isDoctor && this.appointment.patient?.user?.fullName) {
      return this.appointment.patient.user.fullName.charAt(0).toUpperCase();
    }
    if (!this.isDoctor && this.appointment.doctor?.user?.fullName) {
      return this.appointment.doctor.user.fullName.charAt(0).toUpperCase();
    }
    return 'U';
  }

  getStatusClass(): string {
    const statusClasses: Record<string, string> = {
      'PENDING': 'bg-yellow-100 text-yellow-800',
      'CONFIRMED': 'bg-green-100 text-green-800',
      'PATIENT_WAITING': 'bg-orange-100 text-orange-800',
      'IN_PROGRESS': 'bg-blue-100 text-blue-800',
      'COMPLETED': 'bg-gray-100 text-gray-800',
      'CANCELLED': 'bg-red-100 text-red-800',
      'NO_SHOW': 'bg-gray-100 text-gray-800',
      'NEXT_PATIENT_NOTIFIED': 'bg-purple-100 text-purple-800'
    };
    return statusClasses[this.appointment.status || ''] || 'bg-gray-100 text-gray-800';
  }

  canJoinVideo(): boolean {
    return this.appointment.status === 'CONFIRMED' || this.appointment.status === 'IN_PROGRESS';
  }

  canCancel(): boolean {
    return this.appointment.status === 'PENDING' || this.appointment.status === 'CONFIRMED';
  }

  canReschedule(): boolean {
    return this.appointment.status === 'PENDING' || this.appointment.status === 'CONFIRMED';
  }

  canComplete(): boolean {
    return this.appointment.status === 'IN_PROGRESS';
  }
}
