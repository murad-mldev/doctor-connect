import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface QueuePosition {
  appointmentId?: string;
  position?: number;
  estimatedWaitTimeMinutes?: number;
  patientsAhead?: number;
  status?: string;
}

export interface JoinConsultationResponse {
  appointmentId: string;
  twilioRoomName: string;
  accessToken: string;
  doctorName: string;
}

export interface DoctorQueue {
  currentPatient?: any;
  waitingPatients?: any[];
  totalWaiting?: number;
  averageConsultationTime?: number;
}

export interface StartConsultationResponse {
  appointmentId: string;
  twilioRoomName: string;
  twilioRoomSid: string;
  accessToken: string;
  status: string;
  patientName: string;
}

export interface EndConsultationResponse {
  appointmentId: string;
  status: string;
  durationMinutes?: number;
  nextPatient?: {
    appointmentId: string;
    patientName: string;
    notificationSent: boolean;
  } | null;
}

export interface DoctorStats {
  totalConsultations?: number;
  averageDurationMinutes?: number;
  totalPatientsToday?: number;
  completedToday?: number;
  inProgressNow?: number;
  waitingNow?: number;
}

@Injectable({
  providedIn: 'root'
})
export class VideoQueueService {
  private readonly API_URL = 'http://localhost:8000/api/v1/video-queue';

  constructor(private http: HttpClient) {}

  // ============= PATIENT ENDPOINTS =============

  /**
   * Patient joins the waiting room for their appointment
   * POST /api/v1/video-queue/join-waiting-room/{appointmentId}
   */
  joinWaitingRoom(appointmentId: string): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/join-waiting-room/${appointmentId}`, null, {
      withCredentials: true
    });
  }

  /**
   * Get patient's position in the queue
   * GET /api/v1/video-queue/my-position/{appointmentId}
   */
  getMyQueuePosition(appointmentId: string): Observable<QueuePosition> {
    return this.http.get<QueuePosition>(`${this.API_URL}/my-position/${appointmentId}`, {
      withCredentials: true
    });
  }

  /**
   * Patient joins the consultation (when it's their turn)
   * POST /api/v1/video-queue/join-consultation/{appointmentId}
   */
  joinConsultation(appointmentId: string): Observable<JoinConsultationResponse> {
    return this.http.post<JoinConsultationResponse>(
      `${this.API_URL}/join-consultation/${appointmentId}`,
      null,
      {
        withCredentials: true
      }
    );
  }

  // ============= DOCTOR ENDPOINTS =============

  /**
   * Get doctor's current queue
   * GET /api/v1/video-queue/my-queue
   */
  getMyQueue(): Observable<DoctorQueue> {
    return this.http.get<DoctorQueue>(`${this.API_URL}/my-queue`, {
      withCredentials: true
    });
  }

  /**
   * Get list of waiting patients for doctor
   * GET /api/v1/video-queue/waiting-patients
   */
  getWaitingPatients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/waiting-patients`, {
      withCredentials: true
    });
  }

  /**
   * Doctor starts a consultation
   * POST /api/v1/video-queue/start-consultation/{appointmentId}
   */
  startConsultation(appointmentId: string): Observable<StartConsultationResponse> {
    return this.http.post<StartConsultationResponse>(
      `${this.API_URL}/start-consultation/${appointmentId}`,
      null,
      {
        withCredentials: true
      }
    );
  }

  /**
   * Doctor ends a consultation
   * POST /api/v1/video-queue/end-consultation/{appointmentId}
   */
  endConsultation(appointmentId: string): Observable<EndConsultationResponse> {
    return this.http.post<EndConsultationResponse>(
      `${this.API_URL}/end-consultation/${appointmentId}`,
      null,
      {
        withCredentials: true
      }
    );
  }

  /**
   * Manually notify next patient in queue
   * POST /api/v1/video-queue/notify-next-patient
   */
  notifyNextPatient(): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.API_URL}/notify-next-patient`, null, {
      withCredentials: true
    });
  }

  // ============= ANALYTICS ENDPOINTS =============

  /**
   * Get doctor's consultation statistics
   * GET /api/v1/video-queue/analytics/doctor-stats
   */
  getDoctorStats(): Observable<DoctorStats> {
    return this.http.get<DoctorStats>(`${this.API_URL}/analytics/doctor-stats`, {
      withCredentials: true
    });
  }
}
