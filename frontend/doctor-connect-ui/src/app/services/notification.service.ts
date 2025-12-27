import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification, PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  getUserNotifications(userId: string, page: number = 0, limit: number = 20): Observable<PaginatedResponse<Notification>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<Notification>>(`${this.API_URL}/notifications/user/${userId}`, { params });
  }

  getUnreadNotifications(userId: string, page: number = 0, limit: number = 20): Observable<PaginatedResponse<Notification>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<PaginatedResponse<Notification>>(`${this.API_URL}/notifications/user/${userId}/unread`, { params });
  }

  markAsRead(notificationId: string): Observable<Notification> {
    return this.http.patch<Notification>(`${this.API_URL}/notifications/${notificationId}/read`, {});
  }

  getUnreadCount(userId: string): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.API_URL}/notifications/user/${userId}/unread-count`);
  }
}
