import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ScheduleSlot, CreateScheduleSlotRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  getDoctorSchedules(doctorId: string, date?: string): Observable<ScheduleSlot[]> {
    let params = new HttpParams();
    if (date) params = params.set('date', date);

    return this.http.get<ScheduleSlot[]>(`${this.API_URL}/doctors/${doctorId}/schedules`, { params });
  }

  createScheduleSlot(doctorId: string, request: CreateScheduleSlotRequest): Observable<ScheduleSlot> {
    return this.http.post<ScheduleSlot>(`${this.API_URL}/doctors/${doctorId}/schedules`, request);
  }

  updateScheduleSlot(doctorId: string, slotId: string, request: CreateScheduleSlotRequest): Observable<ScheduleSlot> {
    return this.http.put<ScheduleSlot>(`${this.API_URL}/doctors/${doctorId}/schedules/${slotId}`, request);
  }

  deleteScheduleSlot(doctorId: string, slotId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/doctors/${doctorId}/schedules/${slotId}`);
  }

  // Public endpoint - Get available (not fully booked) schedule slots
  getAvailableSchedules(doctorId: string, date?: string): Observable<ScheduleSlot[]> {
    let params = new HttpParams();
    if (date) params = params.set('date', date);

    return this.http.get<ScheduleSlot[]>(`${this.API_URL}/doctors/${doctorId}/schedules/available`, { params });
  }
}
