import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideAngularModule,
  CheckCircle,
  XCircle,
  User,
  Eye,
} from 'lucide-angular';
import { AdminService } from '../../services/admin-stat.service';
import { DoctorProfile } from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-doctor-verification',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, ModalComponent],
  templateUrl: './doctor-verification.component.html',
  styleUrl: './doctor-verification.component.scss',
})
export class DoctorVerificationComponent implements OnInit {
  readonly User = User;
  readonly CheckCircle = CheckCircle;
  readonly XCircle = XCircle;
  readonly Eye = Eye;

  pendingDoctors: DoctorProfile[] = [];
  isLoading = false;
  showDetailsModal = false;
  selectedDoctor: DoctorProfile | null = null;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadPendingDoctors();
  }

  loadPendingDoctors(): void {
    this.isLoading = true;
    this.adminService.getPendingDoctorVerifications().subscribe({
      next: (response) => {
        this.pendingDoctors = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load pending doctors', error);
        this.isLoading = false;
      },
    });
  }

  viewDetails(doctor: DoctorProfile): void {
    this.selectedDoctor = doctor;
    this.showDetailsModal = true;
  }

  verifyDoctor(doctorId: string): void {
    if (confirm('Are you sure you want to verify this doctor?')) {
      this.adminService.verifyDoctor(doctorId).subscribe({
        next: () => {
          this.loadPendingDoctors();
          this.closeModal();
        },
        error: (error) => console.error('Failed to verify doctor', error),
      });
    }
  }

  rejectDoctor(doctorId: string): void {
    if (confirm('Are you sure you want to reject this doctor?')) {
      this.adminService.rejectDoctor(doctorId).subscribe({
        next: () => {
          this.loadPendingDoctors();
          this.closeModal();
        },
        error: (error) => console.error('Failed to reject doctor', error),
      });
    }
  }

  closeModal(): void {
    this.showDetailsModal = false;
    this.selectedDoctor = null;
  }
}
