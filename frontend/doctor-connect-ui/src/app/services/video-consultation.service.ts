import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VideoRoom, CreateVideoRoomRequest, GenerateJoinTokenRequest, GenerateJoinTokenResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class VideoConsultationService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  createVideoRoom(request: CreateVideoRoomRequest): Observable<VideoRoom> {
    return this.http.post<VideoRoom>(`${this.API_URL}/video/rooms`, request);
  }

  getVideoRoomByAppointment(appointmentId: string): Observable<VideoRoom> {
    return this.http.get<VideoRoom>(`${this.API_URL}/video/rooms/appointment/${appointmentId}`);
  }

  getVideoRoomById(roomId: string): Observable<VideoRoom> {
    return this.http.get<VideoRoom>(`${this.API_URL}/video/rooms/${roomId}`);
  }

  generateJoinToken(roomId: string, request: GenerateJoinTokenRequest): Observable<GenerateJoinTokenResponse> {
    return this.http.post<GenerateJoinTokenResponse>(`${this.API_URL}/video/rooms/${roomId}/token`, request);
  }

  endVideoRoom(roomId: string): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/video/rooms/${roomId}/end`, {});
  }
}
