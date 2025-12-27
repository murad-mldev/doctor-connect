import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../services';
import { CreateAppointmentPaymentRequest } from '../../models';

@Component({
  selector: 'app-stripe-payment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="bg-white rounded-lg shadow-md p-6">
      <h3 class="text-lg font-semibold text-gray-900 mb-4">Payment Details</h3>

      <div *ngIf="errorMessage" class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
        {{ errorMessage }}
      </div>

      <div *ngIf="successMessage" class="mb-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded">
        {{ successMessage }}
      </div>

      <div class="space-y-4">
        <div class="bg-gray-50 p-4 rounded-lg">
          <div class="flex justify-between text-sm">
            <span class="text-gray-600">Consultation Fee:</span>
            <span class="font-medium">৳{{ amount }}</span>
          </div>
          <div class="flex justify-between text-sm mt-2">
            <span class="text-gray-600">Platform Fee (10%):</span>
            <span class="font-medium">৳{{ platformFee }}</span>
          </div>
          <div class="border-t border-gray-300 mt-2 pt-2">
            <div class="flex justify-between">
              <span class="font-semibold text-gray-900">Total:</span>
              <span class="font-bold text-blue-600">৳{{ amount }}</span>
            </div>
          </div>
        </div>

        <div *ngIf="!paymentProcessing && !paymentSuccess">
          <button
            (click)="initiateStripePayment()"
            [disabled]="loading"
            class="w-full px-4 py-3 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 disabled:bg-gray-400">
            <span *ngIf="!loading">Pay with Stripe</span>
            <span *ngIf="loading">Processing...</span>
          </button>
        </div>

        <div *ngIf="paymentProcessing" class="text-center py-4">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p class="mt-2 text-sm text-gray-600">Processing payment...</p>
        </div>

        <div *ngIf="paymentSuccess" class="text-center py-4">
          <svg class="mx-auto h-12 w-12 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          <p class="mt-2 text-lg font-medium text-green-600">Payment Successful!</p>
        </div>

        <div class="text-center">
          <img src="https://stripe.com/img/v3/home/social.png" alt="Secured by Stripe" class="h-8 mx-auto opacity-50">
          <p class="text-xs text-gray-500 mt-2">Payments are securely processed by Stripe</p>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class StripePaymentComponent implements OnInit {
  @Input() appointmentId!: string;
  @Input() amount!: number;

  platformFee = 0;
  loading = false;
  paymentProcessing = false;
  paymentSuccess = false;
  errorMessage = '';
  successMessage = '';
  clientSecret = '';

  constructor(private paymentService: PaymentService) {}

  ngOnInit(): void {
    this.calculateFees();
  }

  calculateFees(): void {
    this.platformFee = Math.round(this.amount * 0.1 * 100) / 100;
  }

  initiateStripePayment(): void {
    this.loading = true;
    this.errorMessage = '';

    const request: CreateAppointmentPaymentRequest = {
      appointmentId: this.appointmentId,
      amount: this.amount,
      currency: 'usd',
      commissionRate: 0.10
    };

    this.paymentService.createAppointmentPaymentWithStripe(request).subscribe({
      next: (response) => {
        this.clientSecret = response.clientSecret;
        this.loading = false;
        this.paymentProcessing = true;

        // In a real implementation, you would load Stripe.js and handle the payment
        // For now, we'll simulate a successful payment
        setTimeout(() => {
          this.confirmPayment(response.paymentIntentId);
        }, 2000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to initiate payment';
        this.loading = false;
      }
    });
  }

  confirmPayment(paymentIntentId: string): void {
    this.paymentService.confirmStripePayment(paymentIntentId).subscribe({
      next: (response) => {
        this.paymentProcessing = false;
        this.paymentSuccess = true;
        this.successMessage = 'Payment completed successfully!';
      },
      error: (error) => {
        this.paymentProcessing = false;
        this.errorMessage = 'Payment confirmation failed';
      }
    });
  }
}
