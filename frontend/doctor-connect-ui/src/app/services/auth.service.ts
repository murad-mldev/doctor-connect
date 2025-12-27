import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthRequest, AuthResponse, AdminLoginRequest, User } from '../models';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  register(user: User): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/auth/register`, user);
  }

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.API_URL}/auth/login`,
      credentials,
      { withCredentials: true }
    );
  }

  logout(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.API_URL}/auth/logout`,
      {},
      { withCredentials: true }
    );
  }

  adminLogin(credentials: AdminLoginRequest): Observable<any> {
    return this.http.post(
      `${this.API_URL}/admin/login`,
      credentials,
      { responseType: 'text', withCredentials: true }
    );
  }

  adminLogout(): Observable<string> {
    return this.http.post(
      `${this.API_URL}/admin/logout`,
      {},
      { responseType: 'text', withCredentials: true }
    );
  }
}
