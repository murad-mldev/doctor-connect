import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, FlaskConical, Plus, Search, Edit, Trash2 } from 'lucide-angular';
import { LabTestService } from '../../services/lab-test.service';
import { LabTest } from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-lab-tests',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ModalComponent],
  templateUrl: './lab-tests.component.html',
  styleUrl: './lab-tests.component.scss'
})
export class LabTestsComponent implements OnInit {
  readonly FlaskConical = FlaskConical;
  readonly Plus = Plus;
  readonly Search = Search;
  readonly Edit = Edit;
  readonly Trash2 = Trash2;

  labTests: LabTest[] = [];
  searchQuery = '';
  isLoading = false;
  showModal = false;
  isEditMode = false;

  currentLabTest: LabTest = {
    name: '',
    description: '',
    isActive: true
  };

  constructor(private labTestService: LabTestService) {}

  ngOnInit(): void {
    this.loadLabTests();
  }

  loadLabTests(): void {
    this.isLoading = true;
    this.labTestService.searchLabTests(this.searchQuery).subscribe({
      next: (response) => {
        this.labTests = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load lab tests', error);
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.loadLabTests();
  }

  addLabTest(): void {
    this.isEditMode = false;
    this.currentLabTest = {
      name: '',
      description: '',
      isActive: true
    };
    this.showModal = true;
  }

  editLabTest(labTest: LabTest): void {
    this.isEditMode = true;
    this.currentLabTest = { ...labTest };
    this.showModal = true;
  }

  saveLabTest(): void {
    if (!this.currentLabTest.name.trim()) {
      alert('Lab test name is required');
      return;
    }

    if (this.isEditMode && this.currentLabTest.id) {
      this.labTestService.updateLabTest(this.currentLabTest.id, this.currentLabTest).subscribe({
        next: () => {
          this.loadLabTests();
          this.closeModal();
        },
        error: (error) => console.error('Failed to update lab test', error)
      });
    } else {
      this.labTestService.createLabTest(this.currentLabTest).subscribe({
        next: () => {
          this.loadLabTests();
          this.closeModal();
        },
        error: (error) => console.error('Failed to create lab test', error)
      });
    }
  }

  deleteLabTest(labTest: LabTest): void {
    if (!labTest.id) return;

    if (confirm(`Are you sure you want to delete ${labTest.name}?`)) {
      this.labTestService.deleteLabTest(labTest.id).subscribe({
        next: () => {
          this.loadLabTests();
        },
        error: (error) => console.error('Failed to delete lab test', error)
      });
    }
  }

  closeModal(): void {
    this.showModal = false;
  }
}
