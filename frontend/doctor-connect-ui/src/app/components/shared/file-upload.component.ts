import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Upload, X, File } from 'lucide-angular';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './file-upload.component.html',
  styleUrl: './file-upload.component.scss'
})
export class FileUploadComponent {
  @Input() accept: string = '*/*';
  @Input() multiple: boolean = false;
  @Input() maxSizeMB: number = 10;
  @Input() label: string = 'Upload File';
  @Output() filesSelected = new EventEmitter<File[]>();
  @Output() error = new EventEmitter<string>();

  readonly Upload = Upload;
  readonly X = X;
  readonly File = File;

  isDragging = false;
  selectedFiles: File[] = [];

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    const files = event.dataTransfer?.files;
    if (files) {
      this.handleFiles(Array.from(files));
    }
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(Array.from(input.files));
    }
  }

  handleFiles(files: File[]): void {
    const validFiles: File[] = [];
    const maxSizeBytes = this.maxSizeMB * 1024 * 1024;

    for (const file of files) {
      if (file.size > maxSizeBytes) {
        this.error.emit(`File ${file.name} exceeds maximum size of ${this.maxSizeMB}MB`);
        continue;
      }
      validFiles.push(file);
    }

    if (validFiles.length > 0) {
      if (this.multiple) {
        this.selectedFiles = [...this.selectedFiles, ...validFiles];
      } else {
        this.selectedFiles = [validFiles[0]];
      }
      this.filesSelected.emit(this.selectedFiles);
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.filesSelected.emit(this.selectedFiles);
  }

  getFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  }

  clearFiles(): void {
    this.selectedFiles = [];
    this.filesSelected.emit([]);
  }
}
