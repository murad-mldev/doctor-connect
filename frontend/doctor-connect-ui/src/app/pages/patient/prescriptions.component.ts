import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideAngularModule,
  FileText,
  Download,
  Eye,
  X,
} from 'lucide-angular';
import { PrescriptionService, UserService, FileService } from '../../services';
import { Prescription } from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-prescriptions',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, ModalComponent],
  templateUrl: './prescriptions.component.html',
  styleUrl: './prescriptions.component.scss',
})
export class PrescriptionsComponent implements OnInit {
  readonly FileText = FileText;
  readonly Download = Download;
  readonly Eye = Eye;
  readonly X = X;

  prescriptions: Prescription[] = [];
  loading = false;
  currentUserId = '';

  // View details modal
  showDetailsModal = false;
  selectedPrescription: Prescription | null = null;

  constructor(
    private prescriptionService: PrescriptionService,
    private userService: UserService,
    private fileService: FileService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.currentUserId = profile.id || '';
        this.loadPrescriptions();
      },
      error: (error) => console.error('Failed to load user profile', error),
    });
  }

  loadPrescriptions(): void {
    if (!this.currentUserId) return;

    this.loading = true;
    this.prescriptionService
      .getPatientPrescriptions(this.currentUserId)
      .subscribe({
        next: (response) => {
          this.prescriptions = response.data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Failed to load prescriptions', error);
          this.loading = false;
        },
      });
  }

  viewDetails(prescription: Prescription): void {
    this.selectedPrescription = prescription;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedPrescription = null;
  }

  downloadPDF(prescription: Prescription): void {
    if (prescription.pdfFileId) {
      this.fileService.downloadReport(prescription.pdfFileId).subscribe({
        next: (blob) => {
          // Create a blob URL and trigger download
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `prescription-${prescription.id}.pdf`;
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Error downloading prescription PDF:', error);
          alert('Failed to download prescription PDF. Please try again.');
        }
      });
    }
  }
}
