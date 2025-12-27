import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  /**
   * Get all users (ADMIN only)
   * GET /api/v1/users
   */
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}/users`, {
      withCredentials: true,
    });
  }

  /**
   * Get current authenticated user
   * GET /api/v1/users/me
   */
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/users/me`, {
      withCredentials: true,
    });
  }

  /**
   * Get user by ID
   * GET /api/v1/users/{id}
   */
  getUserById(userId: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/users/${userId}`, {
      withCredentials: true,
    });
  }

  /**
   * Update current authenticated user
   * PATCH /api/v1/users/me
   */
  updateCurrentUser(updateRequest: Partial<User>): Observable<User> {
    return this.http.patch<User>(
      `${this.API_URL}/users/me`,
      updateRequest,
      {
        withCredentials: true,
      }
    );
  }

  /**
   * Update user by ID (ADMIN or self only)
   * PATCH /api/v1/users/{id}
   */
  updateUser(userId: string, updateRequest: Partial<User>): Observable<User> {
    return this.http.patch<User>(
      `${this.API_URL}/users/${userId}`,
      updateRequest,
      {
        withCredentials: true,
      }
    );
  }

  /**
   * Delete user by ID (ADMIN only)
   * DELETE /api/v1/users/{id}
   */
  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/users/${userId}`, {
      withCredentials: true,
    });
  }
}
