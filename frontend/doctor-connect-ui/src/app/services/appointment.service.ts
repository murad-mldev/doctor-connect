import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Appointment,
  CreateAppointmentRequest,
  UpdateAppointmentStatusRequest,
  CancelAppointmentRequest,
  RescheduleAppointmentRequest,
  PaginatedResponse,
  AppointmentStatus
} from '../models';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  createAppointment(request: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.API_URL}/appointments`, request);
  }

  getAppointmentById(appointmentId: string): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.API_URL}/appointments/${appointmentId}`);
  }

  getUserAppointments(
    userId: string,
    status?: AppointmentStatus,
    from?: string,
    to?: string,
    page: number = 0,
    limit: number = 20
  ): Observable<PaginatedResponse<Appointment>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (status) params = params.set('status', status);
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);

    return this.http.get<PaginatedResponse<Appointment>>(`${this.API_URL}/users/${userId}/appointments`, { params });
  }

  updateAppointmentStatus(appointmentId: string, request: UpdateAppointmentStatusRequest): Observable<Appointment> {
    return this.http.patch<Appointment>(`${this.API_URL}/appointments/${appointmentId}/status`, request);
  }

  cancelAppointment(appointmentId: string, request: CancelAppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.API_URL}/appointments/${appointmentId}/cancel`, request);
  }

  rescheduleAppointment(appointmentId: string, request: RescheduleAppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.API_URL}/appointments/${appointmentId}/reschedule`, request);
  }
}
