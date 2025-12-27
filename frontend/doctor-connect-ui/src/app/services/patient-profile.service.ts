import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PatientProfile {
  id: string;
  user: {
    id: string;
    email: string;
    fullName: string;
    phoneNumber: string;
    roles: string[];
  };
  dateOfBirth?: string;
  gender?: string;
  bloodGroup?: string;
  address?: string;
  emergencyContact?: string;
  createdAt: string;
  updatedAt: string;
}

export interface UpdatePatientProfileRequest {
  fullName?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
  bloodGroup?: string;
  address?: string;
  emergencyContact?: string;
}

@Injectable({
  providedIn: 'root',
})
export class PatientProfileService {
  private readonly API_URL = 'http://localhost:8000/api/v1/patient-profile';

  constructor(private http: HttpClient) {}

  /**
   * Get current logged-in patient's profile
   * GET /api/v1/patient-profile/me
   */
  getCurrentPatientProfile(): Observable<PatientProfile> {
    return this.http.get<PatientProfile>(`${this.API_URL}/me`, {
      withCredentials: true
    });
  }

  /**
   * Create current patient's profile
   * POST /api/v1/patient-profile/me
   */
  createCurrentPatientProfile(
    request: UpdatePatientProfileRequest
  ): Observable<PatientProfile> {
    return this.http.post<PatientProfile>(`${this.API_URL}/me`, request, {
      withCredentials: true
    });
  }

  /**
   * Get patient profile by user ID
   * GET /api/v1/patient-profile/{userId}
   */
  getPatientProfileByUserId(userId: string): Observable<PatientProfile> {
    return this.http.get<PatientProfile>(`${this.API_URL}/${userId}`, {
      withCredentials: true
    });
  }

  /**
   * Update current patient's profile
   * PATCH /api/v1/patient-profile/me
   */
  updateCurrentPatientProfile(
    request: UpdatePatientProfileRequest
  ): Observable<PatientProfile> {
    return this.http.patch<PatientProfile>(`${this.API_URL}/me`, request, {
      withCredentials: true
    });
  }

  /**
   * Update patient profile by user ID (admin use)
   * PATCH /api/v1/patient-profile/{userId}
   */
  updatePatientProfile(
    userId: string,
    request: UpdatePatientProfileRequest
  ): Observable<PatientProfile> {
    return this.http.patch<PatientProfile>(`${this.API_URL}/${userId}`, request, {
      withCredentials: true
    });
  }
}
