import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorProfile, UpdateProfileRequest } from '../models';

export interface StripeAccountResponse {
  accountId: string;
  status: string;
  message?: string;
}

export interface StripeOnboardingLinkResponse {
  url: string;
  expiresAt: number;
}

export interface StripeDashboardLinkResponse {
  url: string;
}

export interface StripeAccountStatusResponse {
  accountId: string;
  chargesEnabled: boolean;
  payoutsEnabled: boolean;
  detailsSubmitted: boolean;
}

export interface StripeRefreshStatusResponse {
  stripeAccountStatus: string;
  stripeOnboardingCompleted: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class DoctorProfileService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  // Profile Management
  getCurrentDoctorProfile(): Observable<DoctorProfile> {
    return this.http.get<DoctorProfile>(`${this.API_URL}/doctor-profile/me`);
  }

  getDoctorProfileByUserId(userId: string): Observable<DoctorProfile> {
    return this.http.get<DoctorProfile>(`${this.API_URL}/doctor-profile/${userId}`);
  }

  updateCurrentDoctorProfile(request: UpdateProfileRequest): Observable<DoctorProfile> {
    return this.http.patch<DoctorProfile>(`${this.API_URL}/doctor-profile/me`, request);
  }

  updateDoctorProfile(userId: string, request: UpdateProfileRequest): Observable<DoctorProfile> {
    return this.http.patch<DoctorProfile>(`${this.API_URL}/doctor-profile/${userId}`, request);
  }

  // Stripe Connect Methods
  createStripeConnectAccount(email?: string, country: string = 'US'): Observable<StripeAccountResponse> {
    const request = { email, country };
    return this.http.post<StripeAccountResponse>(`${this.API_URL}/doctor-profile/stripe/create-account`, request);
  }

  getStripeOnboardingLink(refreshUrl: string, returnUrl: string): Observable<StripeOnboardingLinkResponse> {
    const request = { refreshUrl, returnUrl };
    return this.http.post<StripeOnboardingLinkResponse>(`${this.API_URL}/doctor-profile/stripe/onboarding-link`, request);
  }

  getStripeDashboardLink(): Observable<StripeDashboardLinkResponse> {
    return this.http.get<StripeDashboardLinkResponse>(`${this.API_URL}/doctor-profile/stripe/dashboard-link`);
  }

  getStripeAccountStatus(): Observable<StripeAccountStatusResponse> {
    return this.http.get<StripeAccountStatusResponse>(`${this.API_URL}/doctor-profile/stripe/account-status`);
  }

  refreshStripeAccountStatus(): Observable<StripeRefreshStatusResponse> {
    return this.http.post<StripeRefreshStatusResponse>(`${this.API_URL}/doctor-profile/stripe/refresh-status`, {});
  }
}
