import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PlatformEarningsSummary {
  totalRevenue?: number;
  totalPlatformCommission?: number;
  totalDoctorPayouts?: number;
  totalTransactions?: number;
  paymentMethod?: string;
}

export interface PaymentBreakdown {
  appointmentId?: string;
  doctorId?: string;
  doctorName?: string;
  patientId?: string;
  patientName?: string;
  totalAmount?: number;
  platformCommission?: number;
  doctorPayout?: number;
  paymentDate?: Date;
  status?: string;
}

export interface DoctorCommissionReport {
  doctorProfileId?: string;
  doctorName?: string;
  totalEarnings?: number;
  totalCommissionPaid?: number;
  netPayouts?: number;
  totalTransactions?: number;
  averageTransactionAmount?: number;
}

export interface DashboardStats {
  totalStripeRevenue?: number;
  totalStripePlatformCommission?: number;
  totalStripeDoctorPayouts?: number;
  totalStripeTransactions?: number;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PlatformEarningsService {
  private readonly API_URL = 'http://localhost:8000/api/v1/platform-earnings';

  constructor(private http: HttpClient) {}

  /**
   * Get Stripe platform earnings summary
   * GET /api/v1/platform-earnings/stripe/summary
   */
  getStripePlatformSummary(): Observable<PlatformEarningsSummary> {
    return this.http.get<PlatformEarningsSummary>(`${this.API_URL}/stripe/summary`, {
      withCredentials: true
    });
  }

  /**
   * Get Stripe earnings by date range
   * GET /api/v1/platform-earnings/stripe/by-date-range
   */
  getStripeEarningsByDateRange(startDate: string, endDate: string): Observable<PlatformEarningsSummary> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<PlatformEarningsSummary>(`${this.API_URL}/stripe/by-date-range`, {
      params,
      withCredentials: true
    });
  }

  /**
   * Get Stripe payment breakdown with commissions
   * GET /api/v1/platform-earnings/stripe/payment-breakdown
   */
  getStripePaymentBreakdown(startDate: string, endDate: string): Observable<PaymentBreakdown[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<PaymentBreakdown[]>(`${this.API_URL}/stripe/payment-breakdown`, {
      params,
      withCredentials: true
    });
  }

  /**
   * Get doctor's Stripe commission report
   * GET /api/v1/platform-earnings/stripe/doctor/{doctorProfileId}
   */
  getDoctorStripeCommissionReport(doctorProfileId: string): Observable<DoctorCommissionReport> {
    return this.http.get<DoctorCommissionReport>(`${this.API_URL}/stripe/doctor/${doctorProfileId}`, {
      withCredentials: true
    });
  }

  /**
   * Get total Stripe platform commission
   * GET /api/v1/platform-earnings/stripe/total-commission
   */
  getTotalStripeCommission(): Observable<{ totalPlatformCommission: number; paymentMethod: string }> {
    return this.http.get<{ totalPlatformCommission: number; paymentMethod: string }>(
      `${this.API_URL}/stripe/total-commission`,
      {
        withCredentials: true
      }
    );
  }

  /**
   * Get total doctor payouts via Stripe
   * GET /api/v1/platform-earnings/stripe/total-doctor-payouts
   */
  getTotalDoctorPayouts(): Observable<{ totalDoctorPayouts: number; paymentMethod: string }> {
    return this.http.get<{ totalDoctorPayouts: number; paymentMethod: string }>(
      `${this.API_URL}/stripe/total-doctor-payouts`,
      {
        withCredentials: true
      }
    );
  }

  /**
   * Get summary for all payment methods (Stripe, Cash, bKash, Card)
   * GET /api/v1/platform-earnings/all-methods/summary
   */
  getAllPaymentMethodsSummary(): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/all-methods/summary`, {
      withCredentials: true
    });
  }

  /**
   * Get dashboard stats for admin
   * GET /api/v1/platform-earnings/dashboard-stats
   */
  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.API_URL}/dashboard-stats`, {
      withCredentials: true
    });
  }
}
