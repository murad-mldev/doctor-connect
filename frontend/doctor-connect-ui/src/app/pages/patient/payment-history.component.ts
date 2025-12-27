import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideAngularModule,
  CreditCard,
  Download,
  CheckCircle,
  Clock,
  XCircle,
} from 'lucide-angular';
import { PaymentService } from '../../services/payment.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-payment-history',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './payment-history.component.html',
  styleUrl: './payment-history.component.scss',
})
export class PaymentHistoryComponent implements OnInit {
  readonly CreditCard = CreditCard;
  readonly Download = Download;
  readonly CheckCircle = CheckCircle;
  readonly Clock = Clock;
  readonly XCircle = XCircle;

  payments: any[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(
    private paymentService: PaymentService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadPayments();
  }

  loadPayments(): void {
    this.isLoading = true;
    this.error = null;

    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        // Get all payments
        this.paymentService.getAllPayments().subscribe({
          next: (response) => {
            // Filter payments for current user
            this.payments = response.data.filter(
              (payment: any) =>
                payment.appointment?.patient?.user?.id === user.id
            );
            this.isLoading = false;
          },
          error: (error) => {
            this.error = 'Failed to load payment history';
            console.error('Error loading payments:', error);
            this.isLoading = false;
          },
        });
      },
      error: (error) => {
        this.error = 'Failed to load user information';
        console.error('Error loading user:', error);
        this.isLoading = false;
      },
    });
  }

  downloadReceipt(paymentId: string): void {
    console.log('Download receipt for payment:', paymentId);
    // TODO: Implement receipt download when backend provides endpoint
    alert('Receipt download functionality will be implemented soon');
  }

  getStatusIcon(status: string): any {
    const iconMap: { [key: string]: any } = {
      COMPLETED: this.CheckCircle,
      PENDING: this.Clock,
      FAILED: this.XCircle,
      REFUNDED: this.CreditCard,
    };
    return iconMap[status] || this.Clock;
  }

  getStatusClass(status: string): string {
    const classMap: { [key: string]: string } = {
      COMPLETED: 'status-success',
      PENDING: 'status-warning',
      FAILED: 'status-error',
      REFUNDED: 'status-info',
    };
    return classMap[status] || 'status-default';
  }
}
