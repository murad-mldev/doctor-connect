import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Search, MapPin, Calendar, Star } from 'lucide-angular';
import { PublicService } from '../../services';
import { DoctorProfile } from '../../models';

@Component({
  selector: 'app-browse-doctors',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, LucideAngularModule],
  templateUrl: './browse-doctors.component.html',
  styleUrl: './browse-doctors.component.scss',
})
export class BrowseDoctorsComponent implements OnInit {
  readonly Search = Search;
  readonly MapPin = MapPin;
  readonly Calendar = Calendar;
  readonly Star = Star;

  doctors: DoctorProfile[] = [];
  isLoading = false;

  // Filters
  searchName = '';
  searchSpecialization = '';

  // Pagination
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 12;

  constructor(
    private publicService: PublicService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDoctors();
  }

  loadDoctors(): void {
    this.isLoading = true;
    this.publicService.getAvailableDoctors(
      undefined,
      this.searchSpecialization,
      this.searchName,
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (response) => {
        this.doctors = response.data;
        this.totalPages = response.meta.totalPages;
        this.totalElements = response.meta.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load doctors', error);
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadDoctors();
  }

  clearFilters(): void {
    this.searchName = '';
    this.searchSpecialization = '';
    this.currentPage = 0;
    this.loadDoctors();
  }

  viewDoctorDetails(doctorId: string): void {
    this.router.navigate(['/doctors', doctorId]);
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadDoctors();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadDoctors();
    }
  }
}
