import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Payment,
  CreatePaymentRequest,
  ConfirmPaymentRequest,
  UpdatePaymentStatusRequest,
  StripeConfig,
  CreateStripePaymentIntentRequest,
  StripePaymentIntentResponse,
  ConfirmStripePaymentResponse,
  RefundStripePaymentRequest,
  RefundStripePaymentResponse,
  CreateAppointmentPaymentRequest,
  CreateAppointmentPaymentResponse,
  PaginatedResponse
} from '../models';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private readonly API_URL = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) {}

  // Standard payment methods
  createPayment(request: CreatePaymentRequest): Observable<Payment> {
    return this.http.post<Payment>(`${this.API_URL}/payments`, request);
  }

  getAllPayments(): Observable<PaginatedResponse<Payment>> {
    return this.http.get<PaginatedResponse<Payment>>(`${this.API_URL}/payments`, {
      withCredentials: true
    });
  }

  getPaymentById(paymentId: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.API_URL}/payments/${paymentId}`);
  }

  getPaymentByAppointmentId(appointmentId: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.API_URL}/payments/appointment/${appointmentId}`);
  }

  confirmPayment(paymentId: string, request: ConfirmPaymentRequest): Observable<Payment> {
    return this.http.post<Payment>(`${this.API_URL}/payments/${paymentId}/confirm`, request);
  }

  updatePaymentStatus(paymentId: string, request: UpdatePaymentStatusRequest): Observable<Payment> {
    return this.http.patch<Payment>(`${this.API_URL}/payments/${paymentId}/status`, request);
  }

  // Stripe-specific methods
  getStripeConfig(): Observable<StripeConfig> {
    return this.http.get<StripeConfig>(`${this.API_URL}/payments/stripe/config`);
  }

  createStripePaymentIntent(request: CreateStripePaymentIntentRequest): Observable<StripePaymentIntentResponse> {
    return this.http.post<StripePaymentIntentResponse>(`${this.API_URL}/payments/stripe/create-payment-intent`, request);
  }

  confirmStripePayment(paymentIntentId: string): Observable<ConfirmStripePaymentResponse> {
    return this.http.post<ConfirmStripePaymentResponse>(`${this.API_URL}/payments/stripe/confirm-payment/${paymentIntentId}`, {});
  }

  getStripePaymentIntent(paymentIntentId: string): Observable<StripePaymentIntentResponse> {
    return this.http.get<StripePaymentIntentResponse>(`${this.API_URL}/payments/stripe/payment-intent/${paymentIntentId}`);
  }

  refundStripePayment(paymentIntentId: string, request?: RefundStripePaymentRequest): Observable<RefundStripePaymentResponse> {
    return this.http.post<RefundStripePaymentResponse>(`${this.API_URL}/payments/stripe/refund/${paymentIntentId}`, request || {});
  }

  createAppointmentPaymentWithStripe(request: CreateAppointmentPaymentRequest): Observable<CreateAppointmentPaymentResponse> {
    return this.http.post<CreateAppointmentPaymentResponse>(`${this.API_URL}/payments/stripe/create-appointment-payment`, request);
  }
}
