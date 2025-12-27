import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Department } from '../models';

@Injectable({
  providedIn: 'root'
})
export class MetaService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  getDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(`${this.API_URL}/meta/departments`);
  }

  createDepartment(department: Department): Observable<Department> {
    return this.http.post<Department>(`${this.API_URL}/meta/departments`, department);
  }
}
