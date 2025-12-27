import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LabTest, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class LabTestService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  createLabTest(labTest: LabTest): Observable<LabTest> {
    return this.http.post<LabTest>(`${this.API_URL}/admin/lab-tests`, labTest);
  }

  getLabTestById(labTestId: string): Observable<LabTest> {
    return this.http.get<LabTest>(`${this.API_URL}/admin/lab-tests/${labTestId}`);
  }

  getAllActiveLabTests(): Observable<LabTest[]> {
    return this.http.get<LabTest[]>(`${this.API_URL}/admin/lab-tests/active`);
  }

  searchLabTests(name: string = '', page: number = 0, limit: number = 20): Observable<PaginatedResponse<LabTest>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<LabTest>>(`${this.API_URL}/admin/lab-tests`, { params });
  }

  updateLabTest(labTestId: string, labTest: LabTest): Observable<LabTest> {
    return this.http.put<LabTest>(`${this.API_URL}/admin/lab-tests/${labTestId}`, labTest);
  }

  deleteLabTest(labTestId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/admin/lab-tests/${labTestId}`);
  }
}
