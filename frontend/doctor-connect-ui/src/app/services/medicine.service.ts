import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Medicine, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class MedicineService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  createMedicine(medicine: Medicine): Observable<Medicine> {
    return this.http.post<Medicine>(`${this.API_URL}/admin/medicines`, medicine);
  }

  getMedicineById(medicineId: string): Observable<Medicine> {
    return this.http.get<Medicine>(`${this.API_URL}/admin/medicines/${medicineId}`);
  }

  getAllActiveMedicines(): Observable<Medicine[]> {
    return this.http.get<Medicine[]>(`${this.API_URL}/admin/medicines/active`);
  }

  searchMedicines(name: string = '', page: number = 0, limit: number = 20): Observable<PaginatedResponse<Medicine>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<Medicine>>(`${this.API_URL}/admin/medicines`, { params });
  }

  updateMedicine(medicineId: string, medicine: Medicine): Observable<Medicine> {
    return this.http.put<Medicine>(`${this.API_URL}/admin/medicines/${medicineId}`, medicine);
  }

  deleteMedicine(medicineId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/admin/medicines/${medicineId}`);
  }
}
