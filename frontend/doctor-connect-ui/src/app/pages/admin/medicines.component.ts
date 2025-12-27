import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Pill, Plus, Search, Edit, Trash2 } from 'lucide-angular';
import { MedicineService } from '../../services/medicine.service';
import { Medicine } from '../../models';
import { ModalComponent } from '../../components/shared/modal.component';

@Component({
  selector: 'app-medicines',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ModalComponent],
  templateUrl: './medicines.component.html',
  styleUrl: './medicines.component.scss'
})
export class MedicinesComponent implements OnInit {
  readonly Pill = Pill;
  readonly Plus = Plus;
  readonly Search = Search;
  readonly Edit = Edit;
  readonly Trash2 = Trash2;

  medicines: Medicine[] = [];
  searchQuery = '';
  isLoading = false;
  showModal = false;
  isEditMode = false;

  currentMedicine: Medicine = {
    name: '',
    dosageForm: '',
    manufacturer: '',
    description: '',
    isActive: true
  };

  constructor(private medicineService: MedicineService) {}

  ngOnInit(): void {
    this.loadMedicines();
  }

  loadMedicines(): void {
    this.isLoading = true;
    this.medicineService.searchMedicines(this.searchQuery).subscribe({
      next: (response) => {
        this.medicines = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load medicines', error);
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.loadMedicines();
  }

  addMedicine(): void {
    this.isEditMode = false;
    this.currentMedicine = {
      name: '',
      dosageForm: '',
      manufacturer: '',
      description: '',
      isActive: true
    };
    this.showModal = true;
  }

  editMedicine(medicine: Medicine): void {
    this.isEditMode = true;
    this.currentMedicine = { ...medicine };
    this.showModal = true;
  }

  saveMedicine(): void {
    if (!this.currentMedicine.name.trim()) {
      alert('Medicine name is required');
      return;
    }

    if (this.isEditMode && this.currentMedicine.id) {
      this.medicineService.updateMedicine(this.currentMedicine.id, this.currentMedicine).subscribe({
        next: () => {
          this.loadMedicines();
          this.closeModal();
        },
        error: (error) => console.error('Failed to update medicine', error)
      });
    } else {
      this.medicineService.createMedicine(this.currentMedicine).subscribe({
        next: () => {
          this.loadMedicines();
          this.closeModal();
        },
        error: (error) => console.error('Failed to create medicine', error)
      });
    }
  }

  deleteMedicine(medicine: Medicine): void {
    if (!medicine.id) return;

    if (confirm(`Are you sure you want to delete ${medicine.name}?`)) {
      this.medicineService.deleteMedicine(medicine.id).subscribe({
        next: () => {
          this.loadMedicines();
        },
        error: (error) => console.error('Failed to delete medicine', error)
      });
    }
  }

  closeModal(): void {
    this.showModal = false;
  }
}
