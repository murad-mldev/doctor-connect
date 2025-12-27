import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Department } from '../../models';

@Component({
  selector: 'app-doctor-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './doctor-filter.component.html',
  styleUrl: './doctor-filter.component.scss',
})
export class DoctorFilterComponent implements OnInit {
  @Input() departments: Department[] = [];
  @Output() filterChange = new EventEmitter<any>();

  filters = {
    name: '',
    departmentId: '',
    location: '',
    specialization: '',
    minExperience: null,
    maxExperience: null,
    minFee: null,
    maxFee: null,
    verifiedOnly: false
  };

  ngOnInit(): void {
    // Initial filter emit
  }

  onFilterChange(): void {
    // Auto-apply on change (optional)
    // this.filterChange.emit(this.filters);
  }

  applyFilters(): void {
    this.filterChange.emit(this.filters);
  }

  resetFilters(): void {
    this.filters = {
      name: '',
      departmentId: '',
      location: '',
      specialization: '',
      minExperience: null,
      maxExperience: null,
      minFee: null,
      maxFee: null,
      verifiedOnly: false
    };
    this.filterChange.emit(this.filters);
  }
}
