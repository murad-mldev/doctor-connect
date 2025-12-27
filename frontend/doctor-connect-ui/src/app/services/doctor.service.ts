import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorProfile, PaginatedResponse, DoctorSearchResponse, DoctorAvailabilityResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class DoctorService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  searchDoctors(
    department?: string,
    specialization?: string,
    name?: string,
    available?: boolean,
    page: number = 0,
    limit: number = 20
  ): Observable<PaginatedResponse<DoctorProfile>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (department) params = params.set('department', department);
    if (specialization) params = params.set('specialization', specialization);
    if (name) params = params.set('name', name);
    if (available !== undefined) params = params.set('available', available.toString());

    return this.http.get<PaginatedResponse<DoctorProfile>>(`${this.API_URL}/doctors`, { params });
  }

  getDoctorById(doctorId: string): Observable<DoctorSearchResponse> {
    return this.http.get<DoctorSearchResponse>(`${this.API_URL}/doctors/${doctorId}`);
  }

  checkDoctorAvailability(doctorId: string): Observable<DoctorAvailabilityResponse> {
    return this.http.get<DoctorAvailabilityResponse>(`${this.API_URL}/doctors/${doctorId}/availability`);
  }
}
