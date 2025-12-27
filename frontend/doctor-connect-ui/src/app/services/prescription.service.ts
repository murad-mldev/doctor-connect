import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Prescription, CreatePrescriptionRequest, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class PrescriptionService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  createPrescription(appointmentId: string, request: CreatePrescriptionRequest): Observable<Prescription> {
    return this.http.post<Prescription>(`${this.API_URL}/appointments/${appointmentId}/prescriptions`, request);
  }

  getPrescriptionById(prescriptionId: string): Observable<Prescription> {
    return this.http.get<Prescription>(`${this.API_URL}/prescriptions/${prescriptionId}`);
  }

  getPatientPrescriptions(patientId: string, page: number = 0, limit: number = 20): Observable<PaginatedResponse<Prescription>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<Prescription>>(`${this.API_URL}/patients/${patientId}/prescriptions`, { params });
  }
}
