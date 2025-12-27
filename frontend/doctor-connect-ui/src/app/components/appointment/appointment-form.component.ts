import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreateAppointmentRequest, ScheduleSlot } from '../../models';

@Component({
  selector: 'app-appointment-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './appointment-form.component.html',
  styleUrl: './appointment-form.component.scss',
})
export class AppointmentFormComponent implements OnInit {
  @Input() doctorId!: string;
  @Input() availableSlots: ScheduleSlot[] = [];
  @Input() consultationFee = 0;
  @Input() loading = false;
  @Input() errorMessage = '';

  @Output() submit = new EventEmitter<CreateAppointmentRequest>();
  @Output() cancel = new EventEmitter<void>();

  formData: CreateAppointmentRequest = {
    doctorId: '',
    slotId: '',
    patientId: '',
    reason: ''
  };

  appointmentType = 'VIDEO';

  ngOnInit(): void {
    this.formData.doctorId = this.doctorId;
  }

  onSubmit(): void {
    if (this.formData.slotId) {
      this.submit.emit(this.formData);
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
