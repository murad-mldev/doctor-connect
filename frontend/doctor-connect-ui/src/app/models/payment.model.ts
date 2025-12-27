import { PaymentMethod } from './payment-method.enum';
import { PaymentStatus } from './payment-status.enum';

export interface Payment {
  id?: string;
  appointmentId?: string;
  patientId?: string;
  amount: number;
  method: PaymentMethod;
  status?: PaymentStatus;
  transactionId?: string;
  paymentDetails?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreatePaymentRequest {
  appointmentId: string;
  amount: number;
  method: PaymentMethod;
  transactionId?: string;
  paymentDetails?: string;
}

export interface ConfirmPaymentRequest {
  transactionId: string;
}

export interface UpdatePaymentStatusRequest {
  status: PaymentStatus;
}

// Stripe-specific interfaces
export interface StripeConfig {
  publishableKey: string;
}

export interface CreateStripePaymentIntentRequest {
  amount: number;
  currency?: string;
  appointmentId?: string;
  patientId?: string;
}

export interface StripePaymentIntentResponse {
  clientSecret: string;
  paymentIntentId: string;
  status: string;
  amount?: number;
  currency?: string;
  metadata?: Record<string, string>;
}

export interface ConfirmStripePaymentResponse {
  paymentIntentId: string;
  status: string;
  amount: number;
}

export interface RefundStripePaymentRequest {
  amount?: number;
}

export interface RefundStripePaymentResponse {
  refundId: string;
  status: string;
  amount: number;
}

export interface CreateAppointmentPaymentRequest {
  amount: number;
  appointmentId: string;
  currency?: string;
  commissionRate?: number;
}

export interface CreateAppointmentPaymentResponse {
  clientSecret: string;
  paymentIntentId: string;
  status: string;
  amount: number;
  platformFee: number;
  doctorReceives: number;
  doctorStripeAccountId: string;
}
