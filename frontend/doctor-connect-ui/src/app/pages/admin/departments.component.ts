import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Building2, Plus, Edit2, Trash2, X, Check } from 'lucide-angular';
import { MetaService } from '../../services/meta.service';

export interface Department {
  id?: string;
  name: string;
  description?: string;
}

@Component({
  selector: 'app-departments',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule],
  templateUrl: './departments.component.html',
  styleUrl: './departments.component.scss'
})
export class DepartmentsComponent implements OnInit {
  readonly Building2 = Building2;
  readonly Plus = Plus;
  readonly Edit2 = Edit2;
  readonly Trash2 = Trash2;
  readonly X = X;
  readonly Check = Check;

  departments: Department[] = [];
  isLoading = false;
  error: string | null = null;

  // Modal state
  showAddModal = false;
  showEditModal = false;
  showDeleteModal = false;

  // Form data
  newDepartment: Department = { name: '', description: '' };
  editingDepartment: Department | null = null;
  editDepartmentData: Department = { name: '', description: '' };
  deletingDepartment: Department | null = null;

  constructor(private metaService: MetaService) {}

  ngOnInit(): void {
    this.loadDepartments();
  }

  loadDepartments(): void {
    this.isLoading = true;
    this.error = null;

    this.metaService.getDepartments().subscribe({
      next: (departments) => {
        this.departments = departments;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load departments';
        console.error('Error loading departments:', error);
        this.isLoading = false;
      }
    });
  }

  openAddModal(): void {
    this.showAddModal = true;
    this.newDepartment = { name: '', description: '' };
  }

  closeAddModal(): void {
    this.showAddModal = false;
    this.newDepartment = { name: '', description: '' };
  }

  submitAddDepartment(): void {
    if (!this.newDepartment.name.trim()) {
      return;
    }

    this.metaService.createDepartment(this.newDepartment).subscribe({
      next: (department) => {
        this.departments.push(department);
        this.closeAddModal();
      },
      error: (error) => {
        this.error = 'Failed to create department';
        console.error('Error creating department:', error);
      }
    });
  }

  openEditModal(department: Department): void {
    this.editingDepartment = department;
    this.editDepartmentData = {
      name: department.name,
      description: department.description
    };
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingDepartment = null;
    this.editDepartmentData = { name: '', description: '' };
  }

  submitEditDepartment(): void {
    if (!this.editingDepartment || !this.editDepartmentData.name.trim()) {
      return;
    }

    // TODO: Add update endpoint in MetaService and backend
    this.error = 'Update functionality coming soon';
    console.log('Update department:', this.editDepartmentData);
  }

  openDeleteModal(department: Department): void {
    this.deletingDepartment = department;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.deletingDepartment = null;
  }

  confirmDelete(): void {
    if (!this.deletingDepartment) {
      return;
    }

    // TODO: Add delete endpoint in MetaService and backend
    this.error = 'Delete functionality coming soon';
    console.log('Delete department:', this.deletingDepartment);
  }
}
