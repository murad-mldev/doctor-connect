import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileStore, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  uploadPatientReport(patientId: string, file: File): Observable<FileStore> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<FileStore>(`${this.API_URL}/patients/${patientId}/reports`, formData);
  }

  uploadDoctorCredentials(userId: string, file: File): Observable<FileStore> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<FileStore>(`${this.API_URL}/users/${userId}/credentials`, formData);
  }

  downloadReport(fileId: string): Observable<Blob> {
    return this.http.get(`${this.API_URL}/reports/${fileId}`, { responseType: 'blob' });
  }

  getPatientReports(patientId: string, page: number = 0, limit: number = 20): Observable<PaginatedResponse<FileStore>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<FileStore>>(`${this.API_URL}/patients/${patientId}/reports`, { params });
  }
}
