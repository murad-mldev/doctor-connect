import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Role, CreateRoleRequest, UpdateRoleRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  createRole(request: CreateRoleRequest): Observable<Role> {
    return this.http.post<Role>(`${this.API_URL}/roles`, request, {
      withCredentials: true
    });
  }

  updateRole(roleId: string, request: UpdateRoleRequest): Observable<Role> {
    return this.http.put<Role>(`${this.API_URL}/roles/${roleId}`, request, {
      withCredentials: true
    });
  }

  deleteRole(roleId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/roles/${roleId}`, {
      withCredentials: true
    });
  }

  getRoleById(roleId: string): Observable<Role> {
    return this.http.get<Role>(`${this.API_URL}/roles/${roleId}`, {
      withCredentials: true
    });
  }

  getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.API_URL}/roles`, {
      withCredentials: true
    });
  }
}
