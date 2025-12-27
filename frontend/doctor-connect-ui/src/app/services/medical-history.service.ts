import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MedicalHistory } from '../models';

@Injectable({
  providedIn: 'root'
})
export class MedicalHistoryService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  getPatientMedicalHistory(patientId: string): Observable<MedicalHistory> {
    return this.http.get<MedicalHistory>(`${this.API_URL}/patients/${patientId}/medical-history`);
  }
}
