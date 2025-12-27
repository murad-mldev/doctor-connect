import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorProfile, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  search(query: string, type: string = 'doctor', page: number = 0, limit: number = 20): Observable<PaginatedResponse<DoctorProfile>> {
    const params = new HttpParams()
      .set('q', query)
      .set('type', type)
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<DoctorProfile>>(`${this.API_URL}/search`, { params });
  }
}
