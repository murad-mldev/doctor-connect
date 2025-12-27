import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorProfile, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class PublicService {
  private readonly API_URL = 'http://localhost:8000/api/v1/public';

  constructor(private http: HttpClient) {}

  /**
   * Get all available doctors (public - no auth required)
   */
  getAvailableDoctors(
    departmentId?: string,
    specialization?: string,
    name?: string,
    page: number = 0,
    limit: number = 20
  ): Observable<PaginatedResponse<DoctorProfile>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (departmentId) params = params.set('departmentId', departmentId);
    if (specialization) params = params.set('specialization', specialization);
    if (name) params = params.set('name', name);

    return this.http.get<PaginatedResponse<DoctorProfile>>(`${this.API_URL}/doctors`, { params });
  }

  /**
   * Get single doctor profile by ID (public - no auth required)
   */
  getDoctorById(doctorId: string): Observable<DoctorProfile> {
    return this.http.get<DoctorProfile>(`${this.API_URL}/doctors/${doctorId}`);
  }
}
