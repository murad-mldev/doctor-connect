import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  UserCircle,
  Save,
  ExternalLink,
  RefreshCw,
  CheckCircle,
  XCircle,
} from 'lucide-angular';
import { DoctorProfileService } from '../../services';
import { DoctorProfile, UpdateProfileRequest } from '../../models';

@Component({
  selector: 'app-doctor-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class DoctorProfileComponent implements OnInit {
  readonly UserCircle = UserCircle;
  readonly Save = Save;
  readonly ExternalLink = ExternalLink;
  readonly RefreshCw = RefreshCw;
  readonly CheckCircle = CheckCircle;
  readonly XCircle = XCircle;

  profile: DoctorProfile | null = null;
  isLoading = false;
  isSaving = false;

  // Profile form
  profileForm: UpdateProfileRequest = {
    fullName: '',
    phone: '',
    address: '',
    dateOfBirth: '',
    gender: '',
  };

  // Stripe Connect
  stripeAccountStatus: any = null;
  isLoadingStripe = false;

  constructor(private doctorProfileService: DoctorProfileService) {}

  ngOnInit(): void {
    this.loadProfile();
    this.loadStripeStatus();
  }

  loadProfile(): void {
    this.isLoading = true;
    this.doctorProfileService.getCurrentDoctorProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.populateForm();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load profile', error);
        this.isLoading = false;
      },
    });
  }

  populateForm(): void {
    if (!this.profile?.user) return;

    this.profileForm = {
      fullName: this.profile.user.fullName || '',
      phone: this.profile.user.phone || '',
      address: '',
      dateOfBirth: '',
      gender: '',
    };
  }

  saveProfile(): void {
    this.isSaving = true;
    this.doctorProfileService
      .updateCurrentDoctorProfile(this.profileForm)
      .subscribe({
        next: (updatedProfile) => {
          this.profile = updatedProfile;
          this.isSaving = false;
          alert('Profile updated successfully');
        },
        error: (error) => {
          console.error('Failed to update profile', error);
          this.isSaving = false;
          alert('Failed to update profile');
        },
      });
  }

  // Stripe Connect Methods

  loadStripeStatus(): void {
    this.doctorProfileService.getStripeAccountStatus().subscribe({
      /*************  ✨ Windsurf Command ⭐  *************/
      /**
       * Callback to handle the response when the Stripe account status is loaded.
       * @param {StripeAccountStatusResponse} status - The Stripe account status response.
       */
      /*******  3d3fc29e-60db-4fc7-8878-61d0afd0e973  *******/
      next: (status) => {
        this.stripeAccountStatus = status;
      },
      error: (error) => {
        // Account might not exist yet
        console.log('Stripe account not found or not set up yet');
      },
    });
  }

  createStripeAccount(): void {
    if (!this.profile?.user?.email) return;

    this.isLoadingStripe = true;
    this.doctorProfileService
      .createStripeConnectAccount(this.profile.user.email)
      .subscribe({
        next: (response) => {
          alert(response.message || 'Stripe account created successfully');
          this.loadStripeStatus();
          this.isLoadingStripe = false;
        },
        error: (error) => {
          console.error('Failed to create Stripe account', error);
          this.isLoadingStripe = false;
          alert('Failed to create Stripe account');
        },
      });
  }

  completeStripeOnboarding(): void {
    const refreshUrl = window.location.href;
    const returnUrl = window.location.href;

    this.isLoadingStripe = true;
    this.doctorProfileService
      .getStripeOnboardingLink(refreshUrl, returnUrl)
      .subscribe({
        next: (response) => {
          // Redirect to Stripe onboarding
          window.location.href = response.url;
        },
        error: (error) => {
          console.error('Failed to get onboarding link', error);
          this.isLoadingStripe = false;
          alert('Failed to get onboarding link');
        },
      });
  }

  openStripeDashboard(): void {
    this.isLoadingStripe = true;
    this.doctorProfileService.getStripeDashboardLink().subscribe({
      next: (response) => {
        // Open in new tab
        window.open(response.url, '_blank');
        this.isLoadingStripe = false;
      },
      error: (error) => {
        console.error('Failed to get dashboard link', error);
        this.isLoadingStripe = false;
        alert('Failed to open Stripe dashboard');
      },
    });
  }

  refreshStripeStatus(): void {
    this.isLoadingStripe = true;
    this.doctorProfileService.refreshStripeAccountStatus().subscribe({
      next: (response) => {
        this.loadStripeStatus();
        this.loadProfile(); // Reload profile to get updated Stripe status
        this.isLoadingStripe = false;
        alert('Stripe status refreshed');
      },
      error: (error) => {
        console.error('Failed to refresh Stripe status', error);
        this.isLoadingStripe = false;
        alert('Failed to refresh Stripe status');
      },
    });
  }

  get hasStripeAccount(): boolean {
    return !!this.profile?.stripeConnectAccountId;
  }

  get isStripeOnboardingComplete(): boolean {
    return !!this.profile?.stripeOnboardingCompleted;
  }

  get canReceivePayments(): boolean {
    return (
      this.stripeAccountStatus.chargesEnabled &&
      this.stripeAccountStatus.payoutsEnabled
    );
  }
}
