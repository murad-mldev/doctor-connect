import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, FileText, Download, Trash2, AlertCircle } from 'lucide-angular';
import { FileUploadComponent } from '../../components/shared/file-upload.component';
import { FileService } from '../../services/file.service';
import { UserService } from '../../services/user.service';
import { FileStore } from '../../models';

@Component({
  selector: 'app-upload-reports',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FileUploadComponent],
  templateUrl: './upload-reports.component.html',
  styleUrl: './upload-reports.component.scss'
})
export class UploadReportsComponent implements OnInit {
  readonly FileText = FileText;
  readonly Download = Download;
  readonly Trash2 = Trash2;
  readonly AlertCircle = AlertCircle;

  reports: FileStore[] = [];
  isLoading = false;
  isUploading = false;
  error: string | null = null;
  successMessage: string | null = null;
  patientId: string | null = null;

  constructor(
    private fileService: FileService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.patientId = user.id || null;
        if (this.patientId) {
          this.loadReports();
        }
      },
      error: (error) => {
        this.error = 'Failed to load user information';
        console.error('Error loading user:', error);
      }
    });
  }

  loadReports(): void {
    if (!this.patientId) return;

    this.isLoading = true;
    this.error = null;

    this.fileService.getPatientReports(this.patientId).subscribe({
      next: (response) => {
        this.reports = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load reports';
        console.error('Error loading reports:', error);
        this.isLoading = false;
      }
    });
  }

  onFilesSelected(files: File[]): void {
    if (!this.patientId) {
      this.error = 'Patient ID not available';
      return;
    }

    if (files.length === 0) return;

    this.isUploading = true;
    this.error = null;
    this.successMessage = null;

    const uploadPromises = files.map(file =>
      this.fileService.uploadPatientReport(this.patientId!, file).toPromise()
    );

    Promise.all(uploadPromises)
      .then((results) => {
        this.successMessage = `Successfully uploaded ${files.length} file(s)`;
        this.isUploading = false;
        this.loadReports();

        setTimeout(() => {
          this.successMessage = null;
        }, 5000);
      })
      .catch((error) => {
        this.error = 'Failed to upload one or more files';
        console.error('Upload error:', error);
        this.isUploading = false;
      });
  }

  onUploadError(errorMessage: string): void {
    this.error = errorMessage;
    setTimeout(() => {
      this.error = null;
    }, 5000);
  }

  downloadReport(report: FileStore): void {
    this.fileService.downloadReport(report.id!).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = report.fileName || 'download';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        this.error = 'Failed to download report';
        console.error('Download error:', error);
      }
    });
  }

  deleteReport(reportId: string): void {
    if (!confirm('Are you sure you want to delete this report?')) {
      return;
    }

    // TODO: Add delete endpoint in FileService and backend
    this.error = 'Delete functionality coming soon';
  }

  getFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  }

  getFileIcon(fileType: any): typeof FileText {
    return FileText;
  }
}
