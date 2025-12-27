import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, UserCircle, Save, Mail, Phone } from 'lucide-angular';
import {
  PatientProfileService,
  PatientProfile,
  UpdatePatientProfileRequest,
} from '../../services/patient-profile.service';

@Component({
  selector: 'app-patient-profile',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class PatientProfileComponent implements OnInit {
  readonly UserCircle = UserCircle;
  readonly Save = Save;
  readonly Mail = Mail;
  readonly Phone = Phone;

  profile: PatientProfile | null = null;
  isLoading = false;
  isSaving = false;

  // Form fields
  profileForm = {
    fullName: '',
    phoneNumber: '',
    dateOfBirth: '',
    gender: '',
    bloodGroup: '',
    address: '',
    emergencyContact: '',
  };

  constructor(private patientProfileService: PatientProfileService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading = true;
    this.patientProfileService.getCurrentPatientProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.populateForm(profile);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load profile', error);
        this.isLoading = false;
      },
    });
  }

  populateForm(profile: PatientProfile): void {
    this.profileForm = {
      fullName: profile.user?.fullName || '',
      phoneNumber: profile.user?.phoneNumber || '',
      dateOfBirth: profile.dateOfBirth || '',
      gender: profile.gender || '',
      bloodGroup: profile.bloodGroup || '',
      address: profile.address || '',
      emergencyContact: profile.emergencyContact || '',
    };
  }

  saveProfile(): void {
    if (this.isSaving) return;

    this.isSaving = true;

    const updateRequest: UpdatePatientProfileRequest = {
      fullName: this.profileForm.fullName,
      phoneNumber: this.profileForm.phoneNumber,
      dateOfBirth: this.profileForm.dateOfBirth || undefined,
      gender: this.profileForm.gender || undefined,
      bloodGroup: this.profileForm.bloodGroup || undefined,
      address: this.profileForm.address || undefined,
      emergencyContact: this.profileForm.emergencyContact || undefined,
    };

    this.patientProfileService.updateCurrentPatientProfile(updateRequest).subscribe({
      next: (updatedProfile) => {
        this.profile = updatedProfile;
        this.populateForm(updatedProfile);
        this.isSaving = false;
        alert('Profile updated successfully!');
      },
      error: (error) => {
        console.error('Failed to update profile', error);
        this.isSaving = false;
        alert('Failed to update profile. Please try again.');
      },
    });
  }
}
