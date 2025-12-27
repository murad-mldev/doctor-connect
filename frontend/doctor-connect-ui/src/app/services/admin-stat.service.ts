import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorProfile, AdminStats, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  getPendingDoctorVerifications(
    page: number = 0,
    limit: number = 20
  ): Observable<PaginatedResponse<DoctorProfile>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<DoctorProfile>>(
      `${this.API_URL}/admin/doctors/pending`,
      { params }
    );
  }

  verifyDoctor(doctorId: string): Observable<DoctorProfile> {
    return this.http.post<DoctorProfile>(
      `${this.API_URL}/admin/doctors/${doctorId}/verify`,
      {}
    );
  }

  rejectDoctor(doctorId: string): Observable<DoctorProfile> {
    return this.http.post<DoctorProfile>(
      `${this.API_URL}/admin/doctors/${doctorId}/reject`,
      {}
    );
  }

  getSystemStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.API_URL}/admin/stats`);
  }
}
